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

package org.huxizhijian.hhcomicviewer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer.R;
import org.huxizhijian.hhcomicviewer.model.ComicChapter;
import org.huxizhijian.hhcomicviewer.utils.CommonUtils;
import org.huxizhijian.hhcomicviewer.utils.Constants;
import org.huxizhijian.sdk.imageloader.ImageLoaderOptions;
import org.huxizhijian.sdk.imageloader.listener.ImageLoaderManager;

import java.io.File;

/**
 * 漫画图片显示页listview适配器
 * Created by wei on 2017/1/24.
 */

public class GalleryListViewAdapter extends BaseAdapter {

    private Context mContext;
    private ComicChapter mComicChapter;
    private LayoutInflater mInflater;
    private boolean mLoadOnLineFullSizeImage;
    private boolean mIsCenterPositionVisible;

    //图片加载工具类
    private ImageLoaderManager mImageLoader = ImageLoaderOptions.getImageLoaderManager();

    public GalleryListViewAdapter(Context context, ComicChapter comicChapter,
                                  boolean loadOnLineFullSizeImage, boolean isCenterPositionVisible) {
        mContext = context;
        mComicChapter = comicChapter;
        mLoadOnLineFullSizeImage = loadOnLineFullSizeImage;
        mIsCenterPositionVisible = isCenterPositionVisible;
        mInflater = LayoutInflater.from(context);
    }

    public void setComicChapter(ComicChapter comicChapter) {
        mComicChapter = comicChapter;
    }

    @Override
    public int getCount() {
        return mComicChapter.getPageCount();
    }

    @Override
    public Object getItem(int position) {
        return mComicChapter.getPicList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_gallery_list_view, parent, false);
            vh.iv = (ImageView) convertView.findViewById(R.id.imageView_gallery);
            vh.tv = (TextView) convertView.findViewById(R.id.tv_position_gallery);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        loadImage(position, vh.iv);
        vh.tv.setText(position + "");
        if (!mIsCenterPositionVisible) {
            vh.tv.setVisibility(View.GONE);
        }
        return convertView;
    }

    private void loadImage(int position, final ImageView imageView) {
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

    class ViewHolder {
        ImageView iv;
        TextView tv;
    }

}
