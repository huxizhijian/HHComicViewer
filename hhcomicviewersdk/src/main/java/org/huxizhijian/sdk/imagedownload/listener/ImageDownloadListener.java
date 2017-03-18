package org.huxizhijian.sdk.imagedownload.listener;

import org.huxizhijian.sdk.imagedownload.core.Request;

/**
 * @author huxizhijian 2017/3/17
 */
public interface ImageDownloadListener {

    void onStart(Request request);

    void onProgress(Request request, int progress, int size);

    void onFailure(Request request, Throwable throwable, int progress, int size);

    void onCompleted(Request request, int progress, int size);

    void onPaused(Request request, int progress, int size);

    void onDeleted(Request request);

    void onAllFinished();

    void onInQueue(Request request);

}
