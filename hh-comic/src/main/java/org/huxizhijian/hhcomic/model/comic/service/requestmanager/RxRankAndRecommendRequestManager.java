package org.huxizhijian.hhcomic.model.comic.service.requestmanager;

import org.huxizhijian.hhcomic.model.comic.HHComic;
import org.huxizhijian.hhcomic.model.comic.service.bean.ComicListBean;
import org.huxizhijian.hhcomic.model.comic.service.bean.FilterList;
import org.huxizhijian.hhcomic.model.comic.service.bean.result.ComicResultList;
import org.huxizhijian.hhcomic.model.comic.service.source.base.Source;
import org.huxizhijian.hhcomic.model.comic.service.source.base.parser.RankAndRecommendParser;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author huxizhijian
 * @date 2018/9/28
 */
public class RxRankAndRecommendRequestManager extends RxRequestManager {

    RxRankAndRecommendRequestManager(OkHttpClient okHttpClient) {
        super(okHttpClient);
    }

    public Flowable<ComicResultList> getRankResult(String sourceId, ComicListBean listBean, int page,
                                                   FilterList.FilterPicker picker) {
        return Flowable.create(emitter -> {
            RankAndRecommendParser parser = HHComic.getSource(sourceId);
            Request request = parser.buildRankRequest(listBean, page, picker);
            Response response = mOkHttpClient.newCall(request).execute();
            ComicResultList comicResultList = parser.parseRankList(response.body().bytes(), listBean, page);
            emitter.onNext(comicResultList);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }

    public Flowable<ComicResultList> getRecommendResult(String sourceId) {
        return Flowable.create(emitter -> {
            RankAndRecommendParser parser = HHComic.getSource(sourceId);
            Request request = parser.buildRecommendRequest();
            Response response = mOkHttpClient.newCall(request).execute();
            byte[] html = response.body().bytes();
            for (ComicListBean recommendBean : ((Source) parser).getRecommend()) {
                emitter.onNext(parser.parseRecommendList(html, recommendBean));
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }
}
