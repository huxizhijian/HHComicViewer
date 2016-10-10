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

package org.huxizhijian.hhcomicviewer2.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.fragment.ConfigFragment;
import org.huxizhijian.hhcomicviewer2.fragment.MarkedFragment;
import org.huxizhijian.hhcomicviewer2.fragment.SearchFragment;
import org.huxizhijian.hhcomicviewer2.service.DownloadManagerService;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.view.ChangeColorIconWithText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener {

    //控件及适配器
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;

    //数据
    private List<Fragment> mTags = new ArrayList<>(); //各tab的fragment
    private List<ChangeColorIconWithText> mTabIndicator = new ArrayList<>();  //viewPager指示器
    private long mLastBackPressedTime = 0; //用于用户不误操作退出键

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initFragment();
        mViewPager.setAdapter(mAdapter);
        initEvent();
        checkFirstRun();
    }

    private void checkFirstRun() {
        //检测是不是第一次运行
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        if (isFirstRun) {
            //第一次运行，使用manager的默认值
            PreferenceManager.setDefaultValues(this, R.xml.about_preferences, false);
            PreferenceManager.setDefaultValues(this, R.xml.download_preferences, false);
            PreferenceManager.setDefaultValues(this, R.xml.reading_preferences, false);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstRun", false);
            editor.apply();
        }
    }

    private void initFragment() {
        MarkedFragment markedFragment = new MarkedFragment();
        mTags.add(markedFragment);
        SearchFragment searchFragment = new SearchFragment();
        mTags.add(searchFragment);
        ConfigFragment configFragment = new ConfigFragment();
        mTags.add(configFragment);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mTags.get(position);
            }

            @Override
            public int getCount() {
                return mTags.size();
            }
        };
    }

    private void initEvent() {
        mTabIndicator.get(0).setOnClickListener(this);
        mTabIndicator.get(1).setOnClickListener(this);
        mTabIndicator.get(2).setOnClickListener(this);
        mViewPager.addOnPageChangeListener(this);
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager_main);
        ChangeColorIconWithText marked = (ChangeColorIconWithText) findViewById(R.id.indicator_marked);
        ChangeColorIconWithText search = (ChangeColorIconWithText) findViewById(R.id.indicator_search);
        ChangeColorIconWithText config = (ChangeColorIconWithText) findViewById(R.id.indicator_config);
        mTabIndicator.add(marked);
        mTabIndicator.add(search);
        mTabIndicator.add(config);
        marked.setIconAlpha(1.0f);

        //toolbar的设置
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name_short);
        //将其当成actionbar
        setSupportActionBar(toolbar);
        BaseUtils.setStatusBarTint(this, getResources().getColor(R.color.colorPrimaryDark));
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
            case R.id.menu_popup_sync:
                //刷新数据
                ((MarkedFragment) mTags.get(0)).refreshData();
                return true;
            case R.id.menu_download_list:
                intent = new Intent(MainActivity.this, DownloadManagerActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_config:
                intent = new Intent(MainActivity.this, PreferenceActivity.class);
                intent.setAction(PreferenceActivity.ACTION_READING);
                startActivity(intent);
                return true;
            case R.id.menu_exit:
                intent = new Intent(MainActivity.this, DownloadManagerService.class);
                intent.setAction(DownloadManagerService.ACTION_ALL_STOP);
                startService(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        resetOtherTabs();

        switch (view.getId()) {
            case R.id.indicator_marked:
                mTabIndicator.get(0).setIconAlpha(1.0f);
                //不适用动画
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.indicator_search:
                mTabIndicator.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.indicator_config:
                mTabIndicator.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mLastBackPressedTime <= 2000) {
            super.onBackPressed();
        } else {
            mLastBackPressedTime = System.currentTimeMillis();
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
        }
    }

    //重置其他Tab的颜色
    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicator.size(); i++) {
            mTabIndicator.get(i).setIconAlpha(0.0f);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset > 0) {
            ChangeColorIconWithText left = mTabIndicator.get(position);
            ChangeColorIconWithText right = mTabIndicator.get(position + 1);
            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
