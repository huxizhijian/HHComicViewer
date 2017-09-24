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


import org.huxizhijian.hhcomic.comic.bean.Comic;

import java.util.List;

import okhttp3.Request;

/**
 * @author huxizhijian 2017/9/24
 */
public interface BaseParser {

    /**
     * 构造搜索页的http请求
     *
     * @param keyword 关键字
     * @param page    页码
     */
    Request buildSearchRequest(String keyword, int page);

    /**
     * 解析搜索页面返回html
     *
     * @param data 搜索结果html源码，或者json
     * @param page 页码
     */
    List<Comic> getSearchResult(String data, int page);

    /**
     * 构造漫画详情页请求
     *
     * @param cid 网站指定的Comic唯一id
     */
    Request buildDetailRequest(String cid);

    /**
     * 解析出详情
     *
     * @param data  详情html源码，或者json源码
     * @param comic 漫画实体类，用于设置最新参数
     */
    void parseInfo(String data, Comic comic);

    /**
     * 章节列表的http请求构造，如果不需要再次获取，则返回null
     *
     * @param data html或者json源码
     * @param cid  网站指定的漫画唯一id
     */
    Request buildChapterRequest(String data, String cid);

}
