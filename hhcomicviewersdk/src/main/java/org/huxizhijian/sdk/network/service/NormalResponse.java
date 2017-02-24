package org.huxizhijian.sdk.network.service;

/**
 * Created by huxizhijian on 2016/11/18.
 */

public abstract class NormalResponse<T> {
    public abstract void success(NormalRequest request, T data);
    public abstract void fail(int errorCode, String errorMsg);
}
