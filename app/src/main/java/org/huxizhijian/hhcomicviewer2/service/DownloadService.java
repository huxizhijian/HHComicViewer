package org.huxizhijian.hhcomicviewer2.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.huxizhijian.hhcomicviewer2.db.ComicCaptureDBHelper;
import org.huxizhijian.hhcomicviewer2.enities.ComicCapture;
import org.huxizhijian.hhcomicviewer2.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DownloadService extends Service implements DownloadManager.OnMissionFinishedListener {

    private ComicCaptureDBHelper mComicCaptureDBHelper;
    private DownloadManager mDownloadManager;

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_START_RANGE = "ACTION_START_RANGE";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_ALL_START = "ACTION_ALL_START";
    public static final String ACTION_ALL_STOP = "ACTION_ALL_STOP";
    public static final String ACTION_DELETE = "ACTION_DELETE";
    public static final String ACTION_RECEIVER = "ACTION_RECEIVER";
    public static final String ACTION_CHECK_MISSION = "ACTION_CHECK_MISSION";

    public DownloadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mComicCaptureDBHelper = ComicCaptureDBHelper.getInstance(this);
        mDownloadManager = DownloadManager.getInstance(this);
        mDownloadManager.setOnMissionFinishedListener(this);
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
            comicCapture.setDownloadStatus(Constants.DOWNLOAD_INIT);
            //查看是否存在数据库中(是否未下载)
            if (mComicCaptureDBHelper.findByCaptureUrl(comicCapture.getCaptureUrl()) == null) {
                mComicCaptureDBHelper.add(comicCapture);
                //获取自动分配的ID
                comicCapture = mComicCaptureDBHelper.findByCaptureUrl(comicCapture.getCaptureUrl());
                InitComicCapture init = new InitComicCapture(comicCapture, new CallBack() {
                    @Override
                    public void onFinished(ComicCapture comicCapture) {
                        mDownloadManager.startDownload(comicCapture);
                        mComicCaptureDBHelper.update(comicCapture);
                    }
                });
                DownloadManager.sExecutorService.execute(init);
            }
        } else if (intent.getAction().equals(ACTION_START_RANGE)) {
            //继续下载
            ComicCapture comicCapture = (ComicCapture) intent.getSerializableExtra("comicCapture");
            InitComicCapture init = new InitComicCapture(comicCapture, new CallBack() {
                @Override
                public void onFinished(ComicCapture comicCapture) {
                    mDownloadManager.startDownload(comicCapture);
                }
            });
            DownloadManager.sExecutorService.execute(init);
        } else if (intent.getAction().equals(ACTION_STOP)) {
            //暂停下载
            ComicCapture comicCapture = (ComicCapture) intent.getSerializableExtra("comicCapture");
            Log.i("DownloadService", "onStartCommand: stop");
            mDownloadManager.setDownloadPause(comicCapture);
        } else if (intent.getAction().equals(ACTION_ALL_START)) {
            //全部开始下载
            List<ComicCapture> unFinishedCaptures = mComicCaptureDBHelper.findUnFinishedCaptures();
            for (int i = 0; i < unFinishedCaptures.size(); i++) {
                //获取每个Capture的picList
                InitComicCapture init = new InitComicCapture(unFinishedCaptures.get(i), new CallBack() {
                    @Override
                    public void onFinished(ComicCapture comicCapture) {
                        mDownloadManager.startDownload(comicCapture);
                    }
                });
                DownloadManager.sExecutorService.execute(init);
            }
        } else if (intent.getAction().equals(ACTION_ALL_STOP)) {
            //全部停止下载
            mDownloadManager.stopAllDownload();
        } else if (intent.getAction().equals(ACTION_DELETE)) {
            //删除一个下载任务
            ComicCapture comicCapture = (ComicCapture) intent.getSerializableExtra("comicCapture");
            Log.i("DownloadService", "onStartCommand: delete");
            mDownloadManager.deleteCapture(comicCapture);
        } else if (intent.getAction().equals(ACTION_CHECK_MISSION)) {
            //检查是否还有任务在下载，没有就退出
            if (!mDownloadManager.hasMission()) {
                stopSelf();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onFinished() {
        //任务全部完成
        //停止自身
        stopSelf();
    }

    public interface CallBack {
        void onFinished(ComicCapture comicCapture);
    }

    class InitComicCapture extends Thread {
        private ComicCapture mComicCapture;
        private CallBack mCallBack;

        public InitComicCapture(ComicCapture comicCapture, @Nullable CallBack callBack) {
            this.mComicCapture = comicCapture;
            if (callBack != null) {
                this.mCallBack = callBack;
            }
        }

        @Override
        public void run() {
            URL url = null;
            HttpURLConnection conn = null;
            StringBuffer content = null;
            String line = null;
            try {
                url = new URL(Constants.HHCOMIC_URL + mComicCapture.getCaptureUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                conn.connect();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "GB2312"));
                content = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                reader.close();
                conn.disconnect();
                mComicCapture.updatePicList(mComicCapture.getCaptureUrl(), content.toString());
                if (mCallBack != null) {
                    mCallBack.onFinished(mComicCapture);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mComicCapture.setDownloadStatus(Constants.DOWNLOAD_ERROR);
                mComicCaptureDBHelper.update(mComicCapture);
            }
        }
    }
}
