package org.huxizhijian.sdk.network.service;


import org.huxizhijian.sdk.network.http.HttpRequest;
import org.huxizhijian.sdk.network.http.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 请求线程
 * Created by huxizhijian on 2016/11/18.
 */

public class HttpRunnable implements Runnable {

    private HttpRequest mHttpRequest;
    private NormalRequest mRequest;
    private WorkStation mWorkStation;

    public HttpRunnable(HttpRequest httpRequest, NormalRequest request, WorkStation workStation) {
        this.mHttpRequest = httpRequest;
        this.mRequest = request;
        this.mWorkStation = workStation;
    }

    @Override
    public void run() {
        try {
            if (mRequest.getData() != null) {
                mHttpRequest.getBody().write(mRequest.getData());
            }
            HttpResponse response = mHttpRequest.execute();
            String contentType = response.getHeaders().getContentType();
            mRequest.setContentType(contentType);
            if (response.getStatus().isSuccess()) {
                if (mRequest.getResponse() != null) {
                    mRequest.getResponse().success(mRequest, getData(response));
                }
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mWorkStation.finish(mRequest);
        }
    }

    public byte[] getData(HttpResponse response) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len;
        byte[] data = new byte[512];
        try {
            while ((len = response.getBody().read(data)) != -1) {
                out.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }
}
