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
import android.preference.PreferenceManager;
import android.util.Log;

import org.huxizhijian.hhcomicviewer2.db.ComicCaptureDBHelper;
import org.huxizhijian.hhcomicviewer2.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer2.db.DownloadThreadDBHelper;
import org.huxizhijian.hhcomicviewer2.enities.Comic;
import org.huxizhijian.hhcomicviewer2.enities.ComicCapture;
import org.huxizhijian.hhcomicviewer2.enities.ThreadInfo;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.utils.NotificationUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程管理类
 * Created by wei on 2016/9/11.
 */
public class DownloadManager {

    private static DownloadManager mDownloadManager; //单例模式
    private ComicCaptureDBHelper mComicCaptureDBHelper; //DB控制
    private DownloadThreadDBHelper mDownloadThreadDBHelper; //下载线程DB控制
    private LinkedList<ComicCapture> mComicCaptureLinkedList; //待下载列表
    private ComicCapture mComicCapture; //正在下载的任务
    private List<DownloadThread> mThreadList; //下载线程集
    private List<ThreadInfo> mInfos; //线程信息集
    private boolean isPause = false;
    private int mDownloadPosition = 0;
    private int mThreadCount = 3; //默认线程的数量
    private Context mContext;
    private NotificationUtil mNotificationUtil; //通知管理类
    private OnMissionFinishedListener mOnMissionFinishedListener; //任务完成回调接口

    private static final String TAG = "DownloadManager";

    //下载线程池
    public static ExecutorService sExecutorService =
            Executors.newCachedThreadPool();

