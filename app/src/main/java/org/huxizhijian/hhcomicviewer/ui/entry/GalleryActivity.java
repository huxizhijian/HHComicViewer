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

package org.huxizhijian.hhcomicviewer.ui.entry;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer.R;
import org.huxizhijian.hhcomicviewer.adapter.GalleryListViewAdapter;
import org.huxizhijian.hhcomicviewer.adapter.GalleryViewPagerAdapter;
import org.huxizhijian.hhcomicviewer.db.ComicChapterDBHelper;
import org.huxizhijian.hhcomicviewer.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer.model.Comic;
import org.huxizhijian.hhcomicviewer.model.ComicChapter;
import org.huxizhijian.hhcomicviewer.presenter.implpersenter.ComicChapterPresenterImpl;
import org.huxizhijian.hhcomicviewer.presenter.viewinterface.IComicChapterListener;
import org.huxizhijian.hhcomicviewer.utils.CommonUtils;
import org.huxizhijian.hhcomicviewer.utils.Constants;
import org.huxizhijian.hhcomicviewer.view.OpenMenuFrameLayout;
import org.huxizhijian.hhcomicviewer.view.ZoomableListView;
import org.huxizhijian.hhcomicviewer.view.listener.OnCenterTapListener;
import org.huxizhijian.hhcomicviewer.view.listener.OnLeftOrRightTapListener;
import org.huxizhijian.sdk.sharedpreferences.SharedPreferencesManager;

import java.lang.ref.WeakReference;


