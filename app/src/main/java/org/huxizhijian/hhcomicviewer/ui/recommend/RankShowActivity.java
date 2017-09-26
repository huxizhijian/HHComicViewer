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

package org.huxizhijian.hhcomicviewer.ui.recommend;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.huxizhijian.hhcomicviewer.R;
import org.huxizhijian.hhcomicviewer.databinding.ActivityComicShowBinding;
import org.huxizhijian.hhcomicviewer.utils.CommonUtils;
import org.huxizhijian.sdk.util.TransitionLeakFixUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 漫画排行详情页
 * Created by wei on 2017/1/19.
 */

public class RankShowActivity extends AppCompatActivity {

    private ActivityComicShowBinding mBinding;
    private List<Fragment> mFragments;
    private List<String> mTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_comic_show);
        int defaultViewpagerPosition = getIntent().getIntExtra("rank_type", 0);
        initData();
        setupViewPager(defaultViewpagerPosition);
        setToolBar();
    }

    private void initData() {

        mFragments = new ArrayList<>();

        mTitles = new ArrayList<>();
        mTitles.add("最近刷新");
        mTitles.add("最多人看");
        mTitles.add("评分最高");
        mTitles.add("最多人评论");

        List<String> urls = new ArrayList<>();
        urls.add("/top/newrating.aspx");
        urls.add("/top/hotrating.aspx");
        urls.add("/top/toprating.aspx");
        urls.add("/top/hoorating.aspx");

        for (int i = 0; i < mTitles.size(); i++) {
            mBinding.tabs.addTab(mBinding.tabs.newTab().setText(mTitles.get(i)), i);
            Fragment fragment = new RankDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("url", urls.get(i));
            fragment.setArguments(bundle);
            mFragments.add(fragment);
        }

        mBinding.tabs.setTabTextColors(getResources().getColor(R.color.gray_200),
                getResources().getColor(R.color.white));
        mBinding.tabs.setTabMode(TabLayout.MODE_SCROLLABLE);

    }

    private void setupViewPager(int defaultViewpagerPosition) {
        mBinding.viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));
        mBinding.viewPager.setCurrentItem(defaultViewpagerPosition);
        mBinding.tabs.setupWithViewPager(mBinding.viewPager);
    }

    private void setToolBar() {
        //将其当成actionbar
        setSupportActionBar(mBinding.toolbar);
        CommonUtils.setStatusBarTint(this, getResources().getColor(R.color.colorPrimaryDark));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("漫画排行");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.finishAfterTransition();
                } else {
                    this.finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TransitionLeakFixUtil.removeActivityFromTransitionManager(this);
    }

    private class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        FragmentManager fm;

        TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

    }
}
