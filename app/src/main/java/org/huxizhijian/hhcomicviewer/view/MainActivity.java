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
