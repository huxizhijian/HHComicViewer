/*
 * Copyright 2018 huxizhijian
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

package org.huxizhijian.hhcomicviewer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.huxizhijian.hhcomicviewer.app.AppOperator;
import org.huxizhijian.hhcomicviewer.app.HHApplication;
import org.huxizhijian.hhcomicviewer.db.ComicChapterDBHelper;
import org.huxizhijian.hhcomicviewer.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer.model.Comic;
import org.huxizhijian.hhcomicviewer.model.ComicChapter;
import org.huxizhijian.hhcomicviewer.utils.CommonUtils;
import org.huxizhijian.hhcomicviewer.utils.Constants;
import org.huxizhijian.hhcomicviewer.utils.NotificationUtil;
import org.huxizhijian.sdk.imagedownload.ImageDownloader;
import org.huxizhijian.sdk.imagedownload.core.ImageDownloadRequest;
import org.huxizhijian.sdk.imagedownload.core.Request;
import org.huxizhijian.sdk.imagedownload.core.model.ImageEntity;
import org.huxizhijian.sdk.imagedownload.listener.ImageDownloadListener;
import org.huxizhijian.sdk.sharedpreferences.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class DownloadManagerService extends Service implements ImageDownloadListener {

    private ComicChapterDBHelper mComicChapterDBHelper;
    private ImageDownloader mImageDownloader;
    private NotificationUtil mNotificationUtil;

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_START_RANGE = "ACTION_START_RANGE";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_ALL_START = "ACTION_ALL_START";
    public static final String ACTION_ALL_STOP = "ACTION_ALL_STOP";
    public static final String ACTION_DELETE = "ACTION_DELETE";
    public static final String ACTION_DELETE_COMIC = "ACTION_DELETE_COMIC";
    public static final String ACTION_RECEIVER = "ACTION_RECEIVER";

    public DownloadManagerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void init() {
        if (mComicChapterDBHelper == null) {
            mComicChapterDBHelper = ComicChapterDBHelper.getInstance(this);
        }
        if (mNotificationUtil == null) {
            mNotificationUtil = NotificationUtil.getInstance(this);
        }
        if (mImageDownloader == null) {
            mImageDownloader = ImageDownloader.getInstance();
            mImageDownloader.setListener(this);
            if (!mImageDownloader.isInit()) {
                SharedPreferencesManager spm = new SharedPreferencesManager(this);
                String threadCount = spm.getString("download_thread_count", "3_thread");
                mImageDownloader.init(HHApplication.getInstance(),
                        HHApplication.getInstance().getHHWebVariable().getPicServer(),
                        HHApplication.getInstance().getHHWebVariable().getEncodeKey());
                if (org.huxizhijian.sdk.util.Utils.isApkDebugable(this)) {
                    mImageDownloader.setDebug(true);
                } else {
                    mImageDownloader.setDebug(false);
                }
                switch (threadCount) {
                    case "1_thread":
                        mImageDownloader.setThreadCount(1);
                        break;
                    case "3_thread":
                        mImageDownloader.setThreadCount(3);
                        break;
                    case "5_thread":
                        mImageDownloader.setThreadCount(5);
                        break;
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        if (!mImageDownloader.isStarted()) {
            mImageDownloader.start();
        }

        if (intent.getAction().equals(ACTION_START)) {
            //开始下载
            ComicChapter comicChapter = (ComicChapter) intent.getSerializableExtra("comicChapter");
            if (mComicChapterDBHelper.findByChapterId(comicChapter.getChid()) == null) {
                //设置下载目录
                comicChapter.setSavePath(CommonUtils.getDownloadPath(this, comicChapter));
                comicChapter.setDownloadStatus(Constants.DOWNLOAD_IN_QUEUE);
                mComicChapterDBHelper.add(comicChapter);
                ImageEntity entity = new ImageEntity(comicChapter.getChid(),
                        CommonUtils.getChapterUrl(comicChapter.getCid(),
                                comicChapter.getChid(), comicChapter.getServerId()),
                        comicChapter.getSavePath(), comicChapter.getServerId());
                Request request = new ImageDownloadRequest(entity);
                mImageDownloader.addRequest(request);
            } else {
                comicChapter = mComicChapterDBHelper.findByChapterId(comicChapter.getChid());
                if (comicChapter.getDownloadStatus() != Constants.DOWNLOAD_FINISHED) {
                    ImageEntity entity = new ImageEntity(comicChapter.getChid(),
                            CommonUtils.getChapterUrl(comicChapter.getCid(),
                                    comicChapter.getChid(), comicChapter.getServerId()),
                            comicChapter.getSavePath(), comicChapter.getServerId());
                    Request request = new ImageDownloadRequest(entity);
                    mImageDownloader.addRequest(request);
                }
            }
        } else if (intent.getAction().equals(ACTION_START_RANGE)) {
            //继续下载
            ComicChapter comicChapter = (ComicChapter) intent.getSerializableExtra("comicChapter");
            comicChapter = mComicChapterDBHelper.findByChapterId(comicChapter.getChid());
            if (comicChapter.getDownloadStatus() != Constants.DOWNLOAD_FINISHED) {
                ImageEntity entity = new ImageEntity(comicChapter.getChid(),
                        CommonUtils.getChapterUrl(comicChapter.getCid(),
                                comicChapter.getChid(), comicChapter.getServerId()),
                        comicChapter.getSavePath(), comicChapter.getServerId());
                Request request = new ImageDownloadRequest(entity);
                mImageDownloader.addRequest(request);
            }
        } else if (intent.getAction().equals(ACTION_STOP)) {
            //暂停下载
            ComicChapter comicChapter = (ComicChapter) intent.getSerializableExtra("comicChapter");
            Log.i("DownloadManagerService", "onStartCommand: stop");
            ImageEntity entity = new ImageEntity(comicChapter.getChid(),
                    CommonUtils.getChapterUrl(comicChapter.getCid(),
                            comicChapter.getChid(), comicChapter.getServerId()),
                    comicChapter.getSavePath(), comicChapter.getServerId());
            Request request = new ImageDownloadRequest(entity);
            mImageDownloader.cancelRequest(request);
        } else if (intent.getAction().equals(ACTION_ALL_START)) {
            //全部开始下载
            List<ComicChapter> unFinishedChapters = mComicChapterDBHelper.findUnFinishedChapters();
            if (unFinishedChapters != null) {
                List<Request> requestList = new ArrayList<>();
                Request request;
                for (ComicChapter comicChapter : unFinishedChapters) {
                    ImageEntity entity = new ImageEntity(comicChapter.getChid(),
                            CommonUtils.getChapterUrl(comicChapter.getCid(),
                                    comicChapter.getChid(), comicChapter.getServerId()),
                            comicChapter.getSavePath(), comicChapter.getServerId());
                    request = new ImageDownloadRequest(entity);
                    requestList.add(request);
                }
                mImageDownloader.addRequest(requestList);
            }
        } else if (intent.getAction().equals(ACTION_ALL_STOP)) {
            //全部停止下载
            mImageDownloader.stop();
        } else if (intent.getAction().equals(ACTION_DELETE)) {
            //删除一个下载任务
            final ComicChapter comicChapter = (ComicChapter) intent.getSerializableExtra("comicChapter");
            Log.i("DownloadManagerService", "onStartCommand: delete");
            AppOperator.runOnThread(new Runnable() {
                @Override
                public void run() {
                    deleteChapter(comicChapter);
                }
            });
        } else if (intent.getAction().equals(ACTION_DELETE_COMIC)) {
            //删除一本漫画的下载任务
            Comic comic = (Comic) intent.getSerializableExtra("comic");
            final List<ComicChapter> chapters = mComicChapterDBHelper.findByComicCid(comic.getCid());
            if (chapters != null) {
                AppOperator.runOnThread(new Runnable() {
                    @Override
                    public void run() {
                        for (ComicChapter chapter : chapters) {
                            deleteChapter(chapter);
                        }
                    }
                });
            }
        }
        return START_REDELIVER_INTENT;
    }

    private void deleteChapter(ComicChapter comicChapter) {
        //删除存在于sdk model数据库中的数据
        ImageEntity entity = new ImageEntity(comicChapter.getChid(),
                CommonUtils.getChapterUrl(comicChapter.getCid(),
                        comicChapter.getChid(), comicChapter.getServerId()),
                comicChapter.getSavePath(), comicChapter.getServerId());
        Request request = new ImageDownloadRequest(entity);
        mImageDownloader.deleteRequest(request);

        //如果存在下载文件，则删除
        CommonUtils.deleteDirectory(comicChapter.getSavePath());

        //删除漫画的数据库信息
        mComicChapterDBHelper.delete(comicChapter);

        //如果该任务下的comic没有下载的章节了
        List<ComicChapter> chapters = mComicChapterDBHelper.findByComicCid(comicChapter.getCid());
        if (chapters == null || chapters.size() == 0) {
            ComicDBHelper comicDBHelper = ComicDBHelper.getInstance(getApplicationContext());
            Comic comic = comicDBHelper.findByCid(comicChapter.getCid());
            //将下载标记改为false
            comic.setDownload(false);
            comicDBHelper.update(comic);
            //删除上级目录
            CommonUtils.deleteDirectoryParent(comicChapter.getSavePath());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageDownloader.stop();
        mImageDownloader = null;
        mNotificationUtil = null;
        mComicChapterDBHelper = null;
    }

    @Override
    public void onStart(Request request) {
        ComicChapter comicChapter = mComicChapterDBHelper.findByChapterId(request.getChid());
        if (comicChapter == null) return;
        comicChapter.setDownloadStatus(Constants.DOWNLOAD_START);
        mComicChapterDBHelper.update(comicChapter);
        mNotificationUtil.showNotification(DownloadManagerService.this, comicChapter);
        //发送本地广播
        Intent intent = new Intent(DownloadManagerService.ACTION_RECEIVER);
        intent.putExtra("comicChapter", comicChapter);
        intent.putExtra("state", Constants.DOWNLOAD_START);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void onProgress(Request request, int progress, int size) {
        if (request == null) return;
        init();
        ComicChapter comicChapter = mComicChapterDBHelper.findByChapterId(request.getChid());
        if (comicChapter == null) return;
        comicChapter.setDownloadPosition(progress);
        comicChapter.setPageCount(size);
        comicChapter.setDownloadStatus(Constants.DOWNLOAD_DOWNLOADING);
        mComicChapterDBHelper.update(comicChapter);
        mNotificationUtil.updateNotification(comicChapter.getId(), comicChapter);
        //发送本地广播
        Intent intent = new Intent(DownloadManagerService.ACTION_RECEIVER);
        intent.putExtra("comicChapter", comicChapter);
        intent.putExtra("state", Constants.DOWNLOAD_DOWNLOADING);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void onFailure(Request request, Throwable throwable, int progress, int size) {
        if (request == null) return;
        if (mComicChapterDBHelper == null) {
            mComicChapterDBHelper = ComicChapterDBHelper.getInstance(HHApplication.getInstance());
        }
        ComicChapter comicChapter = mComicChapterDBHelper.findByChapterId(request.getChid());
        if (comicChapter == null) return;
        comicChapter.setDownloadPosition(progress);
        comicChapter.setPageCount(size);
        comicChapter.setDownloadStatus(Constants.DOWNLOAD_ERROR);
        mComicChapterDBHelper.update(comicChapter);
        if (mNotificationUtil == null) {
            mNotificationUtil = NotificationUtil.getInstance(HHApplication.getInstance());
        }
        mNotificationUtil.cancelNotification(this, comicChapter.getId());
        //发送本地广播
        Intent intent = new Intent(DownloadManagerService.ACTION_RECEIVER);
        intent.putExtra("comicChapter", comicChapter);
        intent.putExtra("state", Constants.DOWNLOAD_ERROR);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void onCompleted(Request request, int progress, int size) {
        if (request == null) return;
        if (mComicChapterDBHelper == null) {
            mComicChapterDBHelper = ComicChapterDBHelper.getInstance(HHApplication.getInstance());
        }
        ComicChapter comicChapter = mComicChapterDBHelper.findByChapterId(request.getChid());
        if (comicChapter == null) return;
        comicChapter.setDownloadPosition(progress);
        comicChapter.setPageCount(size);
        comicChapter.setDownloadStatus(Constants.DOWNLOAD_FINISHED);
        mComicChapterDBHelper.update(comicChapter);
        if (mNotificationUtil == null) {
            mNotificationUtil = NotificationUtil.getInstance(HHApplication.getInstance());
        }
        mNotificationUtil.finishedNotification(comicChapter);
        //发送本地广播
        Intent intent = new Intent(DownloadManagerService.ACTION_RECEIVER);
        intent.putExtra("comicChapter", comicChapter);
        intent.putExtra("state", Constants.DOWNLOAD_FINISHED);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void onPaused(Request request, int progress, int size) {
        if (request == null) return;
        if (mComicChapterDBHelper == null) {
            mComicChapterDBHelper = ComicChapterDBHelper.getInstance(HHApplication.getInstance());
        }
        ComicChapter comicChapter = mComicChapterDBHelper.findByChapterId(request.getChid());
        if (comicChapter == null) return;
        comicChapter.setDownloadPosition(progress);
        comicChapter.setPageCount(size);
        comicChapter.setDownloadStatus(Constants.DOWNLOAD_PAUSE);
        mComicChapterDBHelper.update(comicChapter);
        if (mNotificationUtil == null) {
            mNotificationUtil = NotificationUtil.getInstance(HHApplication.getInstance());
        }
        mNotificationUtil.cancelNotification(this, comicChapter.getId());
        //发送本地广播
        Intent intent = new Intent(DownloadManagerService.ACTION_RECEIVER);
        intent.putExtra("comicChapter", comicChapter);
        intent.putExtra("state", Constants.DOWNLOAD_PAUSE);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void onDeleted(Request request) {
        //nothing
    }

    @Override
    public void onAllFinished() {
        stopSelf();
    }

    @Override
    public void onAddToQueue(Request request) {
        if (request == null) return;
        if (mComicChapterDBHelper == null) {
            mComicChapterDBHelper = ComicChapterDBHelper.getInstance(HHApplication.getInstance());
        }
        ComicChapter comicChapter = mComicChapterDBHelper.findByChapterId(request.getChid());
        if (comicChapter == null) return;
        comicChapter.setDownloadStatus(Constants.DOWNLOAD_IN_QUEUE);
        mComicChapterDBHelper.update(comicChapter);
        //发送本地广播
        Intent intent = new Intent(DownloadManagerService.ACTION_RECEIVER);
        intent.putExtra("comicChapter", comicChapter);
        intent.putExtra("state", Constants.DOWNLOAD_IN_QUEUE);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

}

