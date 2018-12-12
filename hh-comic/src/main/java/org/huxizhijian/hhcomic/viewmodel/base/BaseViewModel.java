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

import org.huxizhijian.hhcomic.model.repository.base.BaseRepository;

import java.lang.reflect.ParameterizedType;

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
        mRepository = getNewInstance(this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // 取消所有订阅
        if (mRepository != null) {
            mRepository.clear();
        }
    }

    /**
     * 使用反射实例化第一个泛型对象
     *
     * @return T的Class类型
     */
    @SuppressWarnings("unchecked cast")
    private T getNewInstance(Object object) {
        if (object != null) {
            try {
                return ((Class<T>) ((ParameterizedType) (object.getClass()
                        .getGenericSuperclass())).getActualTypeArguments()[0])
                        .newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassCastException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
