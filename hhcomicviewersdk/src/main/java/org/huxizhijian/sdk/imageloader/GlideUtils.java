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
package org.huxizhijian.sdk.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import org.huxizhijian.sdk.imageloader.listener.ImageLoaderManager;
import org.huxizhijian.sdk.imageloader.listener.ImageRequestListener;

import java.io.File;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * @author huxizhijian 2017/2/22
 */
public class GlideUtils implements ImageLoaderManager {

    @Override
    public void displayImage(Context context, ImageView imageView, String url) {
        Glide.with(context).load(url).into(imageView);
    }

    @Override
    public void displayThumbnail(Context context, String url, ImageView imageView,
                                 int placeholderRes, int errorRes, int width, int height) {
        Glide.with(context)
                .load(url)
                .placeholder(placeholderRes)
                .error(errorRes)
                .override(width, height)
                .fitCenter()
                .dontAnimate()
                .into(imageView);
    }

    @Override
    public void displayGallery(Context context, File file, final ImageView imageView) {
        Glide.with(context)
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
    }

    @Override
    public void displayGalleryFull(Context context, String url, final ImageView imageView) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .dontAnimate()
                .skipMemoryCache(true)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImageBitmap(bitmap);
                    }
                });
    }

    @Override
    public void displayGalleryFit(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .dontAnimate()
                .fitCenter()
                .skipMemoryCache(true) //跳过内存缓存
                .into(imageView);
    }

    @Override
    public void displayBlurImage(Context context, String url, ImageView imageView,
                                 int width, int height, int radius, int sampling,
                                 final ImageRequestListener listener) {
        Glide.with(context)
                .load(url)
                //.placeholder(R.drawable.stackblur_default)
                .override(165, 220)
                .bitmapTransform(new BlurTransformation(context, 14, 3))// 设置高斯模糊
                .listener(new RequestListener<String, GlideDrawable>() {//监听加载状态
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                               boolean isFirstResource) {
                        if (listener != null) {
                            listener.onException(e);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                                   Target<GlideDrawable> target, boolean isFromMemoryCache,
                                                   boolean isFirstResource) {
                        if (listener != null) {
                            listener.onResourceReady();
                        }
                        return false;
                    }
                })
                .into(imageView);
    }

    @Override
    public void clearDiskCache(Context context) {
        Glide.get(context).clearDiskCache();
    }

    @Override
    public void clearMemoryCache(Context context) {
        Glide.get(context).clearMemory();
    }

}
