package org.huxizhijian.hhcomicviewer2.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.huxizhijian.hhcomicviewer2.vo.ComicCapture;

public class DownloadService extends Service {

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";

    public DownloadService() {
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
            ComicCapture comicCapture = (ComicCapture) intent.getSerializableExtra("comicCapture");
        } else if (intent.getAction().equals(ACTION_STOP)) {
            //暂停下载
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
