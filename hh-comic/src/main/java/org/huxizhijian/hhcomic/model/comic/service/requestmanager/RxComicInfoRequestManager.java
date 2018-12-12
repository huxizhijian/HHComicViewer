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

package org.huxizhijian.hhcomic.model.comic.service.requestmanager;

import org.huxizhijian.hhcomic.model.comic.HHComic;
import org.huxizhijian.hhcomic.model.comic.db.entity.Chapter;
import org.huxizhijian.hhcomic.model.comic.db.entity.Comic;
import org.huxizhijian.hhcomic.model.comic.service.source.base.parser.ComicInfoParser;

import java.util.List;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author huxizhijian
 * @date 2018/9/28
 */
public class RxComicInfoRequestManager extends RxRequestManager {

    RxComicInfoRequestManager(OkHttpClient okHttpClient) {
        super(okHttpClient);
    }

    public Flowable<Comic> getComicInfo(String sourceId, String comicId) {
        return Flowable.create(emitter -> {
            ComicInfoParser parser = HHComic.getSource(sourceId);
            Request request = parser.buildComicInfoRequest(comicId);
            Response response = mOkHttpClient.newCall(request).execute();
            Comic comic = parser.getComicInfo(response.body().bytes(), comicId);
            Map<String, List<Chapter>> chapterMap = parser.getChaptersInfo(response.body().bytes(), comicId);
            comic.setChapterMap(chapterMap);
            emitter.onNext(comic);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }
}
