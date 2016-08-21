package org.huxizhijian.hhcomicviewer2;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.huxizhijian.hhcomicviewer2.view.ZoomImageView;

public class GalleryActivity extends Activity {

    private ViewPager mViewPager;
    private ZoomImageView[] mImageViews;
    ViewPagerAdapter mViewPagerAdapter;
    private int[] mImgs = new int[]{
            R.drawable.a001,
            R.drawable.a002,
            R.drawable.a003,
            R.drawable.a004,
            R.drawable.a005,
            R.drawable.a006,
            R.drawable.a007,
            R.drawable.a008,
            R.drawable.a009,
            R.drawable.a010
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallary);
        initViewPager();
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager_gallery);
        mImageViews = new ZoomImageView[mImgs.length];
        mViewPagerAdapter = new ViewPagerAdapter();
        mViewPager.setAdapter(mViewPagerAdapter);
    }

    private class ViewPagerAdapter extends PagerAdapter {
        private boolean isMenuOpen = false;

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
            return mImgs.length;
        }

        private ZoomImageView getImageView(int position) {
            ZoomImageView imageView = new ZoomImageView(GalleryActivity.this);
            imageView.setOnCenterTapListener(new ZoomImageView.OnCenterTapListener() {
                @Override
                public void openMenu() {
                    System.out.println("open menu");
                    isMenuOpen = true;
                }

                @Override
                public void closeMenu() {
                    System.out.println("close menu");
                    isMenuOpen = false;
                }

                @Override
                public boolean isOpen() {
                    return isMenuOpen;
                }
            });
            Glide.with(GalleryActivity.this)
                    .load(mImgs[position])
                    .crossFade()
                    .fitCenter()
                    .into(imageView);
            return imageView;
        }
    }
}
