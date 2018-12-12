/*
 * Copyright 2016-2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.hhcomic.viewmodel.base;

import android.app.Application;

import org.huxizhijian.hhcomic.model.comic.HHComic;
import org.huxizhijian.hhcomic.model.comic.config.HHComicConfig;
import org.huxizhijian.hhcomic.model.comic.config.SourceConfig;
import org.huxizhijian.hhcomic.model.comic.service.source.base.SourceInfo;
import org.huxizhijian.hhcomic.model.repository.base.ComicRepository;
import org.huxizhijian.hhcomic.model.repository.bean.Resource;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

/**
 * 封装了ComicRepository的方法
 *
 * @author huxizhijian
 * @date 2018/11/21
 */
public class ComicViewModel<T extends ComicRepository> extends BaseViewModel<T> {

    protected MutableLiveData<Resource<SourceInfo>> mSourceInfoLiveData;

    public ComicViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 获取source的排序，key和name等信息
     *
     * @return source config list
     */
    public List<SourceConfig> getSourceConfigs() {
        return mRepository.getSourceConfigs();
    }

    /**
     * 新建一个LiveData，并且返回
     *
     * @param sourceKey sourceKey
     * @return liveData
     */
    public MutableLiveData<Resource<SourceInfo>> getSourceInfo(String sourceKey) {
        mSourceInfoLiveData = new MutableLiveData<>();
        mRepository.getSourceInfo(sourceKey, mSourceInfoLiveData);
        mSourceInfoLiveData.setValue(Resource.loading(null));
        return mSourceInfoLiveData;
    }

    /**
     * 使用外部提供的LiveData
     *
     * @param sourceKey          sourceKey
     * @param sourceInfoLiveData liveData
     */
    public void retrySourceInfo(String sourceKey, MutableLiveData<Resource<SourceInfo>> sourceInfoLiveData) {
        mRepository.getSourceInfo(sourceKey, sourceInfoLiveData);
        sourceInfoLiveData.setValue(Resource.loading(null));
    }

    /**
     * 配置
     *
     * @return 配置管理类
     */
    public HHComicConfig getConfigUtil() {
        return mRepository.getConfigUtil();
    }

    /**
     * 数据库管理类
     *
     * @return db guide
     */
    public HHComic.DatabaseGuide getDaoGuide() {
        return mRepository.getDaoGuide();
    }
}
