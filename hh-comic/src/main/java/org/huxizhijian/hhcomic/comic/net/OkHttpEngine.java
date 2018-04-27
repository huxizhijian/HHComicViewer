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

package org.huxizhijian.hhcomic.comic.net;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author huxizhijian
 * @date 2018/4/16
 */
public class OkHttpEngine implements HttpEngine {

    /**
     * 超时时间30秒
     */
    private static final int TIME_OUT = 30_000;

    private static volatile OkHttpEngine sOkHttpEngine;

    private final OkHttpClient mOkHttpClient;

    private OkHttpEngine(List<Interceptor> interceptorList) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS);
        for (Interceptor interceptor : interceptorList) {
            builder.addInterceptor(interceptor);
        }
        mOkHttpClient = builder.build();
    }

    /**
     * 单例模式
     *
     * @param interceptorList 用户欲添加的interceptor
     * @return instance
     */
    public static OkHttpEngine getInstance(List<Interceptor> interceptorList) {
        if (sOkHttpEngine != null) {
            synchronized (OkHttpEngine.class) {
                if (sOkHttpEngine != null) {
                    sOkHttpEngine = new OkHttpEngine(interceptorList);
                }
            }
        }
        return sOkHttpEngine;
    }

    @Override
    public ComicResponse execute(ComicRequest request) throws IOException {
        Request.Builder builder = new Request.Builder().url(request.url);
        if (ComicRequest.GET.equals(request.method)) {
            builder.get();
        } else if (ComicRequest.POST.equals(request.method)) {
            if (request.postContentType != null) {
                RequestBody requestBody = RequestBody.create(MediaType.parse(request.postContentType), request.getPostBody());
                builder.post(requestBody);
            } else {
                builder.post(RequestBody.create(null, ""));
            }
        } else if (ComicRequest.PUT.equals(request.method)) {
            if (request.postContentType != null) {
                RequestBody requestBody = RequestBody.create(MediaType.parse(request.postContentType), request.getPostBody());
                builder.put(requestBody);
            } else {
                builder.put(RequestBody.create(null, ""));
            }
        }
        Map<String, String> header = request.getHeader();
        if (header != null && header.size() != 0) {
            Set<String> keySet = header.keySet();
            for (String key : keySet) {
                builder.addHeader(key, header.get(key));
            }
        }
        Response response = mOkHttpClient.newCall(builder.build()).execute();
        return ComicResponse.buildResponse(response, request);
    }
}
