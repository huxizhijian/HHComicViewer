package org.huxizhijian.hhcomic.comic.request;

import org.huxizhijian.hhcomic.comic.HHComic;
import org.huxizhijian.hhcomic.comic.bean.Category;
import org.huxizhijian.hhcomic.comic.bean.ComicResultList;
import org.huxizhijian.hhcomic.comic.source.parser.CategoryParser;

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
        return Flowable.create(emitter -> {
            if (!emitter.isCancelled()) {
                CategoryParser parser = HHComic.getSource(sourceId);
                Request request = parser.buildCategoryRequest(category, page);
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
