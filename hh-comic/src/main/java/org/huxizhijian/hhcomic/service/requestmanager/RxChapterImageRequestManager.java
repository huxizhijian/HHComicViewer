package org.huxizhijian.hhcomic.service.requestmanager;

import org.huxizhijian.hhcomic.HHComic;
import org.huxizhijian.hhcomic.service.bean.result.ChapterImage;
import org.huxizhijian.hhcomic.db.entity.Comic;
import org.huxizhijian.hhcomic.service.source.base.parser.ChapterImageParser;

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
