/*
 * Copyright 2017 huxizhijian
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
import java.net.URI;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author huxizhijian
 */

public class OkHttpRequest extends BufferHttpRequest {

    private OkHttpClient mClient;

    private HttpMethod mMethod;

    private String mUrl;

    public OkHttpRequest(OkHttpClient client, HttpMethod method, String url) {
        this.mClient = client;
        this.mMethod = method;
        this.mUrl = url;
    }

    @Override
    protected HttpResponse executeInternal(HttpHeader header, byte[] data) throws IOException {
        boolean isBody = mMethod == HttpMethod.POST;
        RequestBody requestBody = null;
        if (isBody) {
            requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), data);
        }
        Request.Builder builder = new Request.Builder().url(mUrl).method(mMethod.name(), requestBody);

        for (Map.Entry<String, String> entry : header.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        Response response = mClient.newCall(builder.build()).execute();

        System.out.println("contentLength : " + response.body().contentLength());

        return new OkHttpResponse(response);
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
