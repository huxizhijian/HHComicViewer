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

package org.huxizhijian.hhcomic.comic.parser;

import org.huxizhijian.hhcomic.comic.value.IHHComicRequest;
import org.huxizhijian.hhcomic.comic.value.IHHComicResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.Request;

/**
 * 解析策略
 *
 * @author huxizhijian 2017/9/29
 */
public interface ParseStrategy {

    public Request buildRequest(IHHComicRequest comicRequest) throws UnsupportedEncodingException;

    public IHHComicResponse parseData(IHHComicResponse comicResponse, byte[] data) throws IOException;

}