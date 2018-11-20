package org.huxizhijian.hhcomic.viewmodel.base;

import android.app.Application;

import org.huxizhijian.hhcomic.model.repository.base.BaseRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

/**
 * 基础ViewModel类
 *
 * @author huxizhijian
 * @date 2018/11/15
 */
public class BaseViewModel<T extends BaseRepository> extends AndroidViewModel {

    protected T mRepository;

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // 取消所有订阅
        mRepository.clear();
    }
}
