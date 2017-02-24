package org.huxizhijian.sdk.network;


import org.huxizhijian.sdk.network.http.HttpMethod;
import org.huxizhijian.sdk.network.http.HttpRequest;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by huxizhijian on 2016/11/16.
 */

public class OkHttpRequestFactory implements HttpRequestFactory {

    private OkHttpClient mClient;

    public OkHttpRequestFactory() {
        mClient = new OkHttpClient();
    }


    public OkHttpRequestFactory(OkHttpClient client) {
        this.mClient = client;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.mClient.newBuilder()
                .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                .build();
    }

    public void setWriteTimeOut(int writeTimeOut) {
        this.mClient.newBuilder()
                .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                .build();
    }

    public void setConnectionTimeOut(int connectionTimeOut) {
        this.mClient.newBuilder()
                .connectTimeout(connectionTimeOut, TimeUnit.MILLISECONDS)
                .build();
    }

    @Override
    public HttpRequest createHttpRequest(URI uri, HttpMethod method) {
        return new OkHttpRequest(mClient, method, uri.toString());
    }
}