public class GalleryActivity extends Activity implements View.OnClickListener,
        IComicChapterListener, OnCenterTapListener {

    //comic信息
    private ComicChapter mComicChapter;
    private Comic mComic;
    private int mChapterPosition;
    private int mPosition; //漫画当前的position

    //使用listView或者viewPager
    private int mReadingDirection = VIEW_PAGER;

    private final static int VIEW_PAGER = 0;
    private final static int LIST_VIEW = 1;

    //网络提供者
    ComicChapterPresenterImpl mPresenter = new ComicChapterPresenterImpl(this);

    //数据库操作
    private ComicChapterDBHelper mChapterDBHelper;

    //控件
    private OpenMenuFrameLayout mFrameLayout;
    private ZoomableListView mListView;
    private ViewPager mViewPager;
    private GalleryViewPagerAdapter mViewPagerAdapter;
    private GalleryListViewAdapter mListViewAdapter;
    private ProgressBar mProgressBar;
    private TextView mTv_position;

    //菜单及其上的控件
    private RelativeLayout mMenu;
    private SeekBar mSeekBar;
    private TextView mTv_name, mTv_progress, mTv_time;
    private ImageView mIv_battery;
    private boolean mIsMenuOpen = false; //菜单是否打开

    //更新电池电量
    private BatteryBroadcastReceiver batteryBroadcastReceiver;

    //使用handler循环更新时间
    private static final int UPDATE_TIME = 0x9;

    private static class MyHandler extends Handler {
        //使用弱引用，并且声明为静态内部类，否则会造成leak
        WeakReference<TextView> mOut;

        MyHandler(TextView tv_time) {
            mOut = new WeakReference<>(tv_time);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UPDATE_TIME) {
                TextView tv_time = mOut.get();
                if (tv_time != null) {
                    tv_time.setText(CommonUtils.getNowDate());
                }
                sendEmptyMessageDelayed(UPDATE_TIME, 10000);
            }
        }
    }

    //用户设置
    private boolean loadOnLineFullSizeImage = false; //在线阅读下载全尺寸图片
    private boolean useVolButtonChangePage = false; //使用音量键翻页
    private boolean mIsCenterPositionVisible = true; //正中央页码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mComic = (Comic) getIntent().getSerializableExtra("comic");
        mChapterPosition = getIntent().getIntExtra("position", 0);
        initMenu();
        initView();
        getWebContent();
    }

    private void getWebContent() {
        if (mChapterPosition == -1) {
            mChapterPosition = mComic.getReadChapter();
        }
        long chapterId = mComic.getChapterId().get(mChapterPosition);
        if (mChapterDBHelper == null) {
            mChapterDBHelper = ComicChapterDBHelper.getInstance(this);
        }
        mComicChapter = null;
        mComicChapter = mChapterDBHelper.findByChapterId(chapterId);
        mViewPager.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        if (mComicChapter != null && mComicChapter.getDownloadStatus() == Constants.DOWNLOAD_FINISHED) {
            //如果已经下载完毕
            if (mReadingDirection == VIEW_PAGER) {
                initImageViewInViewPager();
            } else {
                initImageViewInListView();
            }
        } else {
            //否则在线看
            ComicChapter comicChapter = new ComicChapter(mComic.getTitle(), mComic.getCid(),
                    mComic.getChapterId().get(mChapterPosition), mComic.getChapterName().get(mChapterPosition),
                    mComic.getServerId());
            mPresenter.getComicChapter(comicChapter);
        }
    }

    @Override
    public void onSuccess(ComicChapter comicChapter) {
        this.mComicChapter = comicChapter;
        runOnUiThread(() -> {
            if (mReadingDirection == VIEW_PAGER) {
                initImageViewInViewPager();
            } else {
                initImageViewInListView();
            }
        });
    }

    @Override
    public void onException(Throwable e, ComicChapter comicChapter) {
        Log.e("getWebContentAsyn", "onError: " + e.toString());
        if (CommonUtils.getAPNType(GalleryActivity.this) == CommonUtils.NONEWTWORK) {
            runOnUiThread(() -> Toast.makeText(GalleryActivity.this, Constants.NO_NETWORK, Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onFail(int errorCode, String errorMsg, ComicChapter comicChapter) {
        if (CommonUtils.getAPNType(GalleryActivity.this) == CommonUtils.NONEWTWORK) {
            runOnUiThread(() -> Toast.makeText(GalleryActivity.this, Constants.NO_NETWORK, Toast.LENGTH_SHORT).show());
        }
    }

    private void initImageViewInViewPager() {
        //使viewpager可见并设置监听
        mViewPager.setVisibility(View.VISIBLE);

        //加载各种数据
        mSeekBar.setMax(mComicChapter.getPageCount() - 1);
        mTv_name.setText(mComicChapter.getChapterName());
        if (mViewPagerAdapter == null) {
            mViewPagerAdapter = new GalleryViewPagerAdapter(this, loadOnLineFullSizeImage);
            mViewPagerAdapter.setComicChapter(mComicChapter);
        } else {
            mViewPagerAdapter.setComicChapter(mComicChapter);
            mViewPagerAdapter.notifyDataSetChanged();
        }

        //设置前后翻页监听
        mViewPagerAdapter.setOnLeftOrRightTapListener(new OnLeftOrRightTapListener() {
            @Override
            public void leftTap() {
                if (mPosition - 1 >= 0) {
                    mViewPager.setCurrentItem(mPosition - 1, false);
                } else {
                    //开启上一章
                    openPrevChapter();
                }
            }

            @Override
            public void rightTap() {
                if (mPosition + 1 < mComicChapter.getPageCount()) {
                    mViewPager.setCurrentItem(mPosition + 1, false);
                } else {
                    //开启下一章
                    openNextChapter();
                }
            }
        });

        mViewPagerAdapter.setOnCenterTapListener(this);

        //设置预读取2页的内容，默认数字为1
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mViewPagerAdapter);
        //将progressbar设置为不可见
        mProgressBar.setVisibility(View.GONE);
        if (mComic.getReadChapter() == mChapterPosition) {
            //设置读到的页数
            mViewPager.setCurrentItem(mComic.getReadPage(), false);
            mPosition = mComic.getReadPage();
        }
        //初始化页数
        mSeekBar.setProgress(mViewPager.getCurrentItem());
        mTv_progress.setText(mViewPager.getCurrentItem() + 1 + "/" + mComicChapter.getPageCount());
        mTv_position.setText(mViewPager.getCurrentItem() + 1 + "");

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                if (mSeekBar.getProgress() != position) {
                    mSeekBar.setProgress(position);
                }
                mTv_progress.setText(position + 1 + "/" + mComicChapter.getPageCount());
                mTv_position.setText(position + 1 + "");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initImageViewInListView() {
        //使listView可见并设置监听
        mListView.setVisibility(View.VISIBLE);
        mFrameLayout.setOnLeftOrRightTapListener(new OnLeftOrRightTapListener() {
            @Override
            public void leftTap() {
                if (mPosition - 1 >= 0) {
                    mPosition--;
                    mListView.smoothScrollToPosition(mPosition);
                } else {
                    //开启上一章
                    openPrevChapter();
                }
            }

            @Override
            public void rightTap() {
                if (mPosition + 1 < mComicChapter.getPageCount()) {
                    mPosition++;
                    mListView.smoothScrollToPosition(mPosition);
                } else {
                    //开启下一章
                    openNextChapter();
                }
            }
        });

        mFrameLayout.setOnCenterTapListener(this);

        //加载各种数据
        mSeekBar.setMax(mComicChapter.getPageCount() - 1);
        mTv_name.setText(mComicChapter.getChapterName());
        if (mListViewAdapter == null) {
            mListViewAdapter = new GalleryListViewAdapter(this, mComicChapter,
                    loadOnLineFullSizeImage, mIsCenterPositionVisible);
            mListView.setAdapter(mListViewAdapter);
        } else {
            mListViewAdapter.setComicChapter(mComicChapter);
            mListViewAdapter.notifyDataSetChanged();
        }
        //将progressbar设置为不可见
        mProgressBar.setVisibility(View.GONE);
        if (mComic.getReadChapter() == mChapterPosition) {
            //设置读到的页数
            mListView.smoothScrollToPosition(mComic.getReadChapter());
            mPosition = mComic.getReadChapter();
        }
        //初始化页数
        mSeekBar.setProgress(mListView.getFirstVisiblePosition());
        mTv_progress.setText(mListView.getFirstVisiblePosition() + 1 + "/" + mComicChapter.getPageCount());
        mTv_position.setText(mListView.getFirstVisiblePosition() + 1 + "");
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    mSeekBar.setProgress(mListView.getFirstVisiblePosition());
                    mTv_progress.setText(mListView.getFirstVisiblePosition() + 1 + "/" + mComicChapter.getPageCount());
                    mPosition = mListView.getLastVisiblePosition();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void initMenu() {
        mMenu = findViewById(R.id.menu_gallery);
        TextView btnPrev = findViewById(R.id.btn_prev_gallery);
        TextView btnNext = findViewById(R.id.btn_next_gallery);
        mSeekBar = findViewById(R.id.seekBar_gallery);
        mTv_name = findViewById(R.id.tv_name_gallery);
        mTv_progress = findViewById(R.id.tv_progress_gallery);
        mTv_time = findViewById(R.id.tv_time_gallery);
        mIv_battery = findViewById(R.id.iv_battery_gallery);
        mTv_position = findViewById(R.id.tv_position_gallery);

        //用户设置
        preferencesSet();

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mComicChapter != null && fromUser) {
                    mViewPager.setCurrentItem(progress, false);
                    mViewPager.clearAnimation();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        mTv_time.setText(CommonUtils.getNowDate());

        //设置10秒后更新时间
        MyHandler handler = new MyHandler(mTv_time);
        handler.sendEmptyMessageDelayed(UPDATE_TIME, 10000);
    }

    private void preferencesSet() {
        //获取设置文件
        SharedPreferencesManager preferencesManager = new SharedPreferencesManager(this);

        //对于四个控件的显示设置
        if (!preferencesManager.getBoolean("time_visible", true)) {
            //时间不显示
            mTv_time.setVisibility(View.GONE);
        }
        if (!preferencesManager.getBoolean("page_visible", true)) {
            //正上方页码不显示
            mTv_progress.setVisibility(View.GONE);
        }
        if (!preferencesManager.getBoolean("charge_visible", true)) {
            //电量不显示
            mIv_battery.setVisibility(View.GONE);
        }
        if (!preferencesManager.getBoolean("number_visible", false)) {
            //正中央页码不显示
            mIsCenterPositionVisible = false;
        }
        if (preferencesManager.getBoolean("keep_screen_on", false)) {
            //设置屏幕常亮
            mMenu.setKeepScreenOn(true);
        }
        loadOnLineFullSizeImage = preferencesManager.getBoolean("reading_full_size_image", false);
        useVolButtonChangePage = preferencesManager.getBoolean("use_volume_key", false);

        String rotate = preferencesManager.getString("reading_screen_rotate", "none");
        if ("portrait".equals(rotate)) {
            //设置屏幕方向为竖屏
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else if ("landscape".equals(rotate)) {
            //设置屏幕方向为横屏
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        //使用的控件
        String directionValue = preferencesManager.getString("reading_direction", "view_pager");
        if ("view_pager".equals(directionValue)) {
            mReadingDirection = VIEW_PAGER;
        } else if ("list_view".equals(directionValue)) {
            mReadingDirection = LIST_VIEW;
            mTv_position.setVisibility(View.GONE);
        }

    }

    private void initView() {
        mListView = findViewById(R.id.list_view_gallery);
        mViewPager = findViewById(R.id.viewPager_gallery);
        mProgressBar = findViewById(R.id.pg_loading_gallery);
        mFrameLayout = findViewById(R.id.frame_gallery);
    }

    @Override
    protected void onResume() {
        super.onResume();
        batteryBroadcastReceiver = new BatteryBroadcastReceiver();
        //创建一个过滤器
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(batteryBroadcastReceiver);
        mComic.setReadChapter(mChapterPosition);
        mComic.setReadPage(mViewPager.getCurrentItem());
        ComicDBHelper.getInstance(this).update(mComic);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_prev_gallery:
                openPrevChapter();
                break;
            case R.id.btn_next_gallery:
                openNextChapter();
                break;
            default:
                break;
        }
    }

    private void openPrevChapter() {
        if (mChapterPosition - 1 >= 0) {
            mChapterPosition--;
            mComic.setReadChapter(mChapterPosition);
            mComic.setReadPage(0);
            mViewPager.setCurrentItem(0, false);
            getWebContent();
        } else {
            Toast.makeText(GalleryActivity.this, "当前是第一话", Toast.LENGTH_SHORT).show();
        }
    }

    private void openNextChapter() {
        if (mChapterPosition + 1 < mComic.getChapterId().size()) {
            mChapterPosition++;
            mComic.setReadChapter(mChapterPosition);
            mComic.setReadPage(0);
            mViewPager.setCurrentItem(0, false);
            getWebContent();
        } else {
            Toast.makeText(GalleryActivity.this, "当前是最后一话", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        //override返回按钮按下方法
        if (mIsMenuOpen) {
            Animation animation = AnimationUtils.loadAnimation(GalleryActivity.this, R.anim.menu_hide_action);
            mMenu.clearAnimation();
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mMenu.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mMenu.setAnimation(animation);
            animation.start();
            mIsMenuOpen = false;
        } else {
            mComic.setReadChapter(mChapterPosition);
            mComic.setReadPage(mViewPager.getCurrentItem());
            Intent intent = new Intent();
            intent.putExtra("comic", mComic);
            setResult(0, intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.removeListener();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (useVolButtonChangePage) {
            //截取音量键事件
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if (mViewPagerAdapter != null) {
                        if (mViewPager.getCurrentItem() - 1 >= 0) {
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, false);
                        } else {
                            openPrevChapter();
                        }
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (mViewPagerAdapter != null) {
                        if (mViewPager.getCurrentItem() + 1 < mComicChapter.getPageCount()) {
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, false);
                        } else {
                            openNextChapter();
                        }
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (useVolButtonChangePage) {
            //截取音量键事件
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if (mViewPagerAdapter != null) {
                        return true;
                    }
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (mViewPagerAdapter != null) {
                        return true;
                    }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void openMenu() {
        //设置开启动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.menu_show_action);
        mMenu.clearAnimation();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mMenu.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mMenu.setAnimation(animation);
        animation.start();
        mIsMenuOpen = true;
    }

    @Override
    public void closeMenu() {
        //设置关闭动画
        Animation animation = AnimationUtils.loadAnimation(GalleryActivity.this, R.anim.menu_hide_action);
        mMenu.clearAnimation();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMenu.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mMenu.setAnimation(animation);
        animation.start();
        mIsMenuOpen = false;
    }

    @Override
    public boolean isOpen() {
        return mIsMenuOpen;
    }

    public class BatteryBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)
                    && mIv_battery != null) {
                //得到系统当前电量
                int level = intent.getIntExtra("level", 0);
                int status = intent.getIntExtra("status", 0);
                boolean chargingFlag = false;
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        //充电中
                        chargingFlag = true;
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        //放电中
                        chargingFlag = false;
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        //未充电
                        chargingFlag = false;
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        //电量满
                        chargingFlag = false;
                        mIv_battery.setImageResource(R.mipmap.battery_full);
                        break;
                }
                if (level == 100) {
                    if (chargingFlag) {
                        mIv_battery.setImageResource(R.mipmap.battery_charging_full);
                    } else {
                        mIv_battery.setImageResource(R.mipmap.battery_full);
                    }
                } else if (level >= 90 && level < 100) {
                    if (chargingFlag) {
                        mIv_battery.setImageResource(R.mipmap.battery_charging_90);
                    } else {
                        mIv_battery.setImageResource(R.mipmap.battery_90);
                    }
                } else if (level >= 80 && level < 90) {
                    if (chargingFlag) {
                        mIv_battery.setImageResource(R.mipmap.battery_charging_80);
                    } else {
                        mIv_battery.setImageResource(R.mipmap.battery_80);
                    }
                } else if (level >= 60 && level < 80) {
                    if (chargingFlag) {
                        mIv_battery.setImageResource(R.mipmap.battery_charging_60);
                    } else {
                        mIv_battery.setImageResource(R.mipmap.battery_60);
                    }
                } else if (level >= 50 && level < 60) {
                    if (chargingFlag) {
                        mIv_battery.setImageResource(R.mipmap.battery_charging_50);
                    } else {
                        mIv_battery.setImageResource(R.mipmap.battery_50);
                    }
                } else if (level >= 30 && level < 50) {
                    if (chargingFlag) {
                        mIv_battery.setImageResource(R.mipmap.battery_charging_30);
                    } else {
                        mIv_battery.setImageResource(R.mipmap.battery_30);
                    }
                } else if (level >= 20 && level < 30) {
                    if (chargingFlag) {
                        mIv_battery.setImageResource(R.mipmap.battery_charging_20);
                    } else {
                        mIv_battery.setImageResource(R.mipmap.battery_20);
                    }
                } else if (level < 20) {
                    if (chargingFlag) {
                        mIv_battery.setImageResource(R.mipmap.battery_charging_20);
                    } else {
                        mIv_battery.setImageResource(R.mipmap.battery_alert);
                    }
                }
            }
        }
    }
}
