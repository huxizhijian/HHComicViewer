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

import org.huxizhijian.hhcomic.model.comic.db.entity.Comic;
import org.huxizhijian.hhcomic.viewmodel.HomeViewModel;
import org.huxizhijian.hhcomicviewer.R;
import org.huxizhijian.hhcomicviewer.adapter.RecommendMultipleItemAdapter;
import org.huxizhijian.hhcomicviewer.util.StatusViewHelper;
import org.huxizhijian.hhcomicviewer.view.base.ComicFragment;
import org.huxizhijian.hhcomicviewer.weight.MultipleStatusView;

import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author huxizhijian
 * @date 2018/12/5
 */
public class RecommendFragment extends ComicFragment<HomeViewModel> {

    private MultipleStatusView mStatusView;

    private RecyclerView mRecyclerView;

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mStatusView = getViewById(R.id.multiple_status_view);
        mRecyclerView = getViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mStatusView.setOnRetryClickListener(v -> dataObserver());
    }

    @Override
    protected void dataObserver() {
        super.dataObserver();
        mViewModel.getRecommendResult(mViewModel.getCurrentSourceKey()).observe(this, resource ->
                holdResourceState(resource.state, new StatusViewHelper.HandleStateImpl(mStatusView) {
                    @Override
                    public void onSuccess() {
                        super.onSuccess();
                        // 多类型recyclerView创建
                        Map<String, List<Comic>> recommendResouce = resource.data;
                        mRecyclerView.setAdapter(new RecommendMultipleItemAdapter(null));
                    }
                }));
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
