/*
 * Copyright 2016 huxizhijian
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

package org.huxizhijian.hhcomicviewer2.ui.common;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer2.HHApplication;
import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.databinding.ActivityMainBinding;
import org.huxizhijian.hhcomicviewer2.ui.download.OfflineComicDownloadActivity;
import org.huxizhijian.hhcomicviewer2.ui.entry.MarkedFragment;
import org.huxizhijian.hhcomicviewer2.ui.recommend.RankAndClassifiesFragment;
import org.huxizhijian.hhcomicviewer2.ui.recommend.RecommendFragment;
import org.huxizhijian.hhcomicviewer2.ui.search.SearchActivity;
import org.huxizhijian.hhcomicviewer2.ui.user.PreferenceActivity;
import org.huxizhijian.hhcomicviewer2.utils.CommonUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.utils.IMMLeaks;
import org.huxizhijian.sdk.sharedpreferences.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.List;

import br.com.mauker.materialsearchview.MaterialSearchView;

public class MainActivity extends AppCompatActivity {

    //界面代码
    private final static int MARK_PAGE = 0;
    private final static int RECOMMEND_PAGE = 1;
    private final static int CLASSIFIES_PAGE = 2;
    private final static int RANK_PAGE = 3;

    //绑定界面
    private ActivityMainBinding mBinding;

    //fragment操作
    private List<Fragment> mFragments;
    private List<String> mTitles;
    private TabFragmentPagerAdapter mAdapter;

    //搜索记录
    private SharedPreferences mSharedPreferences;

    private long mLastBackPressedTime = 0; //用于用户不误操作退出键

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initView();
        initViewPager();
        checkFirstRun();
        IMMLeaks.fixFocusedViewLeak(HHApplication.getInstance());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.searchView.clearFocus();
    }

    private void initViewPager() {
        //clear view
        mBinding.tabs.removeAllTabs();
        mTitles = new ArrayList<>();
        mFragments = new ArrayList<>();

        //load data
        mTitles.add(getResources().getString(R.string.mark));
        mTitles.add(getResources().getString(R.string.recommendation));
        mTitles.add(getResources().getString(R.string.classifies));
        mTitles.add(getResources().getString(R.string.rank));

        mBinding.tabs.addTab(mBinding.tabs.newTab().setText(mTitles.get(0)), MARK_PAGE);
        mBinding.tabs.addTab(mBinding.tabs.newTab().setText(mTitles.get(1)), RECOMMEND_PAGE);
        mBinding.tabs.addTab(mBinding.tabs.newTab().setText(mTitles.get(2)), CLASSIFIES_PAGE);
        mBinding.tabs.addTab(mBinding.tabs.newTab().setText(mTitles.get(3)), RANK_PAGE);

        mBinding.tabs.setTabTextColors(getResources().getColor(R.color.gray_200),
                getResources().getColor(R.color.white));

        MarkedFragment markedFragment = new MarkedFragment();
        RecommendFragment recommendFragment = new RecommendFragment();
        RankAndClassifiesFragment fragment_classifies =
                RankAndClassifiesFragment.newInstance(RankAndClassifiesFragment.MODE_CLASSIFIES);
        RankAndClassifiesFragment fragment_rank =
                RankAndClassifiesFragment.newInstance(RankAndClassifiesFragment.MODE_RANK);

        mFragments.add(markedFragment);
        mFragments.add(recommendFragment);
        mFragments.add(fragment_classifies);
        mFragments.add(fragment_rank);

        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        mBinding.viewPager.setAdapter(mAdapter);
        mBinding.viewPager.setCurrentItem(0);
        mBinding.tabs.setupWithViewPager(mBinding.viewPager);
    }

    private void checkFirstRun() {
        //检测是不是第一次运行
        SharedPreferencesManager preferencesManager =
                new SharedPreferencesManager(this, Constants.SHARED_PREFERENCES_NAME);
        boolean isFirstRun = preferencesManager.getBoolean("isFirstRun", true);
        if (isFirstRun) {
            //第一次运行，使用manager的默认值
            PreferenceManager.setDefaultValues(this, R.xml.about_preferences, false);
            PreferenceManager.setDefaultValues(this, R.xml.download_preferences, false);
            PreferenceManager.setDefaultValues(this, R.xml.reading_preferences, false);
            preferencesManager.putBoolean("isFirstRun", false);
            //第一次会自动转到推荐界面
            mBinding.viewPager.setCurrentItem(1);
        }
    }

    private void initView() {
        //将其当成actionbar
        setSupportActionBar(mBinding.toolbar);
        CommonUtils.setStatusBarTint(this, getResources().getColor(R.color.colorPrimaryDark));
        //绑定searchView事件
        mBinding.searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String suggestion = mBinding.searchView.getSuggestionAtPosition(position);
                mBinding.searchView.setQuery(suggestion, true);
            }
        });
        mBinding.searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    Toast.makeText(MainActivity.this, "搜索内容不能为空！", Toast.LENGTH_SHORT).show();
                    return true;
                }
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mBinding.searchView.setShouldKeepHistory(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.menu_search:
                //打开搜索列表
                if (!mBinding.searchView.isOpen()) {
                    mBinding.searchView.openSearch();
                    if (mSharedPreferences == null) {
                        mSharedPreferences = getSharedPreferences("history", Context.MODE_PRIVATE);
                    }
                    String group = mSharedPreferences.getString("keys", "");
                    /*if (!TextUtils.isEmpty(group)) {
                        //如果有历史记录，获取
                        String[] history = group.split(":@");
                        mBinding.searchView.addSuggestions(history);
                    }*/
                }
                break;
            case R.id.menu_download_manager:
                //打开下载列表
                intent = new Intent(this, OfflineComicDownloadActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_history:
                //历史
                intent = new Intent(this, PreferenceActivity.class);
                intent.setAction(PreferenceActivity.ACTION_HISTORY);
                startActivity(intent);
                break;
            case R.id.menu_setting:
                //设置页面
                intent = new Intent(this, PreferenceActivity.class);
                intent.setAction(PreferenceActivity.ACTION_ABOUT);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mBinding.searchView.isOpen()) {
            mBinding.searchView.closeSearch();
        } else {
            if (System.currentTimeMillis() - mLastBackPressedTime <= 2000) {
                super.onBackPressed();
            } else {
                mLastBackPressedTime = System.currentTimeMillis();
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> mFragments;
        FragmentManager fm;

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public TabFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fm = fm;
            this.mFragments = fragments;
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
