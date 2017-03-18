package org.huxizhijian.sdk.imagedownload.listener;

/**
 * @author huxizhijian 2017/3/15
 */
public interface TaskProgressListener {

    void onProgressUpdate(int taskPosition, int position, int size);

    void onFinished(int taskPosition, int position, int size);

    void onFailure(int taskPosition, Throwable throwable, int position, int size);

    void onPaused(int taskPosition, int position, int size);

}
