package org.huxizhijian.sdk.network;


import org.huxizhijian.sdk.network.http.HttpMethod;
import org.huxizhijian.sdk.network.http.HttpRequest;

import java.io.IOException;
import java.net.URI;

/**
 * 自动指定使用的http类
 * Created by huxizhijian on 2016/11/16.
 */

public class HttpRequestProvider {

    private HttpRequestFactory mRequestFactory;

    //判断是否支持okhttp对象
    public static boolean OKHTTP_REQUEST = true;

    public HttpRequestProvider() {
        if (OKHTTP_REQUEST) {
            mRequestFactory = new OkHttpRequestFactory();
        } else {
            mRequestFactory = new OriginHttpRequestFactory();
        }
    }

    public HttpRequest getHttpRequest(URI uri, HttpMethod method) throws IOException {
        return mRequestFactory.createHttpRequest(uri, method);
    }

    public HttpRequestFactory getRequestFactory() {
        return mRequestFactory;
    }

    public void setRequestFactory(HttpRequestFactory requestFactory) {
        mRequestFactory = requestFactory;
    }
}
