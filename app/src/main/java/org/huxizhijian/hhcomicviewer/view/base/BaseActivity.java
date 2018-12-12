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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 基础Activity
 *
 * @author huxizhijian
 * @date 2018/11/20
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        // 初始化数据
        initData(savedInstanceState);
        // 初始化控件
        initView(savedInstanceState);
    }

    /**
     * 初始化数据
     *
     * @param savedInstanceState 帮助恢复意外关闭的activity的bundle
     */
    protected abstract void initData(Bundle savedInstanceState);

    /**
     * 初始化view
     *
     * @param savedInstanceState 帮助恢复意外关闭的activity的bundle
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 子类的布局文件id
     *
     * @return layout id
     */
    protected abstract int getLayoutId();
}
