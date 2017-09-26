/*
 * Copyright 2017 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.hhcomicviewer.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * lazy load base fragment abstract class
 * Created by wei on 2017/1/3.
 */

public abstract class RefreshBaseFragment extends Fragment {

    protected Context mContext;
    protected View mViewRoot;

    protected boolean isPrepared = false;
    protected boolean isVisible = false;
    protected boolean isFirst = true;

    public RefreshBaseFragment() {
        //Empty
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            lazyLoad();
        } else {
            isVisible = false;
            onInVisible();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mViewRoot == null) {
            mViewRoot = initView(inflater, container, savedInstanceState);
        }
        return mViewRoot;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
        lazyLoad();
    }

    /**
     * fragment lazy load
     */
    protected void lazyLoad() {
        refreshLoad();
        if (!isPrepared || !isVisible || !isFirst) {
            return;
        }
        Log.d("TAG", getClass().getName() + "->initData()");
        initData();
        isFirst = false;
    }

    /**
     * fragment refresh
     */
    protected void refreshLoad() {
        if (!isPrepared || !isVisible || isFirst) {
            return;
        }
        Log.d("TAG", getClass().getName() + "->refreshData()");
        refreshData();
    }

    /**
     * create content view
     *
     * @return mViewRoot
     */
    public abstract View initView(LayoutInflater inflater, @Nullable ViewGroup container,
                                  @Nullable Bundle savedInstanceState);

    /**
     * loading data in web
     */
    public abstract void initData();

    /**
     * refresh data when it change
     */
    public abstract void refreshData();

    /**
     * onInvisible
     */
    public abstract void onInVisible();
}
