package org.huxizhijian.sdk.network.service;


import org.huxizhijian.sdk.network.http.HttpMethod;

/**
 * Created by huxizhijian on 2016/11/18.
 */

public class NormalRequest {

    private String mUrl;
    private HttpMethod mMethod;
    private byte[] mData;
    private NormalResponse mResponse;

    private String mContentType;

    public String getContentType() {
        return mContentType;
    }

    public void setContentType(String contentType) {
        mContentType = contentType;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public HttpMethod getMethod() {
        return mMethod;
    }

    public void setMethod(HttpMethod method) {
        mMethod = method;
    }

    public byte[] getData() {
        return mData;
    }

    public void setData(byte[] data) {
        mData = data;
    }

    public NormalResponse getResponse() {
        return mResponse;
    }

    public void setResponse(NormalResponse response) {
        mResponse = response;
    }

}
