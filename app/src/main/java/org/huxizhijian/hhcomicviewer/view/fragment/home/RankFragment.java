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

package org.huxizhijian.hhcomicviewer.view.fragment.home;

import android.view.View;

import org.huxizhijian.hhcomic.viewmodel.HomeViewModel;
import org.huxizhijian.hhcomicviewer.R;
import org.huxizhijian.hhcomicviewer.view.base.ComicFragment;
import org.huxizhijian.hhcomicviewer.weight.MultipleStatusView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author huxizhijian
 * @date 2018/12/5
 */
public class RankFragment extends ComicFragment<HomeViewModel> {

    private MultipleStatusView mStatusView;

    private RecyclerView mRecyclerView;

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mStatusView = getViewById(R.id.multiple_status_view);
        mRecyclerView = getViewById(R.id.recycler_view);
        // 网格方式
        mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.content_recycler_view;
    }

    @Override
    protected boolean useActivityViewModel() {
        // 使用activity的ViewModel
        return true;
    }
}
