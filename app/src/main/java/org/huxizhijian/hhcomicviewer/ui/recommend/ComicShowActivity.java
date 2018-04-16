/*
 * Copyright 2016-2018 huxizhijian
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

import android.content.Intent;
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
import org.huxizhijian.hhcomicviewer.adapter.entity.ComicTabList;
import org.huxizhijian.hhcomicviewer.databinding.ActivityComicShowBinding;
import org.huxizhijian.hhcomicviewer.utils.CommonUtils;

import java.util.ArrayList;

public class ComicShowActivity extends AppCompatActivity {

    private ActivityComicShowBinding mBinding;
    private ArrayList<ComicTabList> mTabLists;
    private ArrayList<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_comic_show);
        setToolBar();
        initViews();
    }

    private void initViews() {
        mBinding.tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabLists = new ArrayList<>();
        mFragments = new ArrayList<>();
        Intent intent = getIntent();
        for (int i = 0; i < 5; i++) {
            ComicTabList comicTabList = (ComicTabList) intent.getSerializableExtra("tab_list_" + i);
            mTabLists.add(comicTabList);
            mBinding.tabs.addTab(mBinding.tabs.newTab().setText(comicTabList.getTabName()), i);
            RecommendDetailsFragment fragment = new RecommendDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("tab_list", comicTabList);
            fragment.setArguments(bundle);
            mFragments.add(fragment);
        }
        mBinding.viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));
        mBinding.tabs.setTabTextColors(getResources().getColor(R.color.gray_200),
                getResources().getColor(R.color.white));
        mBinding.tabs.setupWithViewPager(mBinding.viewPager);
        mBinding.viewPager.setCurrentItem(intent.getIntExtra("page", 0), false);
    }

    private void setToolBar() {
        //将其当成actionbar
        setSupportActionBar(mBinding.toolbar);
        CommonUtils.setStatusBarTint(this, getResources().getColor(R.color.colorPrimaryDark));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("主页推荐");
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
            return mTabLists.get(position).getTabName();
        }

    }
}
