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
import android.util.Log;

import org.huxizhijian.hhcomicviewer2.app.HHApplication;
import org.huxizhijian.hhcomicviewer2.db.ComicChapterDBHelper;
import org.huxizhijian.hhcomicviewer2.enities.ComicChapter;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.utils.NotificationUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadManagerService extends Service implements DownloadManager.OnMissionFinishedListener {

    private ComicChapterDBHelper mComicChapterDBHelper;
    private DownloadManager mDownloadManager;
    private OkHttpClient mClient;

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_START_RANGE = "ACTION_START_RANGE";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_ALL_START = "ACTION_ALL_START";
    public static final String ACTION_ALL_STOP = "ACTION_ALL_STOP";
    public static final String ACTION_DELETE = "ACTION_DELETE";
    public static final String ACTION_RECEIVER = "ACTION_RECEIVER";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //获取每个Chapter的picList
            if (msg.what == 0) {
                ComicChapter comicChapter = (ComicChapter) msg.obj;
                updateChapter(comicChapter);
            }
        }
    };

    private void updateChapter(final ComicChapter comicChapter) {
        asyncQuery(comicChapter.getChapterUrl(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                comicChapter.setDownloadStatus(Constants.DOWNLOAD_ERROR);
                mComicChapterDBHelper.update(comicChapter);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] contents = response.body().bytes();
                comicChapter.updatePicList(comicChapter.getChapterUrl(), new String(contents, "GB2312"));
                if (mDownloadManager == null) {
                    mDownloadManager = DownloadManager.getInstance(getApplicationContext());
                }
                mDownloadManager.startDownload(comicChapter);
            }
        });
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
        registerReceiver(mReceiver, filter);
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
            if (mComicChapterDBHelper.findByChapterUrl(comicChapter.getChapterUrl()) == null) {
                comicChapter.setDownloadStatus(Constants.DOWNLOAD_INIT);
                //设置下载目录
                comicChapter.setSavePath(BaseUtils.getDownloadPath(this, comicChapter));
                mComicChapterDBHelper.add(comicChapter);
                //获取自动分配的ID
                comicChapter = mComicChapterDBHelper.findByChapterUrl(comicChapter.getChapterUrl());
                final ComicChapter finalComicChapter = comicChapter;
                asyncQuery(comicChapter.getChapterUrl(), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        finalComicChapter.setDownloadStatus(Constants.DOWNLOAD_ERROR);
                        mComicChapterDBHelper.update(finalComicChapter);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        byte[] contents = response.body().bytes();
                        finalComicChapter.updatePicList(finalComicChapter.getChapterUrl(),
                                new String(contents, "GB2312"));
                        mComicChapterDBHelper.update(finalComicChapter);
                        if (mDownloadManager == null) {
                            mDownloadManager = DownloadManager.getInstance(getApplicationContext());
                        }
                        mDownloadManager.startDownload(finalComicChapter);
                        //发送广播
                        Intent intentBroadcast = new Intent();
                        intentBroadcast.setAction(DownloadManagerService.ACTION_RECEIVER);
                        intentBroadcast.putExtra("comicChapter", finalComicChapter);
                        sendBroadcast(intentBroadcast);
                    }
                });
            } else {
                //继续下载
                comicChapter = mComicChapterDBHelper.findByChapterUrl(comicChapter.getChapterUrl());
                if (comicChapter.getDownloadStatus() != Constants.DOWNLOAD_FINISHED) {
                    updateChapter(comicChapter);
                }
            }
        } else if (intent.getAction().equals(ACTION_START_RANGE)) {
            //继续下载
            ComicChapter comicChapter = (ComicChapter) intent.getSerializableExtra("comicChapter");
            comicChapter = mComicChapterDBHelper.findByChapterUrl(comicChapter.getChapterUrl());
            if (comicChapter.getDownloadStatus() != Constants.DOWNLOAD_FINISHED) {
                updateChapter(comicChapter);
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
            unregisterReceiver(mReceiver);
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

    private void asyncQuery(String url, Callback callback) {
        if (mClient == null) mClient = ((HHApplication) getApplication()).getClient();
        Request request = new Request.Builder().url(url).build();
        mClient.newCall(request).enqueue(callback);
    }
}

