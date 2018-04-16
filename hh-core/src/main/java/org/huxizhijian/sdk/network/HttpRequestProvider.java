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
