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

package org.huxizhijian.hhcomic.comic.sources.base;

import org.huxizhijian.annotations.SourceInterface;
import org.huxizhijian.hhcomic.comic.entities.Chapter;
import org.huxizhijian.hhcomic.comic.entities.Comic;
import org.huxizhijian.hhcomic.comic.entities.ImageUrl;
import org.huxizhijian.hhcomic.comic.net.ComicRequest;

import java.util.List;

/**
 * Comic策略接口
 *
 * @author huxizhijian
 * @date 2018/4/9
 */
@SourceInterface
public interface Source {
    ComicRequest getComicInfoRequest(String cid);

    void parseInfo(String html, Comic comic);

    ComicRequest getChapterRequest(String html, String cid);

    List<Chapter> parseChapter(String html);

    ComicRequest getImageRequest(String cid, String path);

    List<ImageUrl> parseImages(String html);

    ComicRequest getLazyRequest(String url);

    String parseLazy(String html, String url);

    ComicRequest getCheckRequest();

    String parseCheck(String html);

    String getTitle();
}