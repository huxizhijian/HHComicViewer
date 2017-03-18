package org.huxizhijian.sdk.imagedownload.core;


import org.huxizhijian.sdk.imagedownload.listener.RequestProgressListener;

import java.util.List;

/**
 * @author huxizhijian 2017/3/16
 */
public interface Request extends Comparable<Request> {

    void addTask(ImageDownloadTask task);

    String getUri();

    void setPicList(List<String> picList);

    long getChid();

    List<ImageDownloadTask> getDownloadTasks();

    void pause();

    int getServerId();

    String getDownloadPath();

    void onError(Exception e);

    void setListener(RequestProgressListener listener);

}
