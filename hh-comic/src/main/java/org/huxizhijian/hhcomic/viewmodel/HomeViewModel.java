package org.huxizhijian.hhcomic.viewmodel;

import android.app.Application;

import org.huxizhijian.hhcomic.model.comic.service.bean.ComicListBean;
import org.huxizhijian.hhcomic.model.comic.service.bean.FilterList;
import org.huxizhijian.hhcomic.model.comic.service.bean.result.ComicResultList;
import org.huxizhijian.hhcomic.model.repository.HomeRepository;
import org.huxizhijian.hhcomic.model.repository.bean.Response;
import org.huxizhijian.hhcomic.viewmodel.base.ComicViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

/**
 * @author huxizhijian
 * @date 2018/11/19
 */
public class HomeViewModel extends ComicViewModel<HomeRepository> {

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Response<ComicResultList>> getRecommendResult(String sourceKey) {
        MutableLiveData<Response<ComicResultList>> responseLiveData = new MutableLiveData<>();
        mRepository.getRecommendResult(sourceKey, responseLiveData);
        return responseLiveData;
    }

    public MutableLiveData<Response<ComicResultList>> getRankResult(@NonNull String sourceKey,
                                                                    @NonNull ComicListBean listBean, int page,
                                                                    @Nullable FilterList.FilterPicker picker) {
        MutableLiveData<Response<ComicResultList>> responseLiveData = new MutableLiveData<>();
        mRepository.getRankResult(sourceKey, listBean, page, picker, responseLiveData);
        return responseLiveData;
    }
}
