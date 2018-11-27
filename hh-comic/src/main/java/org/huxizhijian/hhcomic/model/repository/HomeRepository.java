package org.huxizhijian.hhcomic.model.repository;

import org.huxizhijian.hhcomic.model.comic.HHComic;
import org.huxizhijian.hhcomic.model.comic.service.bean.ComicListBean;
import org.huxizhijian.hhcomic.model.comic.service.bean.FilterList;
import org.huxizhijian.hhcomic.model.comic.service.bean.result.ComicResultList;
import org.huxizhijian.hhcomic.model.repository.base.ComicRepository;
import org.huxizhijian.hhcomic.model.repository.bean.Response;

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

    public MutableLiveData<Response<ComicResultList>> getRecommendResult(@NonNull String sourceKey) {
        MutableLiveData<Response<ComicResultList>> responseLiveData = new MutableLiveData<>();
        Disposable disposable = HHComic.service().rankAndRecommend()
                .getRecommendResult(sourceKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comicResultList -> successResult(responseLiveData, comicResultList),
                        throwable -> errorResult(responseLiveData, throwable));
        addDisposable(disposable);
        return responseLiveData;
    }

    public MutableLiveData<Response<ComicResultList>> getRankResult(@NonNull String sourceKey, @NonNull ComicListBean listBean,
                                                                    int page, @Nullable FilterList.FilterPicker picker) {
        MutableLiveData<Response<ComicResultList>> responseLiveData = new MutableLiveData<>();
        Disposable disposable = HHComic.service().rankAndRecommend()
                .getRankResult(sourceKey, listBean, page, picker)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(comicResultList -> successResult(responseLiveData, comicResultList),
                        throwable -> errorResult(responseLiveData, throwable));
        addDisposable(disposable);
        return responseLiveData;
    }
}
