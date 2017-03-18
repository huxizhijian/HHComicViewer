package org.huxizhijian.sdk.imagedownload;

import android.content.Context;
import android.text.TextUtils;

import org.huxizhijian.sdk.imagedownload.core.ImageDownloadDispatcher;
import org.huxizhijian.sdk.imagedownload.core.Request;
import org.huxizhijian.sdk.imagedownload.core.db.DataBaseAdapter;
import org.huxizhijian.sdk.imagedownload.listener.ImageDownloadListener;
import org.huxizhijian.sdk.imagedownload.listener.RequestProgressListener;
import org.huxizhijian.sdk.imagedownload.utils.Constants;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author huxizhijian 2017/3/15
 */
public class ImageDownloader implements RequestProgressListener {

    private OkHttpClient mClient;
    private DataBaseAdapter mAdapter;
    private boolean isDebug = false;

    private String mPicServer;
    private String mEncodeKey;
    private int mThreadCount = Constants.DEFAULT_DOWNLOAD_THREAD_COUNT;
    private int mPauseTimeout = Constants.DEFAULT_PAUSE_TIMEOUT;

    /**
     * 外部传来的listener
     */
    private ImageDownloadListener mListener;

    /**
     * 当前进行的请求是否完成或者停止
     */
    private boolean isFinished = true;

    /**
     * 请求队列
     */
    private BlockingQueue<Request> mRequestQueue;

    /**
     * 请求阻塞队列，用于插入调度器保持同时下载一个
     */
    private BlockingQueue<Request> mQueue;

    /**
     * 请求调度器
     */
    private ImageDownloadDispatcher mDispatcher;

    private static volatile ImageDownloader sImageDownloader;

    private ImageDownloader() {
        mRequestQueue = new PriorityBlockingQueue<>();
        mQueue = new PriorityBlockingQueue<>(1);
    }

    public static ImageDownloader getInstance() {
        if (sImageDownloader == null) {
            synchronized (ImageDownloader.class) {
                if (sImageDownloader == null) {
                    sImageDownloader = new ImageDownloader();
                }
            }
        }
        return sImageDownloader;
    }

    public ImageDownloader setPauseTimeout(int pauseTimeout) {
        mPauseTimeout = pauseTimeout;
        return getInstance();
    }

    public int getPauseTimeout() {
        return mPauseTimeout;
    }

    public boolean isInit() {
        return !(TextUtils.isEmpty(mPicServer) || TextUtils.isEmpty(mEncodeKey)
                || mAdapter == null || mDispatcher == null);
    }

    public ImageDownloader init(Context context, String picServer, String encodeKey) {
        mAdapter = new DataBaseAdapter(context.getApplicationContext());
        mPicServer = picServer;
        mEncodeKey = encodeKey;
        mDispatcher = new ImageDownloadDispatcher(mThreadCount, mQueue, mAdapter);
        return getInstance();
    }

    public ImageDownloader setListener(ImageDownloadListener listener) {
        mListener = listener;
        return getInstance();
    }

    /**
     * @param client OkHttpClient，如果设置的话，之后的请求都会使用这个client
     * @return 自身
     */
    public ImageDownloader setClient(OkHttpClient client) {
        mClient = client;
        return getInstance();
    }

    public ImageDownloader setThreadCount(int threadCount) {
        mThreadCount = threadCount;
        return getInstance();
    }

    public OkHttpClient getClient() {
        if (mClient == null) {
            mClient = getDefaultClient();
        }
        return mClient;
    }

    public DataBaseAdapter getAdapter() {
        return mAdapter;
    }

    public String getPicServer() {
        return mPicServer;
    }

    public String getEncodeKey() {
        return mEncodeKey;
    }

    private OkHttpClient getDefaultClient() {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .readTimeout(120000, TimeUnit.MILLISECONDS)
                .connectTimeout(30000, TimeUnit.MILLISECONDS)
                .build();
    }

    public boolean isDebug() {
        return isDebug;
    }

    public ImageDownloader setDebug(boolean debug) {
        isDebug = debug;
        return getInstance();
    }

    /**
     * 添加请求，但是在start之后才会进行请求
     *
     * @param request 请求
     */
    public void addRequest(Request request) {
        if (isInQueueOrActive(request)) return;
        request.setListener(this);
        if (mQueue.size() == 0 && isFinished) {
            mQueue.offer(request);
            mListener.onStart(request);
        } else {
            mRequestQueue.offer(request);
            mListener.onInQueue(request);
        }
    }

