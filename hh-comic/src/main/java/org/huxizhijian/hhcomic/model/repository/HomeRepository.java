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

package org.huxizhijian.hhcomic.model.repository;

import org.huxizhijian.hhcomic.model.comic.HHComic;
import org.huxizhijian.hhcomic.model.comic.db.entity.Comic;
import org.huxizhijian.hhcomic.model.comic.service.bean.ComicListBean;
import org.huxizhijian.hhcomic.model.comic.service.bean.FilterList;
import org.huxizhijian.hhcomic.model.comic.service.bean.result.ComicResultList;
import org.huxizhijian.hhcomic.model.repository.base.ComicRepository;
import org.huxizhijian.hhcomic.model.repository.bean.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author huxizhijian
 * @date 2018/11/15
 */
public class HomeRepository extends ComicRepository {

    public HomeRepository() {
        super();
    }

    public void getRecommendResult(@NonNull String sourceKey, MutableLiveData<Resource<Map<String, List<Comic>>>> responseLiveData) {
        // 将onNext()的结果集合起来，以onComplete()的调用为结束，传送结果
        Map<String, List<Comic>> recommendResult = new HashMap<>();
        Disposable disposable = HHComic.service().rankAndRecommend()
                .getRecommendResult(sourceKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comicResultList -> recommendResult.put(comicResultList.getResultName(), comicResultList.getComicList()),
                        throwable -> errorResult(responseLiveData, throwable),
                        () -> successResult(responseLiveData, recommendResult));
        addDisposable(disposable);
    }

    public void getRankResult(@NonNull String sourceKey, @NonNull ComicListBean listBean,
                              int page, @Nullable FilterList.FilterPicker picker,
                              MutableLiveData<Resource<ComicResultList>> responseLiveData) {
        Disposable disposable = HHComic.service().rankAndRecommend()
                .getRankResult(sourceKey, listBean, page, picker)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comicResultList -> successResult(responseLiveData, comicResultList),
                        throwable -> errorResult(responseLiveData, throwable));
        addDisposable(disposable);
    }
}
