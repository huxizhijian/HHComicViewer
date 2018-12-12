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
import org.huxizhijian.hhcomic.model.comic.service.bean.result.ChapterImage;
import org.huxizhijian.hhcomic.model.comic.db.entity.Comic;
import org.huxizhijian.hhcomic.model.comic.service.source.base.parser.ChapterImageParser;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author huxizhijian
 * @date 2018/9/27
 */
public class RxChapterImageRequestManager extends RxRequestManager {

    RxChapterImageRequestManager(OkHttpClient okHttpClient) {
        super(okHttpClient);
    }

    public Flowable<ChapterImage> getChapterImage(String sourceId, Comic comic, String chapterId) {
        return getChapterImage(sourceId, comic.getComicId(), chapterId, comic.getExtra());
    }

    public Flowable<ChapterImage> getChapterImage(String sourceId, String comicId, String chapterId, String extra) {
        return Flowable.create(emitter -> {
            ChapterImageParser parser = HHComic.getSource(sourceId);
            Request request = parser.buildChapterRequest(comicId, chapterId, extra);
            Response response = mOkHttpClient.newCall(request).execute();
            ChapterImage chapterImage = parser.getChapterImage(response.body().bytes(), comicId, chapterId, extra);
            emitter.onNext(chapterImage);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }
}