    private DownloadManager(Context context) {
        mComicCaptureDBHelper = ComicCaptureDBHelper.getInstance(context);
        mDownloadThreadDBHelper = DownloadThreadDBHelper.getInstance(context);
        mComicCaptureLinkedList = new LinkedList<>();
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
            mDownloadManager = new DownloadManager(context);
        }
        return mDownloadManager;
    }

    public void startDownload(ComicCapture comicCapture) {
        if (comicCapture.getDownloadStatus() == Constants.DOWNLOAD_FINISHED) return;
        if (mComicCapture == null) {
            //如果没有在下载
            mComicCaptureLinkedList.add(comicCapture);
            doNextDownload();
        } else {
            mComicCaptureLinkedList.add(comicCapture);
        }
    }

    public void stopAllDownload() {
        //清空下载列表
        mComicCaptureLinkedList.clear();
        if (mComicCapture != null) {
            //如果有线程正在下载，暂停线程
            isPause = true;
        }
    }

    /**
     * 删除一个任务
     *
     * @param comicCapture
     */
    public void deleteCapture(ComicCapture comicCapture) {
        //取消这个任务
        setDownloadPause(comicCapture);
        if (mDownloadThreadDBHelper.findByCaptureUrl(comicCapture.getCaptureUrl()) != null) {
            //删除章节数据库信息
            mDownloadThreadDBHelper.deleteAllCaptureThread(comicCapture.getCaptureUrl());
        }

        //如果存在下载文件，则删除
        BaseUtils.deleteDirectory(comicCapture.getSavePath());

        //删除漫画的数据库信息
        mComicCaptureDBHelper.delete(comicCapture);

        //如果该任务下的comic没有下载的章节了
        List<ComicCapture> captures = mComicCaptureDBHelper.findByComicUrl(comicCapture.getComicUrl());
        if (captures == null || captures.size() == 0) {
            ComicDBHelper comicDBHelper = ComicDBHelper.getInstance(mContext);
            Comic comic = comicDBHelper.findByUrl(comicCapture.getComicUrl());
            //将下载标记改为false
            comic.setDownload(false);
            comicDBHelper.update(comic);
            //删除上级目录
            BaseUtils.deleteDirectoryParent(comicCapture.getSavePath());
        }
    }

    /**
     * 暂停一个任务
     *
     * @param comicCapture
     */
    public void setDownloadPause(ComicCapture comicCapture) {
        if (mComicCapture != null && mComicCapture.getCaptureUrl().equals(comicCapture.getCaptureUrl())) {
            //如果要暂停的刚好正在下载
            //暂停线程
            isPause = true;
        } else if (mComicCaptureLinkedList.contains(comicCapture)) {
            //如果存在待下载列表中
            //移除即可
            mComicCaptureLinkedList.remove(comicCapture);
            comicCapture.setDownloadStatus(Constants.DOWNLOAD_PAUSE);
            mComicCaptureDBHelper.update(comicCapture);
        }
    }

    //是否还有任务
    public boolean hasMission() {
        if (mComicCapture != null) {
            return true;
        } else {
            if (mComicCaptureLinkedList.size() != 0) {
                return true;
            }
        }
        return false;
    }

    //检查任务是否在队列中
    public boolean isInQueue(ComicCapture comicCapture) {
        return mComicCaptureLinkedList != null && mComicCaptureLinkedList.size() != 0
                && mComicCaptureLinkedList.contains(comicCapture);
    }

    /**
     * 继续下一个任务
     */
    private void doNextDownload() {
        if (mComicCapture != null) return;
        if (mComicCaptureLinkedList.size() != 0) {
            //如果还有任务
            mComicCapture = mComicCaptureLinkedList.removeFirst();
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
        if (mComicCapture.getDownloadStatus() != Constants.DOWNLOAD_INIT) {
            //如果没有下载完毕
            mInfos = mDownloadThreadDBHelper.findByCaptureUrl(mComicCapture.getCaptureUrl());
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
            mComicCapture.setDownloadStatus(Constants.DOWNLOAD_DOWNLOADING);
            mComicCaptureDBHelper.update(mComicCapture);
        } else {
            newDownloadThreadInfo();
        }
        //向activity发送广播通知下载任务开始
        Intent intent = new Intent(DownloadManagerService.ACTION_RECEIVER);
        intent.putExtra("notification", "show");
        intent.putExtra("comicCapture", mComicCapture);
        mContext.sendBroadcast(intent);
    }

    /**
     * 开启新的线程下载
     */
    private void newDownloadThreadInfo() {
        //更新数据库
        mComicCapture.setDownloadStatus(Constants.DOWNLOAD_START);
        mComicCaptureDBHelper.update(mComicCapture);
        if (mComicCapture.getCaptureName().length() > mThreadCount) {
            for (int i = 0; i < mThreadCount; i++) {
                ThreadInfo threadInfo = new ThreadInfo(i, mThreadCount, 0, -1, 0, mComicCapture.getCaptureUrl());
                mDownloadThreadDBHelper.add(threadInfo);
            }
            mInfos = mDownloadThreadDBHelper.findByCaptureUrl(mComicCapture.getCaptureUrl());
            for (ThreadInfo info : mInfos) {
                DownloadThread thread = new DownloadThread(info);
                sExecutorService.execute(thread);
                //添加线程到集合中
                Log.i(TAG, "doNextDownload: threadStart");
                mThreadList.add(thread);
            }
            mComicCapture.setDownloadStatus(Constants.DOWNLOAD_DOWNLOADING);
            mComicCaptureDBHelper.update(mComicCapture);
        } else {
            //如果并没有3页以上，转为单线程下载
            ThreadInfo threadInfo = new ThreadInfo(0, 1, 0, -1, 0, mComicCapture.getCaptureUrl());
            mDownloadThreadDBHelper.add(threadInfo);
            mInfos = mDownloadThreadDBHelper.findByCaptureUrl(mComicCapture.getCaptureUrl());
            for (ThreadInfo info : mInfos) {
                DownloadThread thread = new DownloadThread(info);
                sExecutorService.execute(thread);
                //添加线程到集合中
                Log.i(TAG, "doNextDownload: threadStart");
                mThreadList.add(thread);
            }
            mComicCapture.setDownloadStatus(Constants.DOWNLOAD_DOWNLOADING);
            mComicCaptureDBHelper.update(mComicCapture);
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
            mComicCapture.setDownloadPosition(mDownloadPosition);
            mComicCapture.setDownloadStatus(Constants.DOWNLOAD_PAUSE);
            //下载页数初始化
            mDownloadPosition = 0;
            mComicCaptureDBHelper.update(mComicCapture);
            //向activity发送广播通知下载任务暂停
            Intent intent = new Intent(DownloadManagerService.ACTION_RECEIVER);
            intent.putExtra("comicCapture", mComicCapture);
            intent.putExtra("notification", "cancel");
            mContext.sendBroadcast(intent);
            mComicCapture = null;
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
            if (mComicCapture == null) return;
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream input = null;
            URL url;
            if (mDownloadThreadDBHelper.findByCaptureUrl(mThreadInfo.getComicCaptureUrl()) == null) {
                //如果没有该线程的数据库信息，保存该信息
                mDownloadThreadDBHelper.add(mThreadInfo);
            }
            //计算出线程要下载的页数
            int threadPageCount = mComicCapture.getPageCount() / mThreadInfo.getThreadCount();
            if (mThreadInfo.getThreadPosition() == mThreadInfo.getThreadCount() - 1) {
                threadPageCount = mComicCapture.getPageCount() - threadPageCount * (mThreadInfo.getThreadCount() - 1);
            }
            //初始化下载页数
            mDownloadPosition += mThreadInfo.getDownloadPosition();
            //如果文件夹不存在，创建写入文件夹
            File filePath = new File(mComicCapture.getSavePath());
            if (!filePath.exists()) {
                Log.i(TAG, "run: make dir " + filePath.getAbsolutePath());
                filePath.mkdirs();
            }
            try {
                while (mThreadInfo.getDownloadPosition() < threadPageCount) {
                    //下载页数增加
                    mDownloadPosition++;
                    mComicCapture.setDownloadPosition(mDownloadPosition);
                    mComicCaptureDBHelper.updateProgress(mComicCapture);
                    //发送通知
                    mNotificationUtil.updateNotification(mComicCapture.getId(),
                            mDownloadPosition, mComicCapture.getPageCount());

                    //向activity发送广播通知下载任务进行中
                    Intent intent = new Intent(DownloadManagerService.ACTION_RECEIVER);
                    intent.putExtra("comicCapture", mComicCapture);
                    mContext.sendBroadcast(intent);
                    url = new URL(mComicCapture.getPicList().get(mThreadInfo.getThreadPosition() *
                            (mComicCapture.getPageCount() / mThreadInfo.getThreadCount()) +
                            mThreadInfo.getDownloadPosition()));
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(180000); //下载一页3分钟为超时
                    conn.setRequestMethod("GET");
                    //初始化文件长度
                    if (mThreadInfo.getLength() == -1) {
                        //从头开始下载
                        int length = -1;
                        //获得文件长度
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            length = conn.getContentLength();
                        }
                        if (length <= 0) {
                            Log.i(TAG, "run: 获取文件长度错误！");
                            throw new IOException();
                        }
                        File file = null;

                        file = new File(filePath, BaseUtils.getPageName(mThreadInfo.getThreadPosition() *
                                (mComicCapture.getPageCount() / mThreadInfo.getThreadCount()) +
                                mThreadInfo.getDownloadPosition()));
                        //可以在任意位置进行写入的输出流
                        raf = new RandomAccessFile(file, "rwd");
                        //设置本地文件的长度
                        raf.setLength(length);
                        raf.seek(0);
                        int finished = mThreadInfo.getFinished();
                        input = conn.getInputStream();
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
                        conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.getLength());
                        //设置写入的文件
                        File file = new File(filePath, BaseUtils.getPageName(mThreadInfo.getThreadPosition() *
                                (mComicCapture.getPageCount() / mThreadInfo.getThreadCount()) +
                                mThreadInfo.getDownloadPosition()));
                        raf = new RandomAccessFile(file, "rwd");
                        raf.seek(start);
                        int finished = mThreadInfo.getFinished();
                        //开始下载，由于设置了range，返回代码为部分下载(partial)
                        if (conn.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                            //读取数据
                            input = conn.getInputStream();
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
                mComicCapture.setDownloadStatus(Constants.DOWNLOAD_ERROR);
                //向activity发送广播通知下载取消
                Intent intent = new Intent(DownloadManagerService.ACTION_RECEIVER);
                intent.putExtra("comicCapture", mComicCapture);
                intent.putExtra("notification", "cancel");
                mContext.sendBroadcast(intent);
                mComicCaptureDBHelper.update(mComicCapture);

                isError = true;
                checkAllThreadError();
                setDownloadPause(mComicCapture);
            } finally {
                try {
                    if (conn != null) {
                        conn.disconnect();
                    }
                    if (input != null) {
                        input.close();
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
                mDownloadThreadDBHelper.deleteAllCaptureThread(mThreadInfo.getComicCaptureUrl());
                mComicCapture.setDownloadStatus(Constants.DOWNLOAD_FINISHED);
                mComicCapture.setDownloadPosition(mComicCapture.getPageCount() - 1);
                mComicCaptureDBHelper.update(mComicCapture);
                //向activity发送广播通知下载任务结束
                Intent intent = new Intent(DownloadManagerService.ACTION_RECEIVER);
                intent.putExtra("notification", "cancel");
                intent.putExtra("comicCapture", mComicCapture);
                mContext.sendBroadcast(intent);
                Log.i(TAG, "checkAllThreadFinish: all finished " + mComicCapture.getCaptureName());
                Log.i(TAG, "checkAllThreadFinish: the position is " + mComicCapture.getDownloadPosition());
                //下载完成通知
                mNotificationUtil.finishedNotification(mComicCapture);
                mComicCapture = null;
                //下载页数初始化
                mDownloadPosition = 0;
                //进行下一个任务
                doNextDownload();
            }
        }
    }
}
