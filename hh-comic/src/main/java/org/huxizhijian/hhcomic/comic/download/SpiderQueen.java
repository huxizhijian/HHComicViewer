package org.huxizhijian.hhcomic.comic.download;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.huxizhijian.core.app.ConfigKeys;
import org.huxizhijian.core.app.HHEngine;
import org.huxizhijian.hhcomic.comic.bean.DownloadInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import okhttp3.OkHttpClient;

/**
 * 爬虫任务类
 *
 * @author huxizhijian
 * @date 2017/10/20
 */
public final class SpiderQueen implements Runnable {

    public static final int STATE_NONE = 0;
    public static final int STATE_DOWNLOADING = 1;
    public static final int STATE_FINISHED = 2;
    public static final int STATE_FAILED = 3;

    public static final String SPIDER_INFO_FILENAME = ".hhviewer";

    private static final Map<String, SpiderQueen> sQueenMap = new HashMap<>();

    @NonNull
    private final OkHttpClient mHttpClient;
    @NonNull
    private final DownloadInfo mDownloadInfo;

    @Nullable
    private volatile Thread mQueenThread;
    private final Object mQueenLock = new Object();

    private final Object mWorkLock = new Object();
    private ThreadPoolExecutor mWorkerPoolExecutor;
    private int mWorkCount;

    public SpiderQueen(DownloadInfo info) {
        mHttpClient = HHEngine.getConfiguration(ConfigKeys.OKHTTP_CLIENT);
        mDownloadInfo = info;
    }

    @Override
    public void run() {

    }

    public interface OnSpiderListener {

        void onGetPages(int pages);

        /**
         * @param index         图片位置
         * @param contentLength 图片大小，-1是不知道
         * @param receiveSize
         * @param bytesRead
         */
        void onPageDownload(int index, long contentLength, long receiveSize, int bytesRead);

        void onPageSuccess(int index, int finished, int downloaded, int total);

        void onPageFailure(int index, String error, int finished, int downloaded, int total);

        /**
         * AllWorksEnd
         */
        void onFinish(int finished, int downloaded, int total);

        void onGetImageSuccess(int index, Image image);

        void onGetImageFailure(int index, String error);
    }
}
