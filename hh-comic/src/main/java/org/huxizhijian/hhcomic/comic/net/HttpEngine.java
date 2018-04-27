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

import okhttp3.Interceptor;

/**
 * @author huxizhijian
 * @date 2018/4/16
 */
public interface HttpEngine {

    /**
     * 同步网络请求
     *
     * @param request 请求头和请求体的封装
     * @return 响应头和响应体的封装
     * @throws IOException 可能导致的异常
     */
    ComicResponse execute(ComicRequest request) throws IOException;

    /**
     * 一个默认实现, 用于提供一个子类
     *
     * @param interceptorList 要添加的拦截器列表
     * @return subclass instance
     */
    default HttpEngine getOkHttpEngine(List<Interceptor> interceptorList) {
        return OkHttpEngine.getInstance(interceptorList);
    }
}
