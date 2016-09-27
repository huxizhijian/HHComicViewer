package org.huxizhijian.hhcomicviewer2.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.db.ComicCaptureDBHelper;
import org.huxizhijian.hhcomicviewer2.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer2.enities.Comic;
import org.huxizhijian.hhcomicviewer2.enities.ComicCapture;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.view.ZoomImageView;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;


public class GalleryActivity extends Activity implements View.OnClickListener {

    //comic信息
    private ComicCapture mComicCapture;
    private ArrayList<String> mPicList;
    private Comic mComic;
    private int mCapturePosition;

    //数据库操作
    private ComicCaptureDBHelper mCaptureDBHelper;
    private boolean mIsDownloaded = false; //该章节是否已经下载完毕

    //控件
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private LinkedList<ZoomImageView> mRecyclerImageViews;
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
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UPDATE_TIME) {
                mTv_time.setText(BaseUtils.getNowDate());
                sendEmptyMessageDelayed(UPDATE_TIME, 10000);
            }
        }
    };

    //用户设置
    private boolean loadOnLineFullSizeImage = false; //在线阅读下载全尺寸图片
    private boolean useVolButtonChangePage = false; //使用音量键翻页

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mComic = (Comic) getIntent().getSerializableExtra("comic");
        mCapturePosition = getIntent().getIntExtra("position", 0);
        initMenu();
        initViewPager();
        getWebContent();
    }

    private void getWebContent() {
        mIsDownloaded = false;
        String captureUrl = mComic.getCaptureUrl().get(mCapturePosition);
        if (mCaptureDBHelper == null) {
            mCaptureDBHelper = ComicCaptureDBHelper.getInstance(this);
        }
        mComicCapture = null;
        mComicCapture = mCaptureDBHelper.findByCaptureUrl(captureUrl);
        mViewPager.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        if (mComicCapture != null && mComicCapture.getDownloadStatus() == Constants.DOWNLOAD_FINISHED) {
            //如果已经下载完毕
            mIsDownloaded = true;
            initImageView();
        } else {
            //否则在线看
            RequestParams params = new RequestParams(Constants.HHCOMIC_URL + mComic.getCaptureUrl().get(mCapturePosition));
            x.http().get(params, new Callback.CommonCallback<byte[]>() {
                @Override
                public void onSuccess(byte[] result) {
                    try {
                        final String content = new String(result, "gb2312");
                        mComicCapture = new ComicCapture(mComic.getTitle(), mComic.getCaptureName().get(mCapturePosition),
                                mComic.getCaptureUrl().get(mCapturePosition), mComic.getComicUrl());
                        mComicCapture.updatePicList(mComic.getCaptureUrl().get(mCapturePosition), content);
                        mPicList = mComicCapture.getPicList();
                        initImageView();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.e("getWebContent", "onError: " + ex.toString());
                    if (BaseUtils.getAPNType(GalleryActivity.this) == BaseUtils.NONEWTWORK) {
                        Toast.makeText(GalleryActivity.this, Constants.NO_NETWORK, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    Log.e("getWebContent", "onCancelled: " + cex.toString());
                }

                @Override
                public void onFinished() {
                }
            });
        }
    }

    private void initImageView() {
        //加载各种数据
        mSeekBar.setMax(mComicCapture.getPageCount() - 1);
        mTv_name.setText(mComicCapture.getCaptureName());
        if (mViewPagerAdapter == null) {
            mViewPagerAdapter = new ViewPagerAdapter();
        } else {
            mViewPagerAdapter.notifyDataSetChanged();
        }
        //设置预读取2页的内容，默认数字为1
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mViewPagerAdapter);
        //将progressbar设置为不可见
        mProgressBar.setVisibility(View.GONE);
        if (mComic.getReadCapture() == mCapturePosition) {
            //设置读到的页数
            mViewPager.setCurrentItem(mComic.getReadPage(), false);
        }
        //初始化页数
        mSeekBar.setProgress(mViewPager.getCurrentItem());
        mTv_progress.setText(mViewPager.getCurrentItem() + 1 + "/" + mComicCapture.getPageCount());
        mTv_position.setText(mViewPager.getCurrentItem() + 1 + "");

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mSeekBar.setProgress(position);
                mTv_progress.setText(position + 1 + "/" + mComicCapture.getPageCount());
                mTv_position.setText(position + 1 + "");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setVisibility(View.VISIBLE);
    }

    private void initMenu() {
        mMenu = (RelativeLayout) findViewById(R.id.menu_gallery);
        ImageButton btn_prev = (ImageButton) findViewById(R.id.btn_prev_gallery);
        ImageButton btn_next = (ImageButton) findViewById(R.id.btn_next_gallery);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar_gallery);
        mTv_name = (TextView) findViewById(R.id.tv_name_gallery);
        mTv_progress = (TextView) findViewById(R.id.tv_progress_gallery);
        mTv_time = (TextView) findViewById(R.id.tv_time_gallery);
        mIv_battery = (ImageView) findViewById(R.id.iv_battery_gallery);
        mTv_position = (TextView) findViewById(R.id.tv_position_gallery);

        //用户设置
        preferencesSet();


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
                mViewPager.setCurrentItem(position, false);
                mViewPager.clearAnimation();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        btn_prev.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        mTv_time.setText(BaseUtils.getNowDate());
        //设置10秒后更新时间
        handler.sendEmptyMessageDelayed(UPDATE_TIME, 10000);
    }

    private void preferencesSet() {
        //获取设置文件
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //对于四个控件的显示设置
        if (!sharedPreferences.getBoolean("time_visible", true)) {
            //时间不显示
            mTv_time.setVisibility(View.GONE);
        }
        if (!sharedPreferences.getBoolean("page_visible", true)) {
            //正上方页码不显示
            mTv_progress.setVisibility(View.GONE);
        }
        if (!sharedPreferences.getBoolean("charge_visible", true)) {
            //电量不显示
            mIv_battery.setVisibility(View.GONE);
        }
        if (!sharedPreferences.getBoolean("number_visible", false)) {
            //正中央页码不显示
            mTv_position.setVisibility(View.GONE);
        }
        if (sharedPreferences.getBoolean("keep_screen_on", false)) {
            //设置屏幕常亮
            mMenu.setKeepScreenOn(true);
        }
        loadOnLineFullSizeImage = sharedPreferences.getBoolean("reading_full_size_image", false);
        useVolButtonChangePage = sharedPreferences.getBoolean("use_volume_key", false);
        String rotate = sharedPreferences.getString("reading_screen_rotate", "none");
        if (rotate.equals("portrait")) {
            //设置屏幕方向为竖屏
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else if (rotate.equals("landscape")) {
            //设置屏幕方向为横屏
            if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager_gallery);
        mProgressBar = (ProgressBar) findViewById(R.id.pg_loading_gallery);
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
        mComic.setReadCapture(mCapturePosition);
        mComic.setReadPage(mViewPager.getCurrentItem());
        ComicDBHelper.getInstance(this).update(mComic);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_prev_gallery:
                if (mCapturePosition - 1 >= 0) {
                    mCapturePosition--;
                    mComic.setReadCapture(mCapturePosition);
                    mComic.setReadPage(0);
                    mViewPager.setCurrentItem(0, false);
                    getWebContent();
                } else {
                    Toast.makeText(GalleryActivity.this, "当前是第一话", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_next_gallery:
                if (mCapturePosition + 1 < mComic.getCaptureUrl().size()) {
                    mCapturePosition++;
                    mComic.setReadCapture(mCapturePosition);
                    mComic.setReadPage(0);
                    mViewPager.setCurrentItem(0, false);
                    getWebContent();
                } else {
                    Toast.makeText(GalleryActivity.this, "当前是最后一话", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ZoomImageView imageView = null;
            if (mRecyclerImageViews == null) {
                mRecyclerImageViews = new LinkedList<>();
            }
            if (mRecyclerImageViews.size() > 0) {
                //复用ImageView
                imageView = mRecyclerImageViews.removeFirst();
            } else {
                //获得新的ImageView
                imageView = getImageView();
            }
            //为不同的imageView设置不同图片
            setImageViewRecycler(imageView, position);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            ZoomImageView imageView = (ZoomImageView) object;
            //设置缩放将图片居中缩放
//            imageView.setImageInCenter();
            //回收图片
            imageView.setImageDrawable(null);
            imageView.setImageBitmap(null);
            releaseImageViewResource(imageView);
            //移除页面
            container.removeView(imageView);
            //回收页面
            mRecyclerImageViews.addLast(imageView);
        }

        private void releaseImageViewResource(ZoomImageView imageView) {
            if (imageView == null) return;
            Drawable drawable = imageView.getDrawable();
            if (drawable != null && drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap = null;
                }
            }
            System.gc();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return mComicCapture.getPageCount();
        }

        private ZoomImageView getImageView() {
            final ZoomImageView imageView = new ZoomImageView(GalleryActivity.this);
            imageView.setOnCenterTapListener(new ZoomImageView.OnCenterTapListener() {
                @Override
                public void openMenu() {
                    //设置开启动画
                    Animation animation = AnimationUtils.loadAnimation(GalleryActivity.this, R.anim.menu_show_action);
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
//                    System.out.println("close menu");
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
            });
            return imageView;
        }
    }

    public void setImageViewRecycler(final ZoomImageView imageView, final int position) {
        imageView.setOnLeftOrRightTapListener(new ZoomImageView.OnLeftOrRightTapListener() {
            @Override
            public void leftTap() {
                if (position - 1 >= 0) {
                    mViewPager.setCurrentItem(position - 1, false);
                }
            }

            @Override
            public void rightTap() {
                if (position + 1 < mComicCapture.getPageCount()) {
                    mViewPager.setCurrentItem(position + 1, false);
                }
            }
        });

        if (mIsDownloaded) {
            //如果是下载的章节
            File file = new File(mComicCapture.getSavePath(), BaseUtils.getPageName(position));
            if (file.exists()) {
                Glide.with(this)
                        .load(file)
                        .asBitmap()
                        .dontAnimate()
                        .skipMemoryCache(true)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
            } else {
                Toast.makeText(GalleryActivity.this, "好像下载错误了~", Toast.LENGTH_SHORT).show();
            }
        } else {
            //判断用户设置
            if (loadOnLineFullSizeImage) {
                //加载全尺寸
                Glide.with(this)
                        .load(mPicList.get(position))
                        .asBitmap()
                        .dontAnimate()
                        .skipMemoryCache(true)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
            } else {
                //图片尺寸匹配屏幕
                Glide.with(GalleryActivity.this)
                        .load(mPicList.get(position))
                        .dontAnimate()
                        .fitCenter()
                        .skipMemoryCache(true) //跳过内存缓存
                        .into(imageView);
            }
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
            mComic.setReadCapture(mCapturePosition);
            mComic.setReadPage(mViewPager.getCurrentItem());
            Intent intent = new Intent();
            intent.putExtra("comic", mComic);
            setResult(0, intent);
            finish();
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
                        }
                    }
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (mViewPagerAdapter != null) {
                        if (mViewPager.getCurrentItem() + 1 < mComicCapture.getPageCount()) {
                            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, false);
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
