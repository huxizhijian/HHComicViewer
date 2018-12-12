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

import org.huxizhijian.hhcomic.viewmodel.base.ComicViewModel;

import java.lang.reflect.ParameterizedType;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

/**
 * 结合了ComicViewModel的基类
 *
 * @author huxizhijian
 * @date 2018/11/21
 */
public abstract class ComicActivity<T extends ComicViewModel> extends BaseActivity {

    protected T mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataObserver();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(getClassType());
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
    }

    /**
     * 数据观察者的绑定，在初始化数据和视图之后
     */
    protected void dataObserver() {
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
}
