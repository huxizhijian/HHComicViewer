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
