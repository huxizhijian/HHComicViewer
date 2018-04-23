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

import android.support.annotation.Nullable;

import org.huxizhijian.annotations.SourceInterface;
import org.huxizhijian.hhcomic.comic.entities.Chapter;
import org.huxizhijian.hhcomic.comic.entities.Comic;
import org.huxizhijian.hhcomic.comic.entities.ImageUrl;
import org.huxizhijian.hhcomic.comic.net.ComicRequest;
import org.huxizhijian.hhcomic.comic.net.ComicResponse;

import java.util.List;

/**
 * comic strategy interface
 *
 * @author huxizhijian
 * @date 2018/4/9
 */
@SourceInterface
public interface Source {

    /**
     * Get comic more info page.
     *
     * @param cid comic id, that can use to generate url
     * @return request
     */
    ComicRequest getComicInfoRequest(String cid);

    /**
     * Parse html to comic model.
     *
     * @param response comic info response, it can get response body, header and so on.
     * @param comic    comic model
     */
    void parseInfo(ComicResponse response, Comic comic);

    /**
     * Chapter list request
     *
     * @param response comic info response
     * @param cid      comic id
     * @return If get chapter list should get with once more connection, return a request, else return null.
     */
    ComicRequest getChapterRequest(ComicResponse response, String cid);

    /**
     * Parse response to chapter model.
     *
     * @param response if getChapterRequest return not null, it should be comic info response,
     *                 else it is chapter info response
     * @return list of chapter model
     */
    List<Chapter> parseChapter(ComicResponse response);

    /**
     * Get Image url request.
     *
     * @param cid  comic id
     * @param path chapter path
     * @return chapter page request
     */
    ComicRequest getImageRequest(String cid, String path);

    /**
     * Parse image urls.
     *
     * @param response chapter response
     * @return list of image url
     */
    List<ImageUrl> parseImages(ComicResponse response);

    /**
     * Get chapter specified index page request, if source neet lazy load image url.
     *
     * @param url  chapter page url, set in {@link ImageUrl}, maybe null.
     * @param page chapter page
     * @return chapter page path
     */
    ComicRequest getLazyRequest(@Nullable String url, int page);

    /**
     * Image url lazy parse.
     *
     * @param response lazy chapter page response
     * @param url      chapter url
     * @return image url
     */
    String parseLazy(ComicResponse response, String url);

    /**
     * Make a comic update check request, it usually the same with {@link #getComicInfoRequest}.
     *
     * @return check update request
     */
    ComicRequest getCheckRequest(String cid);

    /**
     * parse check update response.
     *
     * @param response response of {@link #getCheckRequest}
     * @return update time
     */
    String parseCheck(ComicResponse response);
}