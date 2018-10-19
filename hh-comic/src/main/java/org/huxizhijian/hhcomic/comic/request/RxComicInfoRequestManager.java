package org.huxizhijian.hhcomic.comic.request;

import org.huxizhijian.hhcomic.comic.HHComic;
import org.huxizhijian.hhcomic.comic.entity.Chapter;
import org.huxizhijian.hhcomic.comic.entity.Comic;
import org.huxizhijian.hhcomic.comic.source.base.parser.ComicInfoParser;

import java.util.List;

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
            List<Chapter> chapterList = parser.getChaptersInfo(response.body().bytes(), comicId);
            comic.setChapterList(chapterList);
            emitter.onNext(comic);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }
}
