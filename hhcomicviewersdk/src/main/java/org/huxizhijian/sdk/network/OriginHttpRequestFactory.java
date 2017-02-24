package org.huxizhijian.sdk.network;

import org.huxizhijian.sdk.network.http.HttpMethod;
import org.huxizhijian.sdk.network.http.HttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * 工厂模式
 * Created by huxizhijian on 2016/11/18.
 */

public class OriginHttpRequestFactory implements HttpRequestFactory {

    private HttpURLConnection mConnection;

    public OriginHttpRequestFactory() {
    }

    public void setReadTimeOut(int readTimeOut) {
        mConnection.setReadTimeout(readTimeOut);
    }

    public void setConnectionTimeOut(int connectionTimeOut) {
        mConnection.setConnectTimeout(connectionTimeOut);
    }

    @Override
    public HttpRequest createHttpRequest(URI uri, HttpMethod method) throws IOException {
        mConnection = (HttpURLConnection) uri.toURL().openConnection();
        return new OriginHttpRequest(mConnection, method, uri.toString());
    }
}
