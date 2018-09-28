package org.huxizhijian.hhcomic.comic.request;

import org.huxizhijian.hhcomic.comic.HHComic;
import org.huxizhijian.hhcomic.comic.bean.ComicRank;
import org.huxizhijian.hhcomic.comic.bean.ComicResultList;
import org.huxizhijian.hhcomic.comic.source.parser.RankComicParser;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author huxizhijian
 * @date 2018/9/28
 */
public class RxRankComicRequestManager extends RxRequestManager {

    RxRankComicRequestManager(OkHttpClient okHttpClient) {
        super(okHttpClient);
    }

    Flowable<ComicResultList> getRankResult(String sourceId, ComicRank rank, int page) {
        return Flowable.create(emitter -> {
            RankComicParser parser = HHComic.getSource(sourceId);
            Request request = parser.buildRankRequest(rank, page);
            Response response = mOkHttpClient.newCall(request).execute();
            ComicResultList comicResultList = parser.parseRankList(response.body().bytes(), page);
            emitter.onNext(comicResultList);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }
}
