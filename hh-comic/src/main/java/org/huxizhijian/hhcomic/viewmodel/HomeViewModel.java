package org.huxizhijian.hhcomic.viewmodel;

import android.app.Application;

import org.huxizhijian.hhcomic.model.comic.config.SourceConfig;
import org.huxizhijian.hhcomic.model.comic.service.bean.ComicListBean;
import org.huxizhijian.hhcomic.model.comic.service.bean.FilterList;
import org.huxizhijian.hhcomic.model.comic.service.bean.result.ComicResultList;
import org.huxizhijian.hhcomic.model.comic.service.source.base.SourceInfo;
import org.huxizhijian.hhcomic.model.repository.HomeRepository;
import org.huxizhijian.hhcomic.model.repository.bean.Resource;
import org.huxizhijian.hhcomic.viewmodel.base.ComicViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

/**
 * @author huxizhijian
 * @date 2018/11/19
 */
public class HomeViewModel extends ComicViewModel<HomeRepository> {

    /**
     * 当前正在显示的sourceKey，因为Fragment会回收，所以用Activity对应的ViewModel来进行保存
     */
    private String mCurrentSourceKey;

    private MutableLiveData<Resource<ComicResultList>> mRecommendLiveData;

    private MutableLiveData<Resource<ComicResultList>> mRankResultLiveData;

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public MutableLiveData<Resource<SourceInfo>> getSourceInfo(String sourceKey) {
        mCurrentSourceKey = sourceKey;
        if (mSourceInfoLiveData != null) {
            // 当之前的source info不为空时
            retrySourceInfo(sourceKey, mSourceInfoLiveData);
            return mSourceInfoLiveData;
        }
        return super.getSourceInfo(sourceKey);
    }

    public MutableLiveData<Resource<ComicResultList>> getRecommendResult(String sourceKey) {
        mRecommendLiveData = new MutableLiveData<>();
        mRepository.getRecommendResult(sourceKey, mRecommendLiveData);
        mRecommendLiveData.setValue(Resource.loading(null));
        return mRecommendLiveData;
    }

    public MutableLiveData<Resource<ComicResultList>> getRankResult(@NonNull String sourceKey,
                                                                    @NonNull ComicListBean listBean, int page,
                                                                    @Nullable FilterList.FilterPicker picker) {
        mRankResultLiveData = new MutableLiveData<>();
        mRepository.getRankResult(sourceKey, listBean, page, picker, mRankResultLiveData);
        mRankResultLiveData.setValue(Resource.loading(null));
        return mRankResultLiveData;
    }

    @Nullable
    public MutableLiveData<Resource<ComicResultList>> getRecommendLiveData() {
        return mRecommendLiveData;
    }

    @Nullable
    public MutableLiveData<Resource<ComicResultList>> getRankResultLiveData() {
        return mRankResultLiveData;
    }

    @Nullable
    public MutableLiveData<Resource<SourceInfo>> getSourceInfoLiveData() {
        return mSourceInfoLiveData;
    }

    public void retrySourceInfo() {
        retrySourceInfo(mCurrentSourceKey, mSourceInfoLiveData);
    }

    public String getCurrentSourceKey() {
        return mCurrentSourceKey;
    }

    public String getCurrentSourceTitle() {
        if (mCurrentSourceKey == null) {
            return "";
        }
        List<SourceConfig> sourceConfigs = getSourceConfigs();
        for (SourceConfig sourceConfig : sourceConfigs) {
            if (sourceConfig.getSourceKey().equals(mCurrentSourceKey)) {
                return sourceConfig.getSourceName();
            }
        }
        return "";
    }
}
