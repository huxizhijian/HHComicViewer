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

package org.huxizhijian.hhcomicviewer.view;

import android.os.Bundle;
import android.view.MenuItem;

import com.blankj.utilcode.util.StringUtils;
import com.google.android.material.navigation.NavigationView;

import org.huxizhijian.hhcomic.model.comic.config.SourceConfig;
import org.huxizhijian.hhcomic.viewmodel.HomeViewModel;
import org.huxizhijian.hhcomicviewer.R;
import org.huxizhijian.hhcomicviewer.view.base.ComicActivity;
import org.huxizhijian.hhcomicviewer.view.fragment.home.HomeFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

/**
 * @author huxizhijian
 * @date 2018/11/20
 */
public class MainActivity extends ComicActivity<HomeViewModel> implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void dataObserver() {
        super.dataObserver();
        mViewModel.getSourceInfo(getCurrentSourceKey(mViewModel.getSourceConfigs()));
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, homeFragment);
        ft.commit();
    }

    private String getCurrentSourceKey(List<SourceConfig> sourceConfigs) {
        String sourceKey = mViewModel.getConfigUtil().getLastSourceKey();
        if (StringUtils.isEmpty(sourceKey)) {
            SourceConfig sourceConfig = sourceConfigs.get(0);
            sourceKey = sourceConfig.getSourceKey();
        }
        return sourceKey;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    /**
     * 开启抽屉，用于Fragment调用
     */
    public void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START, true);
    }
}
