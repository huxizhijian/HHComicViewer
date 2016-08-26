package org.huxizhijian.hhcomicviewer2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.view.ZoomImageView;
import org.huxizhijian.hhcomicviewer2.vo.Comic;
import org.huxizhijian.hhcomicviewer2.vo.ComicCapture;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class GalleryActivity extends Activity implements View.OnClickListener {

    private ComicCapture comicCapture;
    private ArrayList<String> picList;
    private Comic comic;
    private int capturePosition;

    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private ZoomImageView[] mImageViews;

    //菜单及其上的控件
    private RelativeLayout mMenu;
    private SeekBar mSeekBar;
    private TextView mTv_name, mTv_progress, mTv_time;
    private ImageView mIv_battery;
    private boolean isMenuOpen = false; //菜单是否打开

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        comic = (Comic) getIntent().getSerializableExtra("comic");
        capturePosition = getIntent().getIntExtra("position", 0);
        initMenu();
        initViewPager();
        getWebContent();
    }

    private void getWebContent() {
        RequestParams params = new RequestParams(Constants.URL_HHCOMIC + comic.getCaptureUrl()[capturePosition]);
        x.http().get(params, new Callback.CommonCallback<byte[]>() {
            @Override
            public void onSuccess(byte[] result) {
                try {
                    final String content = new String(result, "gb2312");
                    comicCapture = new ComicCapture(Constants.URL_HHCOMIC + comic.getCaptureUrl()[capturePosition], content);
                    picList = comicCapture.getPicList();
                    //初始化控件数值
                    mSeekBar.setMax(picList.size() - 1);
                    mSeekBar.setProgress(mViewPager.getCurrentItem());
                    mTv_progress.setText(mViewPager.getCurrentItem() + 1 + "/" + picList.size());
                    mTv_name.setText(comic.getCaptureName()[capturePosition]);
                    mImageViews = new ZoomImageView[picList.size()];
                    mViewPagerAdapter = new ViewPagerAdapter();
                    //设置与读取3页的内容，默认数字为1
                    mViewPager.setOffscreenPageLimit(3);
                    mViewPager.setAdapter(mViewPagerAdapter);
                    mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        }

                        @Override
                        public void onPageSelected(int position) {
                            mSeekBar.setProgress(position);
                            mTv_progress.setText(position + 1 + "/" + picList.size());
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("getWebContent", "onError: " + ex.toString());
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

    private void initMenu() {
        mMenu = (RelativeLayout) findViewById(R.id.menu_gallery);
        Button btn_prev = (Button) findViewById(R.id.btn_prev_gallery);
        Button btn_next = (Button) findViewById(R.id.btn_next_gallery);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar_gallery);
        mTv_name = (TextView) findViewById(R.id.tv_name_gallery);
        mTv_progress = (TextView) findViewById(R.id.tv_progress_gallery);
        mTv_time = (TextView) findViewById(R.id.tv_time_gallery);
        mIv_battery = (ImageView) findViewById(R.id.iv_battery_gallery);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
                mViewPager.setCurrentItem(position);
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

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager_gallery);
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_prev_gallery:
                if (capturePosition + 1 < comic.getCaptureUrl().length) {
                    capturePosition++;
                    getWebContent();
                } else {
                    Toast.makeText(GalleryActivity.this, "当前是第一话", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_next_gallery:
                if (capturePosition - 1 >= 0) {
                    capturePosition--;
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
            ZoomImageView imageView = getImageView(position);
            container.addView(imageView);
            mImageViews[position] = imageView;
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView(mImageViews[position]);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return picList.size();
        }

        private ZoomImageView getImageView(int position) {
            final ZoomImageView imageView = new ZoomImageView(GalleryActivity.this);
            imageView.setOnCenterTapListener(new ZoomImageView.OnCenterTapListener() {
                @Override
                public void openMenu() {
//                    System.out.println("open menu");
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
                    isMenuOpen = true;
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
                    isMenuOpen = false;
                }

                @Override
                public boolean isOpen() {
                    return isMenuOpen;
                }
            });
            Glide.with(GalleryActivity.this)
                    .load(picList.get(position))
                    .crossFade()
                    .fitCenter()
                    .into(imageView);
            return imageView;
        }
    }

    @Override
    public void onBackPressed() {
        //override返回按钮按下方法
        if (isMenuOpen) {
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
            isMenuOpen = false;
        } else {
            super.onBackPressed();
        }
    }

    public class BatteryBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)
                    && mIv_battery != null) {
                //得到系统当前电量
                int level = intent.getIntExtra("level", 0);
                if (level == 100) {
                    mIv_battery.setImageResource(R.mipmap.battery_100);
                } else if (level >= 75 && level < 100) {
                    mIv_battery.setImageResource(R.mipmap.battery_75);
                } else if (level >= 50 && level < 75) {
                    mIv_battery.setImageResource(R.mipmap.battery_50);
                } else if (level >= 25 && level < 50) {
                    mIv_battery.setImageResource(R.mipmap.battery_25);
                } else if (level >= 4 && level < 25) {
                    mIv_battery.setImageResource(R.mipmap.battery_4);
                } else if (level < 4) {
                    mIv_battery.setImageResource(R.mipmap.bg_battery);
                }
            }
        }
    }
}
