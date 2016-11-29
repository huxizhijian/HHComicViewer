/*
 * Copyright 2016 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.hhcomicviewer2.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import org.huxizhijian.hhcomicviewer2.app.HHApplication;
import org.huxizhijian.hhcomicviewer2.db.ComicChapterDBHelper;
import org.huxizhijian.hhcomicviewer2.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer2.db.DownloadThreadDBHelper;
import org.huxizhijian.hhcomicviewer2.enities.Comic;
import org.huxizhijian.hhcomicviewer2.enities.ComicChapter;
import org.huxizhijian.hhcomicviewer2.enities.ThreadInfo;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.utils.NotificationUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 线程管理类
 * Created by wei on 2016/9/11.
 */
public class DownloadManager {

    private static volatile DownloadManager mDownloadManager; //单例模式
    private ComicChapterDBHelper mComicChapterDBHelper; //DB控制
    private DownloadThreadDBHelper mDownloadThreadDBHelper; //下载线程DB控制
    private LinkedList<ComicChapter> mComicChapterLinkedList; //待下载列表
    private ComicChapter mComicChapter; //正在下载的任务
    private List<DownloadThread> mThreadList; //下载线程集
    private List<ThreadInfo> mInfos; //线程信息集
    private boolean isPause = false;
    private int mDownloadPosition = 0;
    private int mThreadCount = 3; //默认线程的数量
    private Context mContext;
    private NotificationUtil mNotificationUtil; //通知管理类
    private OnMissionFinishedListener mOnMissionFinishedListener; //任务完成回调接口
    private OkHttpClient mClient; //OkHttpClient实例

    Handler mHandler = new Handler();

    private static final String TAG = "DownloadManager";

    //下载线程池
    public static ExecutorService sExecutorService =
            Executors.newCachedThreadPool();

    private DownloadManager(Context context) {
        mComicChapterDBHelper = ComicChapterDBHelper.getInstance(context);
        mDownloadThreadDBHelper = DownloadThreadDBHelper.getInstance(context);
        mComicChapterLinkedList = new LinkedList<>();
        this.mContext = context;
        this.mNotificationUtil = NotificationUtil.getInstance(mContext);
        //加载用户设置
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String threadCount = sharedPreferences.getString("download_thread_count", "3_thread");
        switch (threadCount) {
            case "1_thread":
                mThreadCount = 1;
                break;
            case "3_thread":
                mThreadCount = 3;
                break;
            case "5_thread":
                mThreadCount = 5;
                break;
        }
    }

    public static DownloadManager getInstance(Context context) {
        if (mDownloadManager == null) {
            synchronized (DownloadManager.class) {
                if (mDownloadManager == null) {
                    mDownloadManager = new DownloadManager(context);
                }
            }
        }
        return mDownloadManager;
    }

    public void startDownload(ComicChapter comicChapter) {
        if (comicChapter.getDownloadStatus() == Constants.DOWNLOAD_FINISHED) return;
        if (mComicChapter == null) {
            //如果没有在下载
            mComicChapterLinkedList.add(comicChapter);
            doNextDownload();
        } else {
            mComicChapterLinkedList.add(comicChapter);
        }
    }

    public void stopAllDownload() {
        //清空下载列表
        mComicChapterLinkedList.clear();
        if (mComicChapter != null) {
            //如果有线程正在下载，暂停线程
            setDownloadPause(mComicChapter);
        }
    }

    /**
     * 删除一个任务
     *
     * @param comicChapter
     */
    public void deleteChapter(ComicChapter comicChapter) {
        //取消这个任务
        setDownloadPause(comicChapter);
        if (mDownloadThreadDBHelper.findByChapterUrl(comicChapter.getChapterUrl()) != null) {
            //删除章节数据库信息
            mDownloadThreadDBHelper.deleteAllChapterThread(comicChapter.getChapterUrl());
        }

        //如果存在下载文件，则删除
        BaseUtils.deleteDirectory(comicChapter.getSavePath());

        //删除漫画的数据库信息
        mComicChapterDBHelper.delete(comicChapter);

        //如果该任务下的comic没有下载的章节了
        List<ComicChapter> chapters = mComicChapterDBHelper.findByComicUrl(comicChapter.getComicUrl());
        if (chapters == null || chapters.size() == 0) {
            ComicDBHelper comicDBHelper = ComicDBHelper.getInstance(mContext);
            Comic comic = comicDBHelper.findByUrl(comicChapter.getComicUrl());
            //将下载标记改为false
            comic.setDownload(false);
            comicDBHelper.update(comic);
            //删除上级目录
            BaseUtils.deleteDirectoryParent(comicChapter.getSavePath());
        }
    }

