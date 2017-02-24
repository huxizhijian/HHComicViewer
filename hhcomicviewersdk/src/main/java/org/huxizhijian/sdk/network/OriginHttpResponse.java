package org.huxizhijian.sdk.network;

import org.huxizhijian.sdk.network.http.HttpHeader;
import org.huxizhijian.sdk.network.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by huxizhijian on 2016/11/18.
 */

public class OriginHttpResponse extends AbstractHttpResponse {

    private HttpURLConnection mConnection;

    public OriginHttpResponse(HttpURLConnection connection) {
        this.mConnection = connection;
    }

    @Override
    public HttpStatus getStatus() {
        try {
            return HttpStatus.getValue(mConnection.getResponseCode());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getStatusMsg() {
        try {
            return mConnection.getResponseMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected InputStream getBodyInternal() throws IOException {
        return mConnection.getInputStream();
    }

    @Override
    protected void closeInternal() {
        mConnection.disconnect();
    }

    @Override
    public long getContentLength() {
        return mConnection.getContentLength();
    }

    @Override
    public HttpHeader getHeaders() {
        HttpHeader headers = new HttpHeader();
        for (Map.Entry<String, List<String>> entry : mConnection.getHeaderFields().entrySet()) {
            headers.set(entry.getKey(), entry.getValue().get(0));
        }
        return headers;
    }
}
