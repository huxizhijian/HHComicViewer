package org.huxizhijian.hhcomic.service.requestmanager;

import org.huxizhijian.hhcomic.HHComic;
import org.huxizhijian.hhcomic.service.bean.Category;
import org.huxizhijian.hhcomic.service.bean.FilterList;
import org.huxizhijian.hhcomic.service.bean.Sort;
import org.huxizhijian.hhcomic.service.bean.result.ComicResultList;
import org.huxizhijian.hhcomic.service.source.base.parser.CategoryParser;

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
