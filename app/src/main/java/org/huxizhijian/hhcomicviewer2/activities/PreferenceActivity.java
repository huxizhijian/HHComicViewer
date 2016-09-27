package org.huxizhijian.hhcomicviewer2.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.fragment.AboutSettingFragment;
import org.huxizhijian.hhcomicviewer2.fragment.AdvanceSettingFragment;
import org.huxizhijian.hhcomicviewer2.fragment.DownloadSettingFragment;
import org.huxizhijian.hhcomicviewer2.fragment.HistoryFragment;
import org.huxizhijian.hhcomicviewer2.fragment.ReadingSettingFragment;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;

public class PreferenceActivity extends Activity {

    public static final String ACTION_HISTORY = "ACTION_HISTORY";
    public static final String ACTION_READING = "ACTION_READING";
    public static final String ACTION_ADVANCE = "ACTION_ADVANCE";
    public static final String ACTION_DOWNLOAD = "ACTION_DOWNLOAD";
    public static final String ACTION_ABOUT = "ACTION_ABOUT";

    FragmentTransaction mFt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        //修改ActionBar
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            //增加左上角返回按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
            //修改ActionBar颜色
            BaseUtils.initActionBar(getActionBar(), Constants.THEME_COLOR);
        }
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
                DownloadSettingFragment downloadSettingFragment = new DownloadSettingFragment();
                mFt = getFragmentManager().beginTransaction();
                mFt.replace(R.id.frame_activity_preference, downloadSettingFragment);
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
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFt = null;
    }
}
