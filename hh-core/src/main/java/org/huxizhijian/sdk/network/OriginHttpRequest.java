/*
 * Copyright 2016-2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.sdk.network;


import org.huxizhijian.sdk.network.http.HttpHeader;
import org.huxizhijian.sdk.network.http.HttpMethod;
import org.huxizhijian.sdk.network.http.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;

/**
 * Created by huxizhijian on 2016/11/18.
 */

public class OriginHttpRequest extends BufferHttpRequest {

    private HttpURLConnection mConnection;
    private HttpMethod mMethod;
    private String mUrl;

    public OriginHttpRequest(HttpURLConnection connection, HttpMethod method, String url) {
        this.mConnection = connection;
        this.mMethod = method;
        this.mUrl = url;
    }

    @Override
    protected HttpResponse executeInternal(HttpHeader header, byte[] data) throws IOException {
        for (Map.Entry<String, String> entry : header.entrySet()) {
            mConnection.addRequestProperty(entry.getKey(), entry.getValue());
        }
        mConnection.setDoOutput(true);
        mConnection.setDoInput(true);
        mConnection.setRequestMethod(mMethod.name());
        //发送请求
        mConnection.connect();
        if (data != null && data.length > 0) {
            OutputStream out = mConnection.getOutputStream();
            out.write(data, 0, data.length);
            out.close();
        }
        return new OriginHttpResponse(mConnection);
    }

    @Override
    protected OutputStream getBodyOutputStream() {
        try {
            return mConnection.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HttpMethod getMethod() {
        return mMethod;
    }

    @Override
    public URI getUri() {
        return URI.create(mUrl);
    }
}
