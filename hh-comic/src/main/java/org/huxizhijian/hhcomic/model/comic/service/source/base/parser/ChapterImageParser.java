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

import org.huxizhijian.hhcomic.model.comic.service.bean.result.ChapterImage;

import java.io.UnsupportedEncodingException;

import okhttp3.Request;

/**
 * 章节图片地址分析
 *
 * @author huxizhijian
 * @date 2018/8/31
 */
public interface ChapterImageParser {

    /**
     * 章节详情获取request
     *
     * @param comicId   comic id
     * @param chapterId chapter id
     * @param extra     额外信息，比如图片服务器信息，这些信息可以在comic实体类中获得
     * @return request
     */
    Request buildChapterRequest(String comicId, String chapterId, String extra);

    /**
     * 章节image实体类分析
     *
     * @param html      HTML数据
     * @param comicId   comic id
     * @param chapterId chapter id
     * @param extra     额外信息，比如图片服务器信息，这些信息可以在comic实体类中获得
     * @return chapter image
     * @throws UnsupportedEncodingException 可能出现的转码异常
     */
    ChapterImage getChapterImage(byte[] html, String comicId, String chapterId, String extra)
            throws UnsupportedEncodingException;
}