    /**
     * 暂停一个任务
     *
     * @param comicChapter
     */
    public void setDownloadPause(final ComicChapter comicChapter) {
        if (mComicChapter != null && mComicChapter.getChapterUrl().equals(comicChapter.getChapterUrl())) {
            //如果要暂停的刚好正在下载
            //暂停线程
            isPause = true;
            //等待5秒钟
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mComicChapter != null && isPause &&
                            mComicChapter.getChapterUrl().equals(comicChapter.getChapterUrl())) {
                        for (int i = 0; i < mThreadList.size(); i++) {
                            if (mThreadList.get(i).getState() == Thread.State.RUNNABLE) {
                                //如果线程还在进行，将其中断
                                mThreadList.get(i).interrupt();
                            }
                        }
                        mComicChapter.setDownloadPosition(mDownloadPosition);
                        mComicChapter.setDownloadStatus(Constants.DOWNLOAD_PAUSE);
                        //下载页数初始化
                        mDownloadPosition = 0;
                        mComicChapterDBHelper.update(mComicChapter);
                        //向activity发送广播通知下载任务暂停
                        Intent intent = new Intent(DownloadManagerService.ACTION_RECEIVER);
                        intent.putExtra("comicChapter", mComicChapter);
                        intent.putExtra("notification", "cancel");
                        mContext.sendBroadcast(intent);
                        mComicChapter = null;
                        //将标记改为false
                        isPause = false;
                        //开启下一个任务
                        doNextDownload();
                    }
                }
            }, 5000);
        } else if (mComicChapterLinkedList.contains(comicChapter)) {
            //如果存在待下载列表中
            //移除即可
            mComicChapterLinkedList.remove(comicChapter);
            comicChapter.setDownloadStatus(Constants.DOWNLOAD_PAUSE);
            mComicChapterDBHelper.update(comicChapter);
        }
    }

    //是否还有任务
    public boolean hasMission() {
        if (mComicChapter != null) {
            return true;
        } else {
            if (mComicChapterLinkedList.size() != 0) {
                return true;
            }
        }
        return false;
    }

    //检查任务是否在队列中
    public boolean isInQueue(ComicChapter comicChapter) {
        return mComicChapterLinkedList != null && mComicChapterLinkedList.size() != 0
                && mComicChapterLinkedList.contains(comicChapter);
    }

    /**
     * 继续下一个任务
     */
    private void doNextDownload() {
        if (mComicChapter != null) return;
        if (mComicChapterLinkedList.size() != 0) {
            //如果还有任务
            mComicChapter = mComicChapterLinkedList.removeFirst();
        } else {
            //所有任务结束
            //通知Service
            if (mOnMissionFinishedListener != null) {
                mOnMissionFinishedListener.onFinished();
            }
            return;
        }
        if (mThreadList == null) {
            mThreadList = new ArrayList<>();
        }
        mThreadList.clear();
        if (mDownloadPosition != 0) {
            //初始化下载进度
            mDownloadPosition = 0;
        }
        //开始下载
        //如果之前有进行过下载
        if (mComicChapter.getDownloadStatus() != Constants.DOWNLOAD_INIT) {
            //如果没有下载完毕
            mInfos = mDownloadThreadDBHelper.findByChapterUrl(mComicChapter.getChapterUrl());
            if (mInfos == null) {
                //没有进行过下载
                newDownloadThreadInfo();
            }
            for (ThreadInfo info : mInfos) {
                DownloadThread thread = new DownloadThread(info);
                sExecutorService.execute(thread);
                //添加线程到集合中
                Log.i(TAG, "doNextDownload: threadRestart");
                mThreadList.add(thread);
            }
            //更新下载状态在数据库中
            mComicChapter.setDownloadStatus(Constants.DOWNLOAD_DOWNLOADING);
            mComicChapterDBHelper.update(mComicChapter);
        } else {
            newDownloadThreadInfo();
        }
        //向activity发送广播通知下载任务开始
        Intent intent = new Intent(DownloadManagerService.ACTION_RECEIVER);
        intent.putExtra("notification", "show");
        intent.putExtra("comicChapter", mComicChapter);
        mContext.sendBroadcast(intent);
    }

    /**
     * 开启新的线程下载
     */
    private void newDownloadThreadInfo() {
        //更新数据库
        mComicChapter.setDownloadStatus(Constants.DOWNLOAD_START);
        mComicChapterDBHelper.update(mComicChapter);
        if (mComicChapter.getChapterName().length() > mThreadCount) {
            for (int i = 0; i < mThreadCount; i++) {
                ThreadInfo threadInfo = new ThreadInfo(i, mThreadCount, 0, -1, 0, mComicChapter.getChapterUrl());
                mDownloadThreadDBHelper.add(threadInfo);
            }
            mInfos = mDownloadThreadDBHelper.findByChapterUrl(mComicChapter.getChapterUrl());
            for (ThreadInfo info : mInfos) {
                DownloadThread thread = new DownloadThread(info);
                sExecutorService.execute(thread);
                //添加线程到集合中
                Log.i(TAG, "doNextDownload: threadStart");
                mThreadList.add(thread);
            }
            mComicChapter.setDownloadStatus(Constants.DOWNLOAD_DOWNLOADING);
            mComicChapterDBHelper.update(mComicChapter);
        } else {
            //如果并没有3页以上，转为单线程下载
            ThreadInfo threadInfo = new ThreadInfo(0, 1, 0, -1, 0, mComicChapter.getChapterUrl());
            mDownloadThreadDBHelper.add(threadInfo);
            mInfos = mDownloadThreadDBHelper.findByChapterUrl(mComicChapter.getChapterUrl());
            for (ThreadInfo info : mInfos) {
                DownloadThread thread = new DownloadThread(info);
                sExecutorService.execute(thread);
                //添加线程到集合中
                Log.i(TAG, "doNextDownload: threadStart");
                mThreadList.add(thread);
            }
            mComicChapter.setDownloadStatus(Constants.DOWNLOAD_DOWNLOADING);
            mComicChapterDBHelper.update(mComicChapter);
        }
    }

    /**
     * 确认是否所有线程已经暂停了，都暂停就做下一部操作
     *
     * @param info
     */
    private void checkAllThreadPause(ThreadInfo info) {
        mInfos.remove(info);
        //如果所有任务都移除了
        if (mInfos.size() == 0) {
            mInfos = null;
            mComicChapter.setDownloadPosition(mDownloadPosition);
            mComicChapter.setDownloadStatus(Constants.DOWNLOAD_PAUSE);
            //下载页数初始化
            mDownloadPosition = 0;
            mComicChapterDBHelper.update(mComicChapter);
            //向activity发送广播通知下载任务暂停
            Intent intent = new Intent(DownloadManagerService.ACTION_RECEIVER);
            intent.putExtra("comicChapter", mComicChapter);
            intent.putExtra("notification", "cancel");
            mContext.sendBroadcast(intent);
            mComicChapter = null;
            //将标记改为false
            isPause = false;
            //开启下一个任务
            doNextDownload();
        }
    }

    /**
     * 所有任务结束时回调的接口
     */
    public interface OnMissionFinishedListener {
        void onFinished();
    }

    public void setOnMissionFinishedListener(OnMissionFinishedListener onMissionFinishedListener) {
        mOnMissionFinishedListener = onMissionFinishedListener;
    }

    /**
     * 下载线程类
     */
    private class DownloadThread extends Thread {

        private ThreadInfo mThreadInfo;
        boolean isFinished = false; //标识线程是否下载完毕
        boolean isError = false; //标识下载线程是否都下载错误

        DownloadThread(ThreadInfo mThreadInfo) {
            this.mThreadInfo = mThreadInfo;
        }

        @Override
        public void run() {
            if (mComicChapter == null) return;
            RandomAccessFile raf = null;
            Response response = null;
            if (mDownloadThreadDBHelper.findByChapterUrl(mThreadInfo.getComicChapterUrl()) == null) {
                //如果没有该线程的数据库信息，保存该信息
                mDownloadThreadDBHelper.add(mThreadInfo);
            }
            //计算出线程要下载的页数
            int threadPageCount = mComicChapter.getPageCount() / mThreadInfo.getThreadCount();
            if (mThreadInfo.getThreadPosition() == mThreadInfo.getThreadCount() - 1) {
                threadPageCount = mComicChapter.getPageCount() - threadPageCount * (mThreadInfo.getThreadCount() - 1);
            }
            //初始化下载页数
            mDownloadPosition += mThreadInfo.getDownloadPosition();
            //如果文件夹不存在，创建写入文件夹
            File filePath = new File(mComicChapter.getSavePath());
            if (!filePath.exists()) {
                Log.i(TAG, "run: make dir " + filePath.getAbsolutePath());
                filePath.mkdirs();
            }
            try {
                while (mThreadInfo.getDownloadPosition() < threadPageCount) {
                    //下载页数增加
                    mDownloadPosition++;
                    mComicChapter.setDownloadPosition(mDownloadPosition);
                    mComicChapterDBHelper.updateProgress(mComicChapter);
                    //发送通知
                    mNotificationUtil.updateNotification(mComicChapter.getId(),
                            mComicChapter);

                    //向activity发送广播通知下载任务进行中
                    Intent intent = new Intent(DownloadManagerService.ACTION_RECEIVER);
                    intent.putExtra("comicChapter", mComicChapter);
                    mContext.sendBroadcast(intent);
                    Request.Builder builder = new Request.Builder().url(mComicChapter.getPicList().get(mThreadInfo.getThreadPosition() *
                            (mComicChapter.getPageCount() / mThreadInfo.getThreadCount()) +
                            mThreadInfo.getDownloadPosition()));
                    if (mClient == null)
                        mClient = ((HHApplication) mContext.getApplicationContext()).getClient();
                    //初始化文件长度
                    if (mThreadInfo.getLength() == -1) {
                        response = mClient.newCall(builder.build()).execute();
                        //从头开始下载
                        long length = -1;
                        //获得文件长度
                        if (response.isSuccessful()) {
                            length = response.body().contentLength();
                        }
                        if (length <= 0) {
                            Log.i(TAG, "run: 获取文件长度错误！");
                            throw new IOException();
                        }
                        File file = null;

                        file = new File(filePath, BaseUtils.getPageName(mThreadInfo.getThreadPosition() *
                                (mComicChapter.getPageCount() / mThreadInfo.getThreadCount()) +
                                mThreadInfo.getDownloadPosition()));
                        //可以在任意位置进行写入的输出流
                        raf = new RandomAccessFile(file, "rwd");
                        //设置本地文件的长度
                        raf.setLength(length);
                        raf.seek(0);
                        int finished = mThreadInfo.getFinished();
                        InputStream input = response.body().byteStream();
                        byte[] buffer = new byte[1024 * 4];
                        int len = -1;
                        while ((len = input.read(buffer)) != -1) {
                            //将数据写入文件
                            raf.write(buffer, 0, len);
                            //累加每个线程完成进度
                            finished += len;
                            mThreadInfo.setFinished(finished);
                            //在下载暂停时将进度保存至数据库
                            if (isPause) {
                                mDownloadThreadDBHelper.update(mThreadInfo);
                                checkAllThreadPause(mThreadInfo);
                                return;
                            }
                        }
                    } else {
                        //如果之前进行过本页的下载，设置线程的下载位置
                        int start = mThreadInfo.getFinished();
                        response = mClient.newCall(builder.addHeader("Range",
                                "bytes=" + start + "-" + mThreadInfo.getLength()).build()).execute();
                        //设置写入的文件
                        File file = new File(filePath, BaseUtils.getPageName(mThreadInfo.getThreadPosition() *
                                (mComicChapter.getPageCount() / mThreadInfo.getThreadCount()) +
                                mThreadInfo.getDownloadPosition()));
                        raf = new RandomAccessFile(file, "rwd");
                        raf.seek(start);
                        int finished = mThreadInfo.getFinished();
                        //开始下载，由于设置了range，返回代码为部分下载(partial)
                        if (response.code() == HttpURLConnection.HTTP_PARTIAL) {
                            //读取数据
                            InputStream input = response.body().byteStream();
                            byte[] buffer = new byte[1024 * 4];
                            int len = -1;

                            while ((len = input.read(buffer)) != -1) {
                                //将数据写入文件
                                raf.write(buffer, 0, len);
                                //累加每个线程完成进度
                                finished += len;
                                mThreadInfo.setFinished(finished);
                                //在下载暂停时将进度保存至数据库
                                if (isPause) {
                                    mDownloadThreadDBHelper.update(mThreadInfo);
                                    checkAllThreadPause(mThreadInfo);
                                    return;
                                }
                            }
                        }
                    }

                    //一页下载完毕
                    //重置下载进度
                    mThreadInfo.setFinished(0);
                    //设置长度为需要重新获取
                    mThreadInfo.setLength(-1);
                    //将下载进度往前推一页
                    Log.i(TAG, "run: thread-" + mThreadInfo.getThreadPosition() + " finish page " + mThreadInfo.getDownloadPosition());
                    mThreadInfo.setDownloadPosition(mThreadInfo.getDownloadPosition() + 1);
                    mDownloadThreadDBHelper.update(mThreadInfo);
                }

                //标识线程执行完毕
                isFinished = true;
                //检查下载任务是否执行完毕
                checkAllThreadFinish();
            } catch (IOException e) {
                e.printStackTrace();
                //保存进度
                mDownloadThreadDBHelper.update(mThreadInfo);
                mComicChapter.setDownloadStatus(Constants.DOWNLOAD_ERROR);
                //向activity发送广播通知下载取消
                Intent intent = new Intent(DownloadManagerService.ACTION_RECEIVER);
                intent.putExtra("comicChapter", mComicChapter);
                intent.putExtra("notification", "cancel");
                mContext.sendBroadcast(intent);
                mComicChapterDBHelper.update(mComicChapter);

                isError = true;
                checkAllThreadError();
                setDownloadPause(mComicChapter);
            } finally {
                try {
                    if (response != null) {
                        response.close();
                    }
                    if (raf != null) {
                        raf.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 判断所有线程是否执行错误
         */
        private synchronized void checkAllThreadError() {
            boolean allError = true;
            for (DownloadThread thread : mThreadList) {
                if (!thread.isError) {
                    allError = false;
                    break;
                }
            }
            if (allError) {
                doNextDownload();
            }
        }

        /**
         * 判断所有线程是否执行完毕
         */
        private synchronized void checkAllThreadFinish() {
            boolean allFinished = true;
            for (DownloadThread thread : mThreadList) {
                if (!thread.isFinished) {
                    allFinished = false;
                    break;
                }
            }
            if (allFinished) {
                //下载完成后删除本任务线程信息
                mDownloadThreadDBHelper.deleteAllChapterThread(mThreadInfo.getComicChapterUrl());
                mComicChapter.setDownloadStatus(Constants.DOWNLOAD_FINISHED);
                mComicChapter.setDownloadPosition(mComicChapter.getPageCount() - 1);
                mComicChapterDBHelper.update(mComicChapter);
                //向activity发送广播通知下载任务结束
                Intent intent = new Intent(DownloadManagerService.ACTION_RECEIVER);
                intent.putExtra("notification", "cancel");
                intent.putExtra("comicChapter", mComicChapter);
                mContext.sendBroadcast(intent);
                Log.i(TAG, "checkAllThreadFinish: all finished " + mComicChapter.getChapterName());
                Log.i(TAG, "checkAllThreadFinish: the position is " + mComicChapter.getDownloadPosition());
                //下载完成通知
                mNotificationUtil.finishedNotification(mComicChapter);
                mComicChapter = null;
                //下载页数初始化
                mDownloadPosition = 0;
                //进行下一个任务
                doNextDownload();
            }
        }
    }
}
