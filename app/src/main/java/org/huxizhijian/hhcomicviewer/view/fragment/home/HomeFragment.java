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

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import org.huxizhijian.hhcomic.model.comic.service.source.base.SourceInfo;
import org.huxizhijian.hhcomic.model.repository.bean.Resource;
import org.huxizhijian.hhcomic.viewmodel.HomeViewModel;
import org.huxizhijian.hhcomicviewer.R;
import org.huxizhijian.hhcomicviewer.view.MainActivity;
import org.huxizhijian.hhcomicviewer.view.base.ComicFragment;
import org.huxizhijian.hhcomicviewer.weight.MultipleStatusView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

/**
 * @author huxizhijian
 * @date 2018/12/2
 */
public class HomeFragment extends ComicFragment<HomeViewModel> implements Toolbar.OnMenuItemClickListener {

    private MultipleStatusView mMultipleStatusView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mMultipleStatusView = getViewById(R.id.multiple_status_view);
        mTabLayout = getViewById(R.id.tab_layout);
        mViewPager = getViewById(R.id.view_pager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        // 初始化Toolbar
        Toolbar toolbar = getViewById(R.id.toolbar);
        toolbar.setTitle(mViewModel.getCurrentSourceTitle());
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(v -> {
            if (mActivity != null) {
                ((MainActivity) mActivity).openDrawer();
            }
        });
    }

    @Override
    protected void dataObserver() {
        super.dataObserver();
        // tab初始化
        TabLayout.Tab recommendTab = mTabLayout.newTab().setText("推荐");
        TabLayout.Tab rankTab = mTabLayout.newTab().setText("排行");
        TabLayout.Tab categoryTab = mTabLayout.newTab().setText("分类");
        // comicInfo返回值订阅
        Observer<Resource<SourceInfo>> resourceObserver = resource -> {
            if (Resource.LOADING.equals(resource.state)) {
                // 加载中
                mTabLayout.setVisibility(View.GONE);
                mMultipleStatusView.showLoading();
            } else if (Resource.ERROR.equals(resource.state)) {
                // 加载失败
                mTabLayout.setVisibility(View.GONE);
                mMultipleStatusView.showError(R.id.error_text_view, resource.message);
            } else if (Resource.NO_NETWORK.equals(resource.state)) {
                // 网络未连接
                mTabLayout.setVisibility(View.GONE);
                mMultipleStatusView.showNoNetwork();
            } else if (Resource.SUCCESS.equals(resource.state)) {
                // 加载成功
                SourceInfo sourceInfo = resource.data;
                mTabLayout.setVisibility(View.VISIBLE);
                mTabLayout.removeAllTabs();
                if (sourceInfo.hasRecommend()) {
                    mTabLayout.addTab(recommendTab);
                }
                if (sourceInfo.hasRank()) {
                    mTabLayout.addTab(rankTab);
                }
                mTabLayout.addTab(categoryTab);
                if (!sourceInfo.hasRecommend() && !sourceInfo.hasRank()) {
                    // 仅仅只有一个标签时，隐藏起来
                    mTabLayout.setVisibility(View.GONE);
                }
                // 加载viewpager对应的fragment
                mMultipleStatusView.showContent();
            }
        };
        mMultipleStatusView.setOnRetryClickListener(v -> {
            // 重新加载（使用的是之前的LiveData，所以不用重新观察）
            mViewModel.retrySourceInfo();
        });
        mViewModel.getSourceInfoLiveData().observe(this, resourceObserver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.toolbar_home, menu);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected boolean useActivityViewModel() {
        // 使用MainActivity的HomeViewModel
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                break;
            case R.id.menu_exchange:
                break;
            default:
                break;
        }
        return false;
    }
}
