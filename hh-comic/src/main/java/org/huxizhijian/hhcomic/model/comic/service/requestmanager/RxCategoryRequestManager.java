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
import org.huxizhijian.hhcomic.model.comic.service.bean.Category;
import org.huxizhijian.hhcomic.model.comic.service.bean.FilterList;
import org.huxizhijian.hhcomic.model.comic.service.bean.Sort;
import org.huxizhijian.hhcomic.model.comic.service.bean.result.ComicResultList;
import org.huxizhijian.hhcomic.model.comic.service.source.base.parser.CategoryParser;

import java.io.IOException;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author huxizhijian
 * @date 2018/9/25
 */
public class RxCategoryRequestManager extends RxRequestManager {

    public RxCategoryRequestManager(OkHttpClient okHttpClient) {
        super(okHttpClient);
    }

    public Flowable<ComicResultList> getCategoryList(String sourceId, Category category, int page) {
        return getCategoryList(sourceId, category, page, null, null);
    }

    public Flowable<ComicResultList> getCategoryList(String sourceId, Category category, int page,
                                                     FilterList.FilterPicker picker) {
        return getCategoryList(sourceId, category, page, picker, null);
    }

    public Flowable<ComicResultList> getCategoryList(String sourceId, Category category, int page,
                                                     FilterList.FilterPicker picker, Sort sort) {
        return Flowable.create(emitter -> {
            if (!emitter.isCancelled()) {
                CategoryParser parser = HHComic.getSource(sourceId);
                Request request = parser.buildCategoryRequest(category, page, picker, sort);
                try {
                    // 进行网络请求
                    Response response = mOkHttpClient.newCall(request).execute();
                    if (response.body() != null) {
                        // 进行解析
                        ComicResultList comicResultList = parser.parseCategoryList(response.body().bytes(), category, page);
                        // 返回结果
                        emitter.onNext(comicResultList);
                        emitter.onComplete();
                    } else {
                        emitter.onError(new NullPointerException("response body is null!"));
                    }
                } catch (IOException e) {
                    emitter.onError(e);
                }
            }
        }, BackpressureStrategy.BUFFER);
    }
}
