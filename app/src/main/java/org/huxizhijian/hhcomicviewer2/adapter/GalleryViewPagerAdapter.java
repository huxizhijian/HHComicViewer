package org.huxizhijian.hhcomicviewer2.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer2.model.ComicChapter;
import org.huxizhijian.hhcomicviewer2.ui.entry.GalleryActivity;
import org.huxizhijian.hhcomicviewer2.utils.CommonUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.view.ZoomImageView;
import org.huxizhijian.hhcomicviewer2.view.listener.OnCenterTapListener;
import org.huxizhijian.hhcomicviewer2.view.listener.OnLeftOrRightTapListener;
import org.huxizhijian.sdk.imageloader.ImageLoaderOptions;
import org.huxizhijian.sdk.imageloader.listener.ImageLoaderManager;

import java.io.File;
import java.util.LinkedList;

/**
 * Gallery的ViewPagerAdapter
 * Created by wei on 2017/1/21.
 */

public class GalleryViewPagerAdapter extends PagerAdapter {

    private GalleryActivity mContext;
    private ComicChapter mComicChapter;
    private LinkedList<ZoomImageView> mRecyclerImageViews;
    private boolean mLoadOnLineFullSizeImage;

    private OnCenterTapListener onCenterTapListener;
    private OnLeftOrRightTapListener onLeftOrRightTapListener;

    //图片加载工具类
    private ImageLoaderManager mImageLoader = ImageLoaderOptions.getImageLoaderManager();

    public GalleryViewPagerAdapter(GalleryActivity context, boolean loadOnLineFullSizeImage) {
        mContext = context;
        mLoadOnLineFullSizeImage = loadOnLineFullSizeImage;
    }

    public void setComicChapter(ComicChapter comicChapter) {
        mComicChapter = comicChapter;
    }

    public void setOnLeftOrRightTapListener(OnLeftOrRightTapListener onLeftOrRightTapListener) {
        this.onLeftOrRightTapListener = onLeftOrRightTapListener;
    }

    public void setOnCenterTapListener(OnCenterTapListener onCenterTapListener) {
        this.onCenterTapListener = onCenterTapListener;
    }

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

    private void setImageViewRecycler(final ZoomImageView imageView, int position) {
        if (mComicChapter != null && mComicChapter.getDownloadStatus() == Constants.DOWNLOAD_FINISHED) {
            //如果是下载的章节
            File file = new File(mComicChapter.getSavePath(), CommonUtils.getPageName(position));
            if (file.exists()) {
                mImageLoader.displayGallery(mContext, file, imageView);
            } else {
                Toast.makeText(mContext, "好像下载错误了~", Toast.LENGTH_SHORT).show();
            }
        } else {
            //判断用户设置
            if (mLoadOnLineFullSizeImage) {
                //加载全尺寸
                if (mComicChapter != null) {
                    mImageLoader.displayGalleryFull(mContext, mComicChapter.getPicList().get(position), imageView);
                }
            } else {
                //图片尺寸匹配屏幕
                if (mComicChapter != null) {
                    mImageLoader.displayGalleryFit(mContext, mComicChapter.getPicList().get(position), imageView);
                }
            }
        }
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
        return mComicChapter.getPageCount();
    }

    private ZoomImageView getImageView() {
        ZoomImageView imageView = new ZoomImageView(mContext);
        if (onCenterTapListener != null) {
            imageView.setOnCenterTapListener(onCenterTapListener);
        }
        if (onLeftOrRightTapListener != null) {
            imageView.setOnLeftOrRightTapListener(onLeftOrRightTapListener);
        }
        return imageView;
    }
}
