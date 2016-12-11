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

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.DownloadManagerAdapter;
import org.huxizhijian.hhcomicviewer2.db.ComicChapterDBHelper;
import org.huxizhijian.hhcomicviewer2.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer2.enities.Comic;
import org.huxizhijian.hhcomicviewer2.enities.ComicChapter;
import org.huxizhijian.hhcomicviewer2.service.DownloadManager;
import org.huxizhijian.hhcomicviewer2.service.DownloadManagerService;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadManagerActivity extends AppCompatActivity implements View.OnClickListener {

    //控件
    private Button mBtn_all_control, mBtn_manager, mBtn_delete;
    private ExpandableListView mExpandableListView;
    private TextView mTv_storage_info;

    //数据
    private List<Comic> mDownloadedComicList;
    private Map<String, List<ComicChapter>> mDownloadedChapterList;
    private DownloadManagerAdapter mAdapter;
    private List<String> mComicUrlList;

    private String mDownloadPath;

    //控制
    private DownloadReceiver mReceiver;
    private ComicDBHelper mComicDBHelper;
    private ComicChapterDBHelper mComicChapterDBHelper;
    private DownloadManager mDownloadManager;
    private boolean mManagerBackGroundDoing = false;
    private boolean mDefaultExpandAll = false; //是否默认打开所有
    private long mLastAllControlPressed;

    public boolean mHasWritePermission = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
        initView();
        checkPermission();

        //显示剩余控件
        if (mHasWritePermission) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            mDownloadPath = sharedPreferences.getString("download_path", Constants.DEFAULT_DOWNLOAD_PATH);
            mTv_storage_info.setText(BaseUtils.getStorageBlockSpace(mDownloadPath));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.download_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        switch (item.getItemId()) {
            case R.id.menu_manager_sync:
                //重置界面（较为耗费资源）
                mAdapter = null;
                mExpandableListView.setVisibility(View.GONE);
                mDownloadedComicList = mComicDBHelper.findDownloadedComics();
                if (mDownloadedComicList != null && mDownloadedComicList.size() != 0) {
                    mDownloadedChapterList = mComicChapterDBHelper.findDownloadChapterMap(mDownloadedComicList);
                    mAdapter = new DownloadManagerAdapter(this, mDownloadedComicList, mDownloadedChapterList);
                    mComicUrlList = new ArrayList<>();
                    for (Comic comic : mDownloadedComicList) {
                        mComicUrlList.add(comic.getComicUrl());
                    }
                    mAdapter.setOnNotifyDataSetChanged(new DownloadManagerAdapter.OnNotifyDataSetChanged() {
                        @Override
                        public void onNotify() {
                            for (int i = 0; i < mDownloadedComicList.size(); i++) {
                                mExpandableListView.collapseGroup(i);
                                mExpandableListView.expandGroup(i);
                            }
                        }
                    });
                    mExpandableListView.setAdapter(mAdapter);
                    mExpandableListView.setVisibility(View.VISIBLE);
                    if (mDefaultExpandAll) {
                        for (int i = 0; i < mDownloadedComicList.size(); i++) {
                            mExpandableListView.expandGroup(i);
                        }
                    }
                }
                return true;
            case R.id.menu_all_stop:
                //强制停止所有任务，没有反应时用
                if (mDownloadManager.hasMission()) {
                    mDownloadManager = null;
                    Intent intent = new Intent(this, DownloadManagerService.class);
                    intent.setAction(DownloadManagerService.ACTION_ALL_STOP);
                    stopService(intent);
                    //取消所有通知
                    NotificationManager manager = (NotificationManager)
                            getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancelAll();
                }
                System.gc();
                if (mDownloadManager == null) {
                    mDownloadManager = DownloadManager.getInstance(getApplicationContext());
                }
                return true;
            case R.id.menu_download_setting:
                //打开下载设置
                Intent intent = new Intent(this, PreferenceActivity.class);
                intent.setAction(PreferenceActivity.ACTION_DOWNLOAD);
                startActivity(intent);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //加载数据
        initData();
        //注册广播监听
        mReceiver = new DownloadReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManagerService.ACTION_RECEIVER);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //取消广播监听
        unregisterReceiver(mReceiver);
    }

    public void checkPermission() {
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
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    mHasWritePermission = true;
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    mHasWritePermission = false;
                    return;
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initView() {
        mExpandableListView = (ExpandableListView) findViewById(R.id.elv_download_manager);
        mBtn_all_control = (Button) findViewById(R.id.btn_all_start_stop);
        mBtn_manager = (Button) findViewById(R.id.btn_download_manager);
        mBtn_delete = (Button) findViewById(R.id.btn_delete_download_manager);
        mTv_storage_info = (TextView) findViewById(R.id.textView_storage_space);

        //toolbar的设置
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.download);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_black_24dp);
        //将其当成actionbar
        setSupportActionBar(toolbar);
        BaseUtils.setStatusBarTint(this, getResources().getColor(R.color.colorPrimaryDark));
    }

    private void initData() {
        mReceiver = new DownloadReceiver();
        mComicDBHelper = ComicDBHelper.getInstance(this);
        mComicChapterDBHelper = ComicChapterDBHelper.getInstance(this);
        mDownloadedComicList = mComicDBHelper.findDownloadedComics();
        if (mDownloadManager == null) {
            mDownloadManager = DownloadManager.getInstance(getApplicationContext());
        }
        if (mDownloadManager.hasMission()) {
            mBtn_all_control.setText("全部暂停");
        } else {
            mBtn_all_control.setText("全部开始");
        }

        if (mDownloadedComicList != null) {
            mDownloadedChapterList = mComicChapterDBHelper.findDownloadChapterMap(mDownloadedComicList);
            //初始化控件数据
            mAdapter = new DownloadManagerAdapter(this, mDownloadedComicList, mDownloadedChapterList);
            mExpandableListView.setAdapter(mAdapter);
            //初始化ComicList
            mComicUrlList = new ArrayList<>();
            for (Comic comic : mDownloadedComicList) {
                mComicUrlList.add(comic.getComicUrl());
            }
            mAdapter.setOnNotifyDataSetChanged(new DownloadManagerAdapter.OnNotifyDataSetChanged() {
                @Override
                public void onNotify() {
                    for (int i = 0; i < mDownloadedComicList.size(); i++) {
                        mExpandableListView.collapseGroup(i);
                        mExpandableListView.expandGroup(i);
                    }
                }
            });

            //加载用户设置
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            mDefaultExpandAll = sharedPreferences.getBoolean("default_open_all", false);
            if (mDefaultExpandAll) {
                //一开始就打开所有一级目录
                for (int i = 0; i < mDownloadedComicList.size(); i++) {
                    mExpandableListView.expandGroup(i);
                }
            }

            mExpandableListView.setVisibility(View.VISIBLE);
            mBtn_all_control.setVisibility(View.VISIBLE);
            mBtn_manager.setVisibility(View.VISIBLE);

            //设置按钮事件
            mBtn_all_control.setOnClickListener(this);
            mBtn_manager.setOnClickListener(this);
            mBtn_delete.setOnClickListener(this);
        } else {
            mAdapter = null;
            mExpandableListView.setVisibility(View.GONE);
            mBtn_all_control.setVisibility(View.GONE);
            mBtn_manager.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_all_start_stop:
                //全部开始、全部停止
                if (System.currentTimeMillis() - mLastAllControlPressed < 2000) return;
                if (!mHasWritePermission) return;
                if (mManagerBackGroundDoing) return;

                if (mDownloadManager.hasMission()) {
                    //全部暂停
                    Intent intent = new Intent(this, DownloadManagerService.class);
                    intent.setAction(DownloadManagerService.ACTION_ALL_STOP);
                    startService(intent);
                    mBtn_all_control.setText("全部开始");
                    mManagerBackGroundDoing = true;
                } else {
                    //全部开始
                    Intent intent = new Intent(this, DownloadManagerService.class);
                    intent.setAction(DownloadManagerService.ACTION_ALL_START);
                    startService(intent);
                    mManagerBackGroundDoing = true;
                }
                mLastAllControlPressed = System.currentTimeMillis();
                break;
            case R.id.btn_download_manager:
                //管理
                if (mAdapter.isEditMode()) {
                    mBtn_delete.setVisibility(View.GONE);
                    mBtn_manager.setText("管理");
                    mAdapter.closeEditMode();
                } else {
                    editModeOpen();
                }
                break;
            case R.id.btn_delete_download_manager:
                //删除
                if (!mHasWritePermission) return;
                if (mAdapter.isEditMode()) {
                    List<ComicChapter> selectedChapterList = mAdapter.getSelectedChapters();
                    if (selectedChapterList != null) {
                        mAdapter.delete();
                        for (ComicChapter chapter : selectedChapterList) {
                            Intent intent = new Intent(this, DownloadManagerService.class);
                            intent.setAction(DownloadManagerService.ACTION_DELETE);
                            intent.putExtra("comicChapter", chapter);
                            startService(intent);
                        }
                    }
                }
                break;
        }
    }

    public void editModeOpen() {
        mBtn_delete.setVisibility(View.VISIBLE);
        mBtn_manager.setText("取消");
        mAdapter.openEditMode();
    }

    public boolean isManagerBackGroundDoing() {
        return mManagerBackGroundDoing;
    }

    public void setManagerBackGroundDoing(boolean managerBackGroundDoing) {
        mManagerBackGroundDoing = managerBackGroundDoing;
    }

    @Override
    public void onBackPressed() {
        if (mAdapter != null && mAdapter.isEditMode()) {
            mBtn_delete.setVisibility(View.GONE);
            mBtn_manager.setText("管理");
            mAdapter.closeEditMode();
        } else {
            super.onBackPressed();
        }
    }

    class DownloadReceiver extends BroadcastReceiver {

        private long mLastUpdateTime;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mComicUrlList == null || mDownloadedComicList == null) return;
            if (mLastUpdateTime == 0) {
                mLastUpdateTime = System.currentTimeMillis();
            }

            mManagerBackGroundDoing = false;

            ComicChapter comicChapter = (ComicChapter) intent.getSerializableExtra("comicChapter");

            /*//控制刷新间隔不少于400ms，如果不是正在下载，还是要刷新状态
            if (System.currentTimeMillis() - mLastUpdateTime < 400 &&
                    comicChapter.getDownloadStatus() == Constants.DOWNLOAD_DOWNLOADING) return;*/

            int groupPosition = mComicUrlList.indexOf(comicChapter.getComicUrl());
            List<ComicChapter> chapterList = mDownloadedChapterList.get(comicChapter.getComicUrl());
            if (chapterList == null) return;
            //替换list
            if (chapterList.contains(comicChapter)) {
                //如果存在，直接替换
                chapterList.set(chapterList.indexOf(comicChapter), comicChapter);
            } else {
                //没有找到符合的chapter，说明这是新的任务，将其加入队列
                chapterList.add(comicChapter);
            }

            mAdapter.setDownloadedchapterList(mDownloadedChapterList);
//            mAdapter.resetCheckStatus();
            mAdapter.notifyDataSetChanged();
            mExpandableListView.collapseGroup(groupPosition);
            mExpandableListView.expandGroup(groupPosition);
            //更新上次刷新时间
            mLastUpdateTime = System.currentTimeMillis();

            //更新显示SD卡剩余空间
            if (mHasWritePermission) {
                mTv_storage_info.setText(BaseUtils.getStorageBlockSpace(mDownloadPath));
            }

            //button控件信息修改
            if (mDownloadManager == null) {
                mDownloadManager = DownloadManager.getInstance(getApplicationContext());
            }
            if (mDownloadManager.hasMission()) {
                mBtn_all_control.setText("全部暂停");
            } else {
                mBtn_all_control.setText("全部开始");
            }
        }
    }
}
