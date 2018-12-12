/*
 * Copyright 2016-2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.hhcomic.model.comic.service.source.base.parser;

import org.huxizhijian.hhcomic.model.comic.service.bean.result.ComicResultList;

import java.io.UnsupportedEncodingException;

import okhttp3.Request;

/**
 * 漫画搜索结果解析器
 *
 * @author huxizhijian
 * @date 2018/8/31
 */
public interface SearchComicParser {

    /**
     * 构建搜索请求Request
     *
     * @param searchKey 搜索关键词
     * @param page      结果页码
     * @return request
     */
    Request buildSearchRequest(String searchKey, int page);

    /**
     * 解析搜索结果
     *
     * @param html      html
     * @param searchKey 搜索关键词
     * @param page      页码
     * @return result list
     * @throws UnsupportedEncodingException 可能出现的解析错误
     */
    ComicResultList parseSearchList(byte[] html, String searchKey, int page) throws UnsupportedEncodingException;
}
