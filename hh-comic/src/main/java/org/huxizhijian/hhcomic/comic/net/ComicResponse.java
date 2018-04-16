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

import okhttp3.Request;
import okhttp3.Response;

/**
 * 封装一个okhttp3的Response
 * 不可变类
 *
 * @author huxizhijian
 * @date 2018/4/16
 */
public final class ComicResponse {

    final Response mResponse;
    final ComicRequest mComicRequest;

    public static ComicResponse buildResponse(Response response, ComicRequest comicRequest) {
        return new ComicResponse(response, comicRequest);
    }

    public ComicRequest getComicRequest() {
        return mComicRequest;
    }

    public Request request() {
        return mResponse.request();
    }

    public int code() {
        return mResponse.code();
    }

    public boolean isSuccessful() {
        return mResponse.isSuccessful();
    }

    public byte[] getBodyBytes() throws IOException {
        return mResponse.body().bytes();
    }

    public String getBodyString() throws IOException {
        return mResponse.body().string();
    }

    public String getBodyGB2312EncodingString() throws IOException {
        return new String(getBodyBytes(), "GB2312");
    }

    public String getBodyUTF8EncodingString() throws IOException {
        return new String(getBodyBytes(), "UTF-8");
    }

    public String header(String name) {
        return mResponse.header(name);
    }

    public String header(String name, String defultValue) {
        return mResponse.header(name, defultValue);
    }

    private ComicResponse(Response response, ComicRequest comicRequest) {
        mResponse = response;
        mComicRequest = comicRequest;
    }
}
