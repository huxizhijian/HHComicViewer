package org.huxizhijian.hhcomicviewer2.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.fragment.AboutSettingFragment;
import org.huxizhijian.hhcomicviewer2.fragment.AdvanceSettingFragment;
import org.huxizhijian.hhcomicviewer2.fragment.DownloadSettingFragment;
import org.huxizhijian.hhcomicviewer2.fragment.HistoryFragment;
import org.huxizhijian.hhcomicviewer2.fragment.ReadingSettingFragment;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;

public class PreferenceActivity extends AppCompatActivity {

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

        //toolbar的设置
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_black_24dp);
        //将其当成actionbar
        setSupportActionBar(toolbar);
        BaseUtils.setStatusBarTint(this, getResources().getColor(R.color.colorPrimaryDark));

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
}
