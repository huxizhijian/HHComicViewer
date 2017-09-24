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

package org.huxizhijian.hhcomicviewer2.ui.user;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.ui.entry.HistoryFragment;
import org.huxizhijian.hhcomicviewer2.utils.CommonUtils;

import java.io.IOException;

public class PreferenceActivity extends AppCompatActivity {

    public static final String ACTION_HISTORY = "ACTION_HISTORY";
    public static final String ACTION_READING = "ACTION_READING";
    public static final String ACTION_ADVANCE = "ACTION_ADVANCE";
    public static final String ACTION_DOWNLOAD = "ACTION_DOWNLOAD";
    public static final String ACTION_ABOUT = "ACTION_ABOUT";

    public static final int OPEN_DIALOG = 0x00;
    public static final int MAKE_NO_MEDIA = 0x01;

    FragmentTransaction mFt;

    DownloadSettingFragment mDownloadSettingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        //toolbar的设置
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //将其当成actionbar
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_padded);
        }
        CommonUtils.setStatusBarTint(this, getResources().getColor(R.color.colorPrimaryDark));

        String action = getIntent().getAction();
        switch (action) {
            case ACTION_HISTORY:
                //打开历史
                setTitle("观看历史");
                HistoryFragment historyFragment = new HistoryFragment();
                mFt = getFragmentManager().beginTransaction();
                mFt.replace(R.id.frame_activity_preference, historyFragment);
                mFt.commit();
                break;
            case ACTION_READING:
                //打开阅读设置
                setTitle("阅读");
                ReadingSettingFragment readingSettingFragment = new ReadingSettingFragment();
                mFt = getFragmentManager().beginTransaction();
                mFt.replace(R.id.frame_activity_preference, readingSettingFragment);
                mFt.commit();
                break;
            case ACTION_ADVANCE:
                //打开高级设置
                setTitle("高级");
                AdvanceSettingFragment advanceSettingFragment = new AdvanceSettingFragment();
                mFt = getFragmentManager().beginTransaction();
                mFt.replace(R.id.frame_activity_preference, advanceSettingFragment);
                mFt.commit();
                break;
            case ACTION_DOWNLOAD:
                //打开下载设置
                setTitle("下载");
                mDownloadSettingFragment = new DownloadSettingFragment();
                mFt = getFragmentManager().beginTransaction();
                mFt.replace(R.id.frame_activity_preference, mDownloadSettingFragment);
                mFt.commit();
                break;
            case ACTION_ABOUT:
                //打开关于
                setTitle("关于");
                AboutSettingFragment aboutSettingFragment = new AboutSettingFragment();
                mFt = getFragmentManager().beginTransaction();
                mFt.replace(R.id.frame_activity_preference, aboutSettingFragment);
                mFt.commit();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFt = null;
    }

    public void checkPermission(int method) {
        //检查自身权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "没有权限，无法完成操作！", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, method);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, method);
            }
        } else {
            if (method == OPEN_DIALOG) {
                //打开dialog
                if (mDownloadSettingFragment != null) {
                    mDownloadSettingFragment.openDirectChooserDialog();
                }
            } else if (method == MAKE_NO_MEDIA) {
                //创建.nomedia文件
                try {
                    CommonUtils.createNomediaIfAllow(getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case OPEN_DIALOG:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    //打开dialog
                    if (mDownloadSettingFragment != null) {
                        mDownloadSettingFragment.openDirectChooserDialog();
                    }
                    //创建.nomedia文件
                    try {
                        CommonUtils.createNomediaIfAllow(getApplicationContext());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    return;
                }
                break;
            case MAKE_NO_MEDIA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    //创建.nomedia文件
                    try {
                        CommonUtils.createNomediaIfAllow(getApplicationContext());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    return;
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
