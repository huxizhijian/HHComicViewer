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

package org.huxizhijian.hhcomicviewer.view.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.huxizhijian.hhcomic.model.repository.bean.Resource;
import org.huxizhijian.hhcomic.viewmodel.base.ComicViewModel;
import org.huxizhijian.hhcomicviewer.util.StatusViewHelper;

import java.lang.reflect.ParameterizedType;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

/**
 * 结合了ComicViewModel的基类
 *
 * @author huxizhijian
 * @date 2018/11/21
 */
public abstract class ComicFragment<T extends ComicViewModel> extends BaseFragment {

    protected T mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        dataObserver();
        return view;
    }

    @Override
    protected void initView(View rootView) {
    }

    @Override
    protected void initData() {
        if (useActivityViewModel()) {
            mViewModel = ViewModelProviders.of(mActivity).get(getClassType());
        } else {
            mViewModel = ViewModelProviders.of(this).get(getClassType());
        }
    }

    protected void dataObserver() {
    }

    /**
     * 是否使用Activity的ViewModel，这样可以在fragment之间共有一个ViewModel，默认不使用
     *
     * @return 是否使用Activity的ViewModel
     */
    protected boolean useActivityViewModel() {
        return false;
    }

    /**
     * 将T转换成Class类型数据，使用反射
     *
     * @return T的Class类型
     */
    @SuppressWarnings("unchecked cast")
    protected Class<T> getClassType() {
        return (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    protected void holdResourceState(@Resource.State @NonNull String state,
                                     @NonNull StatusViewHelper.HandleStateListener listener) {
        StatusViewHelper.holdResourceState(state, listener);
    }
}