    /**
     * 添加请求列表，是上面方法的重载方法
     *
     * @param requestList 请求列表
     */
    public void addRequest(List<Request> requestList) {
        Collections.sort(requestList);
        for (Request request : requestList) {
            addRequest(request);
        }
    }

    /**
     * 将dispatcher启动
     *
     * @return 启动是否成功
     */
    public boolean start() {
        if (isInit()) {
            mDispatcher.start();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将dispatcher停止
     */
    public void stop() {
        if (mDispatcher != null) {
            if (mDispatcher.request != null && !isFinished) {
                mDispatcher.request.pause();
            }
            mDispatcher.quit();
        }
        for (int i = 0; i < mRequestQueue.size(); i++) {
            Request request = mRequestQueue.poll();
            mListener.onPaused(request, 0, 0);
        }
        for (int i = 0; i < mQueue.size(); i++) {
            Request request = mRequestQueue.poll();
            mListener.onPaused(request, 0, 0);
        }
        mRequestQueue.clear();
        mQueue.clear();
    }

    public boolean isStarted() {
        return mDispatcher != null && mDispatcher.isAlive();
    }

    /**
     * 查询一个请求是否在请求队列中或者正在进行
     *
     * @param request 请求
     * @return 一个请求是否在队列
     */
    public boolean isInQueueOrActive(Request request) {
        if (mRequestQueue.contains(request) || mQueue.contains(request)) {
            return true;
        }
        if (mDispatcher != null && mDispatcher.request != null) {
            if (request.getChid() != mDispatcher.request.getChid()) return false;
            if (!isFinished) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查询是否有请求在队列中
     *
     * @return 是否队列中
     */
    public boolean isQueueEmpty() {
        return mRequestQueue.size() == 0 && mQueue.size() == 0;
    }

    /**
     * 查询是否有请求进行中
     *
     * @return 是否进行中
     */
    public boolean isHasRequestActive() {
        return mDispatcher != null && mDispatcher.request != null && !isFinished;
    }

    /**
     * 取消一个请求，正在下载则暂停，未下载则移除队列
     *
     * @param request 请求
     */
    public void cancelRequest(Request request) {
        if (mRequestQueue.contains(request)) {
            mRequestQueue.remove(request);
        }
        if (mQueue.contains(request)) {
            mQueue.remove(request);
        }
        if (!isFinished && mDispatcher != null && mDispatcher.request != null) {
            if (mDispatcher.request.getChid() == request.getChid()) {
                //正在下载则暂停
                mDispatcher.request.pause();
            }
        }
    }

    /**
     * 删除一个请求的db记录（已经完成的请求不会有db记录）
     * 如果请求正在进行或在队列中将会首先移除请求
     *
     * @param request 请求
     */
    public void deleteRequest(Request request) {
        cancelRequest(request);
        if (mAdapter.findByChid(request.getChid()) != null) {
            mAdapter.delete(request.getChid());
        }
        mListener.onDeleted(request);
    }

    @Override
    public void onProgress(long chid, int progress, int size) {
        mListener.onProgress(mDispatcher.request, progress, size);
        isFinished = false;
    }

    @Override
    public void onFailure(long chid, Throwable throwable, int progress, int size) {
        mListener.onFailure(mDispatcher.request, throwable, progress, size);
        isFinished = true;
        if (mRequestQueue.size() != 0) {
            Request request = mRequestQueue.poll();
            mListener.onStart(request);
            mQueue.offer(request);
        } else {
            mListener.onAllFinished();
        }
    }

    @Override
    public void onCompleted(long chid, int progress, int size) {
        mListener.onCompleted(mDispatcher.request, progress, size);
        isFinished = true;
        mAdapter.delete(chid);
        if (mRequestQueue.size() != 0) {
            Request request = mRequestQueue.poll();
            mListener.onStart(request);
            mQueue.offer(request);
        } else {
            mListener.onAllFinished();
        }
    }

    @Override
    public void onPaused(long chid, int progress, int size) {
        mListener.onPaused(mDispatcher.request, progress, size);
        isFinished = true;
        if (mRequestQueue.size() != 0) {
            Request request = mRequestQueue.poll();
            mListener.onStart(request);
            mQueue.offer(request);
        } else {
            mListener.onAllFinished();
        }
    }

}
