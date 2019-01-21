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
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.button.MaterialButton;

import org.huxizhijian.hhcomic.model.comic.service.bean.Category;
import org.huxizhijian.hhcomic.model.comic.service.source.base.SourceInfo;
import org.huxizhijian.hhcomic.model.repository.bean.Resource;
import org.huxizhijian.hhcomic.viewmodel.HomeViewModel;
import org.huxizhijian.hhcomicviewer.R;
import org.huxizhijian.hhcomicviewer.util.StatusViewHelper;
import org.huxizhijian.hhcomicviewer.view.base.ComicFragment;
import org.huxizhijian.hhcomicviewer.weight.MultipleStatusView;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author huxizhijian
 * @date 2018/12/5
 */
public class CategoryFragment extends ComicFragment<HomeViewModel> {

    private RecyclerView mRecyclerView;

    private MultipleStatusView mStatusView;

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mStatusView = getViewById(R.id.multiple_status_view);
        mRecyclerView = getViewById(R.id.recycler_view);
        // 初始化RecyclerView
        RecyclerView.LayoutManager manager = new GridLayoutManager(mActivity, 3);
        mRecyclerView.setLayoutManager(manager);
        mStatusView.setOnRetryClickListener(view -> mViewModel.retrySourceInfo());
    }

    @Override
    protected void dataObserver() {
        super.dataObserver();
        MutableLiveData<Resource<SourceInfo>> liveData = mViewModel.getSourceInfoLiveData();
        if (liveData != null) {
            liveData.observe(this, resource ->
                    holdResourceState(resource.state, new StatusViewHelper.HandleStateImpl(mStatusView) {
                        @Override
                        public void onSuccess() {
                            super.onSuccess();
                            SourceInfo sourceInfo = resource.data;
                            List<Category> categoryList = sourceInfo.getCategory();
                            mRecyclerView.setAdapter(new BaseQuickAdapter<Category, BaseViewHolder>
                                    (R.layout.item_category_name, categoryList) {
                                @Override
                                protected void convert(BaseViewHolder helper, Category item) {
                                    MaterialButton button = helper.itemView.findViewById(R.id.button);
                                    button.setText(item.getCategoryName());
                                    button.setOnClickListener(v -> {
                                        // 打开分类结果列表
                                        Toast.makeText(mContext, item.getCategoryId(), Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                        }
                    }));
        } else {
            mStatusView.showError();
        }
    }

    @Override
    protected boolean useActivityViewModel() {
        // 使用activity的ViewModel
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.content_recycler_view;
    }
}
