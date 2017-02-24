package org.huxizhijian.hhcomicviewer2.ui.recommend;

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

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.databinding.ActivityComicShowBinding;
import org.huxizhijian.hhcomicviewer2.utils.CommonUtils;

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

        mBinding.tabs.setTabTextColors(getResources().getColor(R.color.dark_icon),
                getResources().getColor(R.color.colorAccent));
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
