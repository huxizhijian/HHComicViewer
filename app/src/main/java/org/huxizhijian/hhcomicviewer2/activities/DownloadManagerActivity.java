package org.huxizhijian.hhcomicviewer2.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.DownloadManagerAdapter;
import org.huxizhijian.hhcomicviewer2.db.ComicCaptureDBHelper;
import org.huxizhijian.hhcomicviewer2.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer2.enities.Comic;
import org.huxizhijian.hhcomicviewer2.enities.ComicCapture;
import org.huxizhijian.hhcomicviewer2.service.DownloadManager;
import org.huxizhijian.hhcomicviewer2.service.DownloadManagerService;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadManagerActivity extends Activity implements View.OnClickListener {

    //控件
    private Button mBtn_all_control, mBtn_manager, mBtn_delete;
    private ExpandableListView mExpandableListView;
    private TextView mTv_storage_info;

    //数据
    private List<Comic> mDownloadedComicList;
    private Map<String, List<ComicCapture>> mDownloadedCaptureList;
    private DownloadManagerAdapter mAdapter;
    private List<String> mComicUrlList;

    //控制
    private DownloadReceiver mReceiver;
    private ComicDBHelper mComicDBHelper;
    private ComicCaptureDBHelper mComicCaptureDBHelper;
    private DownloadManager mDownloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
        initView();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.download_manager, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
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
                    mDownloadedCaptureList = mComicCaptureDBHelper.findDownloadCaptureMap(mDownloadedComicList);
                    mAdapter = new DownloadManagerAdapter(this, mDownloadedComicList, mDownloadedCaptureList);
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
                }
                return true;
            case R.id.menu_all_stop:
                //强制停止所有任务，没有反应时用
                if (mDownloadManager.hasMission()) {
                    Intent intent = new Intent(this, DownloadManagerService.class);
                    intent.setAction(DownloadManagerService.ACTION_ALL_STOP);
                    stopService(intent);
                    //取消所有通知
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancelAll();
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
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onResume() {
        super.onResume();
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


    private void initView() {
        mExpandableListView = (ExpandableListView) findViewById(R.id.elv_download_manager);
        mBtn_all_control = (Button) findViewById(R.id.btn_all_start_stop);
        mBtn_manager = (Button) findViewById(R.id.btn_download_manager);
        mBtn_delete = (Button) findViewById(R.id.btn_delete_download_manager);
        mTv_storage_info = (TextView) findViewById(R.id.textView_storage_space);

        //修改ActionBar
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            //增加左上角返回按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
            //修改ActionBar颜色
            BaseUtils.initActionBar(getActionBar(), Constants.THEME_COLOR);
        }
    }

    private void initData() {
        mReceiver = new DownloadReceiver();
        mComicDBHelper = ComicDBHelper.getInstance(this);
        mComicCaptureDBHelper = ComicCaptureDBHelper.getInstance(this);
        mDownloadedComicList = mComicDBHelper.findDownloadedComics();
        if (mDownloadedComicList != null) {
            mDownloadedCaptureList = mComicCaptureDBHelper.findDownloadCaptureMap(mDownloadedComicList);
            //初始化控件数据
            mAdapter = new DownloadManagerAdapter(this, mDownloadedComicList, mDownloadedCaptureList);
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
            if (sharedPreferences.getBoolean("default_open_all", false)) {
                //一开始就打开所有一级目录
                for (int i = 0; i < mDownloadedComicList.size(); i++) {
                    mExpandableListView.expandGroup(i);
                }
            }

            //设置按钮事件
            mBtn_all_control.setOnClickListener(this);
            mBtn_manager.setOnClickListener(this);
            mBtn_delete.setOnClickListener(this);
        } else {
            mAdapter = null;
            mExpandableListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_all_start_stop:
                //全部开始、全部停止
                if (mDownloadManager == null) {
                    mDownloadManager = DownloadManager.getInstance(DownloadManagerActivity.this);
                }
                if (mDownloadManager.hasMission()) {
                    //全部暂停
                    Intent intent = new Intent(this, DownloadManagerService.class);
                    intent.setAction(DownloadManagerService.ACTION_ALL_STOP);
                    startService(intent);
                    mBtn_all_control.setText("全部开始");
                } else {
                    //全部开始
                    Intent intent = new Intent(this, DownloadManagerService.class);
                    intent.setAction(DownloadManagerService.ACTION_ALL_START);
                    startService(intent);
                }
                break;
            case R.id.btn_download_manager:
                //管理
                if (mAdapter.isEditMode()) {
                    mBtn_delete.setVisibility(View.GONE);
                    mBtn_manager.setText("管理");
                    mAdapter.closeEditMode();
                } else {
                    mBtn_delete.setVisibility(View.VISIBLE);
                    mBtn_manager.setText("取消");
                    mAdapter.openEditMode();
                }
                break;
            case R.id.btn_delete_download_manager:
                //删除
                if (mAdapter.isEditMode()) {
                    List<ComicCapture> selectedCaptureList = mAdapter.getSelectedCaptures();
                    if (selectedCaptureList != null) {
                        mAdapter.delete();
                        for (ComicCapture capture : selectedCaptureList) {
                            Intent intent = new Intent(this, DownloadManagerService.class);
                            intent.setAction(DownloadManagerService.ACTION_DELETE);
                            intent.putExtra("comicCapture", capture);
                            startService(intent);
                        }
                    }
                }
                break;
        }
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
            if (mDownloadedComicList == null) return;
            if (mLastUpdateTime == 0) {
                mLastUpdateTime = System.currentTimeMillis();
            }

            ComicCapture comicCapture = (ComicCapture) intent.getSerializableExtra("comicCapture");

            /*//控制刷新间隔不少于400ms，如果不是正在下载，还是要刷新状态
            if (System.currentTimeMillis() - mLastUpdateTime < 400 &&
                    comicCapture.getDownloadStatus() == Constants.DOWNLOAD_DOWNLOADING) return;*/

            int groupPosition = mComicUrlList.indexOf(comicCapture.getComicUrl());
            List<ComicCapture> captureList = mDownloadedCaptureList.get(comicCapture.getComicUrl());
            if (captureList == null) return;
            //替换list
            if (captureList.contains(comicCapture)) {
                //如果存在，直接替换
                captureList.set(captureList.indexOf(comicCapture), comicCapture);
            } else {
                //没有找到符合的capture，说明这是新的任务，将其加入队列
                captureList.add(comicCapture);
            }

            mAdapter.setDownloadedCaptureList(mDownloadedCaptureList);
            mAdapter.resetCheckStatus();
            mAdapter.notifyDataSetChanged();
            mExpandableListView.collapseGroup(groupPosition);
            mExpandableListView.expandGroup(groupPosition);
            //更新上次刷新时间
            mLastUpdateTime = System.currentTimeMillis();

            //button控件信息修改
            if (mDownloadManager == null) {
                mDownloadManager = DownloadManager.getInstance(DownloadManagerActivity.this);
            }
            if (mDownloadManager.hasMission()) {
                mBtn_all_control.setText("全部暂停");
            } else {
                mBtn_all_control.setText("全部开始");
            }
        }
    }
}
