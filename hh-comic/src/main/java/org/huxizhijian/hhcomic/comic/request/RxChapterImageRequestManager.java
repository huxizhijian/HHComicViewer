package org.huxizhijian.hhcomic.comic.request;

import org.huxizhijian.hhcomic.comic.HHComic;
import org.huxizhijian.hhcomic.comic.bean.ChapterImage;
import org.huxizhijian.hhcomic.comic.source.parser.ChapterImageParser;

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

    public Flowable<ChapterImage> getChapterImage(String sourceId, String comicId, String chapterId) {
        return Flowable.create(emitter -> {
            ChapterImageParser parser = HHComic.getSource(sourceId);
            Request request = parser.buildChapterRequest(comicId, chapterId);
            Response response = mOkHttpClient.newCall(request).execute();
            ChapterImage chapterImage = parser.getChapterImage(response.body().bytes(), comicId, chapterId);
            emitter.onNext(chapterImage);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }
}
