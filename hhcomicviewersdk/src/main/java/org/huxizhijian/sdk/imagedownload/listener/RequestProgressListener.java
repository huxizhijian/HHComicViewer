package org.huxizhijian.sdk.imagedownload.listener;

/**
 * @author huxizhijian 2017/3/15
 */
public interface RequestProgressListener {

    void onProgress(long chid, int progress, int size);

    void onFailure(long chid, Throwable throwable, int progress, int size);

    void onCompleted(long chid, int progress, int size);

    void onPaused(long chid, int progress, int size);

}
