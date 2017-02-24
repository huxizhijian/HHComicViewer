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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.huxizhijian.hhcomicviewer2.HHApplication;
import org.huxizhijian.hhcomicviewer2.db.ComicChapterDBHelper;
import org.huxizhijian.hhcomicviewer2.model.Comic;
import org.huxizhijian.hhcomicviewer2.model.ComicChapter;
import org.huxizhijian.hhcomicviewer2.persenter.implpersenter.ComicChapterPresenterImpl;
import org.huxizhijian.hhcomicviewer2.persenter.viewinterface.IComicChapterListener;
import org.huxizhijian.hhcomicviewer2.utils.CommonUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.utils.NotificationUtil;

import java.util.List;

public class DownloadManagerService extends Service implements DownloadManager.OnMissionFinishedListener,
        IComicChapterListener {

    private ComicChapterDBHelper mComicChapterDBHelper;
    private DownloadManager mDownloadManager;
    private ComicChapterPresenterImpl mPresenter = new ComicChapterPresenterImpl(this);

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_START_RANGE = "ACTION_START_RANGE";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_ALL_START = "ACTION_ALL_START";
    public static final String ACTION_ALL_STOP = "ACTION_ALL_STOP";
    public static final String ACTION_DELETE = "ACTION_DELETE";
    public static final String ACTION_DELETE_COMIC = "ACTION_DELETE_COMIC";
    public static final String ACTION_RECEIVER = "ACTION_RECEIVER";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //获取每个Chapter的picList
            if (msg.what == 0) {
                ComicChapter comicChapter = (ComicChapter) msg.obj;
                mPresenter.getComicChapter(comicChapter);
            }
        }
    };

    @Override
    public void onSuccess(ComicChapter comicChapter) {
        if (mDownloadManager == null) {
            mDownloadManager = DownloadManager.getInstance(getApplicationContext());
        }
        mDownloadManager.startDownload(comicChapter);
    }

    @Override
    public void onException(Throwable e, ComicChapter comicChapter) {
        e.printStackTrace();
        comicChapter.setDownloadStatus(Constants.DOWNLOAD_ERROR);
        mComicChapterDBHelper.update(comicChapter);
    }

    @Override
    public void onFail(int errorCode, String errorMsg, ComicChapter comicChapter) {
        comicChapter.setDownloadStatus(Constants.DOWNLOAD_ERROR);
        mComicChapterDBHelper.update(comicChapter);
    }

    //广播接收器
    private NotificationChangeReceiver mReceiver;
    private NotificationUtil mNotificationUtil;

    public DownloadManagerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mComicChapterDBHelper = ComicChapterDBHelper.getInstance(this);
        mDownloadManager = DownloadManager.getInstance(this);
        mDownloadManager.setOnMissionFinishedListener(this);
        //初始化
        mReceiver = new NotificationChangeReceiver();
        mNotificationUtil = NotificationUtil.getInstance(this);
        //注册广播接收器
        IntentFilter filter = new IntentFilter(ACTION_RECEIVER);
        LocalBroadcastManager.getInstance(HHApplication.getInstance()).registerReceiver(mReceiver, filter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_START)) {
            //开始下载
            ComicChapter comicChapter = (ComicChapter) intent.getSerializableExtra("comicChapter");

            //查看是否存在数据库中(是否未下载)
            if (mComicChapterDBHelper.findByChapterId(comicChapter.getChid()) == null) {
                comicChapter.setDownloadStatus(Constants.DOWNLOAD_INIT);
                //设置下载目录
                comicChapter.setSavePath(CommonUtils.getDownloadPath(this, comicChapter));
                mComicChapterDBHelper.add(comicChapter);
                //获取自动分配的ID
                comicChapter = mComicChapterDBHelper.findByChapterId(comicChapter.getChid());
                ComicChapterPresenterImpl persenter = new ComicChapterPresenterImpl(new IComicChapterListener() {
                    @Override
                    public void onSuccess(ComicChapter comicChapter) {
                        mComicChapterDBHelper.update(comicChapter);
                        if (mDownloadManager == null) {
                            mDownloadManager = DownloadManager.getInstance(getApplicationContext());
                        }
                        mDownloadManager.startDownload(comicChapter);
                        //发送广播
                        Intent intentBroadcast = new Intent();
                        intentBroadcast.setAction(DownloadManagerService.ACTION_RECEIVER);
                        intentBroadcast.putExtra("comicChapter", comicChapter);
                        sendBroadcast(intentBroadcast);
                    }

                    @Override
                    public void onException(Throwable e, ComicChapter comicChapter) {
                        e.printStackTrace();
                        comicChapter.setDownloadStatus(Constants.DOWNLOAD_ERROR);
                        mComicChapterDBHelper.update(comicChapter);
                    }

                    @Override
                    public void onFail(int errorCode, String errorMsg, ComicChapter comicChapter) {
                        comicChapter.setDownloadStatus(Constants.DOWNLOAD_ERROR);
                        mComicChapterDBHelper.update(comicChapter);
                    }
                });
                persenter.getComicChapter(comicChapter);
            } else {
                //继续下载
                comicChapter = mComicChapterDBHelper.findByChapterId(comicChapter.getChid());
                if (comicChapter.getDownloadStatus() != Constants.DOWNLOAD_FINISHED) {
                    mPresenter.getComicChapter(comicChapter);
                }
            }
        } else if (intent.getAction().equals(ACTION_START_RANGE)) {
            //继续下载
            ComicChapter comicChapter = (ComicChapter) intent.getSerializableExtra("comicChapter");
            comicChapter = mComicChapterDBHelper.findByChapterId(comicChapter.getChid());
            if (comicChapter.getDownloadStatus() != Constants.DOWNLOAD_FINISHED) {
                mPresenter.getComicChapter(comicChapter);
            }
        } else if (intent.getAction().equals(ACTION_STOP)) {
            //暂停下载
            ComicChapter comicChapter = (ComicChapter) intent.getSerializableExtra("comicChapter");
            Log.i("DownloadManagerService", "onStartCommand: stop");
            mDownloadManager.setDownloadPause(comicChapter);
        } else if (intent.getAction().equals(ACTION_ALL_START)) {
            //全部开始下载
            List<ComicChapter> unFinishedChapters = mComicChapterDBHelper.findUnFinishedChapters();
            if (unFinishedChapters != null) {
                Message message = null;
                for (int i = 0; i < unFinishedChapters.size(); i++) {
                    message = new Message();
                    message.what = 0;
                    message.obj = unFinishedChapters.get(i);
                    mHandler.sendMessageDelayed(message, 1000 * i);
                }
            }
        } else if (intent.getAction().equals(ACTION_ALL_STOP)) {
            //全部停止下载
            mDownloadManager.stopAllDownload();
        } else if (intent.getAction().equals(ACTION_DELETE)) {
            //删除一个下载任务
            ComicChapter comicChapter = (ComicChapter) intent.getSerializableExtra("comicChapter");
            Log.i("DownloadManagerService", "onStartCommand: delete");
            mDownloadManager.deleteChapter(comicChapter);
        } else if (intent.getAction().equals(ACTION_DELETE)) {
            //删除一本漫画的下载任务
            Comic comic = (Comic) intent.getSerializableExtra("comic");
            List<ComicChapter> chapters = mComicChapterDBHelper.findByComicCid(comic.getCid());
            if (chapters != null) {
                for (ComicChapter c : chapters) {
                    mDownloadManager.deleteChapter(c);
                }
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDownloadManager.setOnMissionFinishedListener(null);
        mDownloadManager = null;

        //注销广播接收器
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(HHApplication.getInstance()).unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onFinished() {
        //任务全部完成，service停止
        stopSelf();
    }

    class NotificationChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(ACTION_RECEIVER)) return;
            String action = intent.getStringExtra("notification");
            if (action == null) return;
            if (mNotificationUtil == null) return;
            ComicChapter Chapter = (ComicChapter) intent.getSerializableExtra("comicChapter");
            if (Chapter == null) return;

            switch (action) {
                case "show":
                    mNotificationUtil.showNotification(DownloadManagerService.this, Chapter);
                    break;
                case "cancel":
                    mNotificationUtil.cancelNotification(DownloadManagerService.this, Chapter.getId());
                    break;
                default:
                    break;
            }
        }
    }
}

