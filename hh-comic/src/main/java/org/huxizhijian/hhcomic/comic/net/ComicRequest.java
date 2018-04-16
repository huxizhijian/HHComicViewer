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

import android.support.annotation.StringDef;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装一个Request, 然后按照{@link HttpEngine}的需要转换成一个真正的网络请求
 * 该类为不可变类, 类的不可变成员都为私有, 使用了构造器模式生成实例
 *
 * @author huxizhijian
 * @date 2018/4/16
 */
public final class ComicRequest {

    final String url;
    private final Map<String, String> header;
    @Method
    final String method;
    final String postContentType;
    private final byte[] postBody;

    public static class Builder {

        String url;
        Map<String, String> header;
        @Method
        String method;
        String postContentType;
        byte[] postBody;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder addHeader(String key, String value) {
            if (header == null) {
                header = new HashMap<>(16);
            }
            header.put(key, value);
            return this;
        }

        public Builder get() {
            this.method = GET;
            return this;
        }

        public Builder post(String contentType, byte[] body) {
            this.method = POST;
            postContentType = contentType;
            postBody = body;
            return this;
        }

        public ComicRequest build() {
            return new ComicRequest(this);
        }
    }

    private ComicRequest(Builder builder) {
        url = builder.url;
        method = builder.method;
        header = builder.header;
        postContentType = builder.postContentType;
        postBody = builder.postBody;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeader() {
        if (header == null || header.size() == 0) {
            return null;
        }
        // 传递一个header的复制, 因为该类是不可变类, 不能让外部有机会改变该类的任何属性
        Map<String, String> newCopy = new HashMap<>(header.size());
        newCopy.putAll(header);
        return newCopy;
    }

    public String getMethod() {
        return method;
    }

    public String getPostContentType() {
        return postContentType;
    }

    public byte[] getPostBody() {
        return postBody.clone();
    }

    public static final String GET = "GET";
    public static final String POST = "POST";

    @StringDef({GET, POST})
    public @interface Method {
    }
}
