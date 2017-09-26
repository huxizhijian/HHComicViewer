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

package org.huxizhijian.hhcomicviewer.ui.download;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.Visibility;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer.R;
import org.huxizhijian.hhcomicviewer.db.ComicChapterDBHelper;
import org.huxizhijian.hhcomicviewer.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer.model.ComicChapter;
import org.huxizhijian.hhcomicviewer.service.DownloadManagerService;
import org.huxizhijian.hhcomicviewer.ui.download.listener.OnEditModeListener;
import org.huxizhijian.hhcomicviewer.ui.user.PreferenceActivity;
import org.huxizhijian.hhcomicviewer.utils.CommonUtils;
import org.huxizhijian.hhcomicviewer.utils.Constants;

import java.io.IOException;

/**
 * 基础下载显示类，处理公有方法
 * Created by wei on 2017/2/6.
 */

public abstract class OfflineDownloadBaseActivity extends AppCompatActivity implements OnEditModeListener {

    protected boolean mHasWritePermission = true; //是否具有下载权限
    protected String mDownloadPath; //下载路径

    protected ComicDBHelper mComicDBHelper; //Comic数据库操作类
    protected ComicChapterDBHelper mComicChapterDBHelper; //ComicChapter数据库操作类

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //进行权限判断
        checkPermission();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mDownloadPath = sharedPreferences.getString("download_path", Constants.DEFAULT_DOWNLOAD_PATH);
        mComicDBHelper = ComicDBHelper.getInstance(this);
        mComicChapterDBHelper = ComicChapterDBHelper.getInstance(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected Visibility buildEnterTransition() {
        Slide enterTransition = new Slide();
        enterTransition.setDuration(getResources().getInteger(R.integer.anim_duration_medium));
        enterTransition.setSlideEdge(Gravity.RIGHT); //从右边滑动进入
        return enterTransition;
    }

    private void checkPermission() {
        //检查自身权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "没有权限，无法进行下载管理", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
            mHasWritePermission = false;
        } else {
            mHasWritePermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        //权限确认
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    mHasWritePermission = true;
                    //创建.nomedia文件
                    try {
                        CommonUtils.createNomediaIfAllow(getApplicationContext());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    mHasWritePermission = false;
                    return;
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if (isEditModeOpen()) {
            editModeClose();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.download_manager, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isEditModeOpen()) {
            menu.findItem(R.id.menu_edit_mode).setVisible(false);
            menu.findItem(R.id.menu_download_setting).setVisible(false);
            menu.findItem(R.id.menu_edit_confirm).setVisible(true);
        } else {
            menu.findItem(R.id.menu_edit_mode).setVisible(true);
            menu.findItem(R.id.menu_download_setting).setVisible(true);
            menu.findItem(R.id.menu_edit_confirm).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_edit_mode:
                if (!isEditModeOpen()) {
                    editModeOpen();
                }
                break;
            case R.id.menu_edit_confirm:
                if (isEditModeOpen()) {
                    editModeClose();
                }
                break;
            case R.id.menu_download_setting:
                Intent intent = new Intent(this, PreferenceActivity.class);
                intent.setAction(PreferenceActivity.ACTION_DOWNLOAD);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    protected void initSupportAppBar(String title) {
        CommonUtils.setStatusBarTint(this, getResources().getColor(R.color.colorPrimaryDark));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }
    }

    //singleTask调用方法
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData();
        initView();
    }

    protected abstract void editModeOpen();

    protected abstract void editModeClose();

    protected abstract boolean isEditModeOpen();

    /**
     * onResume方法执行，用于跳转返回后更新数据
     */
    protected abstract void initData();

    /**
     * onResume方法执行，用于跳转返回后更新view
     */
    protected abstract void initView();

    /**
     * 在接收到广播后调用
     *
     * @param downloadState
     * @param comicChapter
     */
    protected abstract void onDownloadStateChanged(int downloadState, ComicChapter comicChapter);

    private DownloadStateChangedReceiver mReceiver = new DownloadStateChangedReceiver();

    //下载状态改变广播接收器
    class DownloadStateChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ComicChapter comicChapter = (ComicChapter) intent.getSerializableExtra("comicChapter");
            int state = intent.getIntExtra("state", Constants.DOWNLOAD_INIT);
            onDownloadStateChanged(state, comicChapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(DownloadManagerService.ACTION_RECEIVER);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
        initData();
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

}
