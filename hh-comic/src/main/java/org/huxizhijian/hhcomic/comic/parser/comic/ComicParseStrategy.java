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

package org.huxizhijian.hhcomic.comic.parser.comic;

import org.huxizhijian.hhcomic.comic.value.IComicRequest;
import org.huxizhijian.hhcomic.comic.value.IComicResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.Request;

/**
 * 解析策略
 *
 * @author huxizhijian
 * @date 2017/9/29
 */
public interface ComicParseStrategy {

    /**
     * 构建Request
     *
     * @param comicRequest 请求参数
     * @return request
     * @throws UnsupportedEncodingException 字符串转换时可能会产生的异常
     * @throws NullPointerException         请求参数不全时产生的异常
     */
    Request buildRequest(IComicRequest comicRequest) throws UnsupportedEncodingException, NullPointerException;

    /**
     * 解析并返回参数
     *
     * @param comicResponse 返回参数
     * @param data          请求得到的data
     * @return 返回参数
     * @throws IOException          OkHttp请求时可能产生的异常
     * @throws NullPointerException 请求时可能产生的异常
     */
    IComicResponse parseData(IComicResponse comicResponse, byte[] data) throws IOException, NullPointerException;

}