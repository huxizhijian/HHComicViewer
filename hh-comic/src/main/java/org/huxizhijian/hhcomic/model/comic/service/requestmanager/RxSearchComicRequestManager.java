package org.huxizhijian.hhcomic.model.comic.service.requestmanager;

import org.huxizhijian.hhcomic.model.comic.HHComic;
import org.huxizhijian.hhcomic.model.comic.service.bean.result.ComicResultList;
import org.huxizhijian.hhcomic.model.comic.service.source.base.parser.SearchComicParser;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author huxizhijian
 * @date 2018/9/28
 */
public class RxSearchComicRequestManager extends RxRequestManager {

    RxSearchComicRequestManager(OkHttpClient okHttpClient) {
        super(okHttpClient);
    }

    Flowable<ComicResultList> searchComic(String sourceId, String searchKey, int page) {
        return Flowable.create(emitter -> {
            SearchComicParser parser = HHComic.getSource(sourceId);
            Request request = parser.buildSearchRequest(searchKey, page);
            Response response = mOkHttpClient.newCall(request).execute();
            ComicResultList comicResultList = parser.parseSearchList(response.body().bytes(), searchKey, page);
            emitter.onNext(comicResultList);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }
}
