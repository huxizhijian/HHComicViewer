/*
 * Copyright 2018 huxizhijian
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

import android.support.annotation.IntDef;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装一个Request, 然后转换成一个真正的网络请求
 * 该类为不可变类, 使用了构造器模式生成实例
 *
 * @author huxizhijian
 * @date 2018/4/16
 */
public class ComicRequest {

    final String url;
    final Map<String, String> header;
    @Method
    final int method;

    public static class Builder {

        String url;
        Map<String, String> header;
        @Method
        int method;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder addHeader(String key, String value) {
            if (header == null) {
                header = new HashMap<>();
            }
            header.put(key, value);
            return this;
        }

        public Builder get() {
            this.method = GET;
            return this;
        }

        public Builder post() {
            this.method = POST;
            return this;
        }

        public ComicRequest build(){
            return new ComicRequest(this);
        }
    }

    private ComicRequest(Builder builder) {
        url = builder.url;
        method = builder.method;
        header = builder.header;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public int getMethod() {
        return method;
    }

    public static final int GET = 1;
    public static final int POST = 2;

    @IntDef({GET, POST})
    public @interface Method {
    }
}
