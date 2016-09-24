package org.huxizhijian.hhcomicviewer2.activities;


import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.fragment.ConfigFragment;
import org.huxizhijian.hhcomicviewer2.fragment.HistoryFragment;
import org.huxizhijian.hhcomicviewer2.fragment.MarkedFragment;
import org.huxizhijian.hhcomicviewer2.fragment.SearchFragment;
import org.huxizhijian.hhcomicviewer2.service.DownloadService;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.view.ChangeColorIconWithText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

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
    }

    private void initFragment() {
        MarkedFragment markedFragment = new MarkedFragment();
        mTags.add(markedFragment);
        HistoryFragment historyFragment = new HistoryFragment();
        mTags.add(historyFragment);
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
        mTabIndicator.get(3).setOnClickListener(this);
        mViewPager.addOnPageChangeListener(this);
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager_main);
        ChangeColorIconWithText marked = (ChangeColorIconWithText) findViewById(R.id.indicator_marked);
        ChangeColorIconWithText history = (ChangeColorIconWithText) findViewById(R.id.indicator_history);
        ChangeColorIconWithText search = (ChangeColorIconWithText) findViewById(R.id.indicator_search);
        ChangeColorIconWithText config = (ChangeColorIconWithText) findViewById(R.id.indicator_config);
        mTabIndicator.add(marked);
        mTabIndicator.add(history);
        mTabIndicator.add(search);
        mTabIndicator.add(config);

        marked.setIconAlpha(1.0f);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(false);
            BaseUtils.initActionBar(actionBar, Constants.THEME_COLOR);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_popup_sync:
                //刷新数据
                ((MarkedFragment) mTags.get(0)).refreshData();
                ((HistoryFragment) mTags.get(1)).refreshData();
                return true;
            case R.id.menu_download_list:
                Intent intent = new Intent(this, DownloadManagerActivity.class);
                startActivity(intent);
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
            case R.id.indicator_history:
                mTabIndicator.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.indicator_search:
                mTabIndicator.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
            case R.id.indicator_config:
                mTabIndicator.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3, false);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //DownloadService自行检查是否要退出
        Intent intent = new Intent(this, DownloadService.class);
        intent.setAction(DownloadService.ACTION_CHECK_MISSION);
        startService(intent);
    }
}
