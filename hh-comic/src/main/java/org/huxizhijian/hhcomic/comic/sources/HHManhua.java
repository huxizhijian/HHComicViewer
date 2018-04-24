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

package org.huxizhijian.hhcomic.comic.sources;

import android.support.annotation.Nullable;

import org.huxizhijian.annotations.SourceImpl;
import org.huxizhijian.hhcomic.comic.entities.Chapter;
import org.huxizhijian.hhcomic.comic.entities.Comic;
import org.huxizhijian.hhcomic.comic.entities.ImageUrl;
import org.huxizhijian.hhcomic.comic.net.ComicRequest;
import org.huxizhijian.hhcomic.comic.net.ComicResponse;
import org.huxizhijian.hhcomic.comic.sources.baseimpl.ComicSource;

import java.util.List;

import static org.huxizhijian.hhcomic.comic.sources.HHManhua.HH_MANHUA;

/**
 * 汗汗漫画策略实现类
 *
 * @author huxizhijian
 * @date 2018/4/9
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@SourceImpl(name = HH_MANHUA)
public class HHManhua extends ComicSource {

    public static final String HH_MANHUA = "汗汗漫画";

    public void createRequest() {
    }

    @Override
    public ComicRequest getComicInfoRequest(String cid) {
        return null;
    }

    @Override
    public void parseInfo(ComicResponse response, Comic comic) {

    }

    @Override
    public ComicRequest getChapterRequest(ComicResponse response, String cid) {
        return null;
    }

    @Override
    public List<Chapter> parseChapter(ComicResponse response) {
        return null;
    }

    @Override
    public ComicRequest getImageRequest(String cid, String path) {
        return null;
    }

    @Override
    public List<ImageUrl> parseImages(ComicResponse response) {
        return null;
    }

    @Override
    public ComicRequest getLazyRequest(@Nullable String url, int page) {
        return null;
    }

    @Override
    public String parseLazy(ComicResponse response, String url) {
        return null;
    }

    @Override
    public ComicRequest getCheckRequest(String cid) {
        return null;
    }

    @Override
    public String parseCheck(ComicResponse response) {
        return null;
    }
}
