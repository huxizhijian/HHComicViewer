package org.huxizhijian.hhcomic.viewmodel.base;

import android.app.Application;

import org.huxizhijian.hhcomic.model.comic.HHComic;
import org.huxizhijian.hhcomic.model.comic.config.HHComicConfig;
import org.huxizhijian.hhcomic.model.comic.config.SourceConfig;
import org.huxizhijian.hhcomic.model.comic.service.source.base.SourceInfo;
import org.huxizhijian.hhcomic.model.repository.base.BaseRepository;
import org.huxizhijian.hhcomic.model.repository.bean.Response;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

/**
 * 基础ViewModel类
 *
 * @author huxizhijian
 * @date 2018/11/15
 */
public abstract class BaseViewModel<T extends BaseRepository> extends AndroidViewModel {

    protected T mRepository;

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    public List<SourceConfig> getSourceConfigs() {
        return mRepository.getSourceConfigs();
    }

    public MutableLiveData<Response<SourceInfo>> getSourceInfo(String sourceKey) {
        MutableLiveData<Response<SourceInfo>> mutableLiveData = new MutableLiveData<>();
        mRepository.getSourceInfo(sourceKey, mutableLiveData);
        return mutableLiveData;
    }

    public HHComicConfig getConfigUtil() {
        return mRepository.getConfigUtil();
    }

    public HHComic.DatabaseGuide getDaoGuide() {
        return mRepository.getDaoGuide();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // 取消所有订阅
        mRepository.unSubscribe();
    }
}
