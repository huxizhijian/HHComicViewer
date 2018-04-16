/*
 * Copyright 2016-2018 huxizhijian
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
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import org.huxizhijian.sdk.imageloader.listener.ImageLoaderManager;
import org.huxizhijian.sdk.imageloader.listener.ImageLoaderProgressListener;
import org.huxizhijian.sdk.imageloader.listener.ImageRequestListener;
import org.huxizhijian.sdk.imageloader.options.GlideApp;

import java.io.File;

import jp.wasabeef.glide.transformations.BlurTransformation;


/**
 * @author huxizhijian 2017/2/22
 */
public class GlideUtils implements ImageLoaderManager {

    private static final RequestOptions GALLERY_REQUEST_OPTIONS = new RequestOptions()
            .dontAnimate()
            .skipMemoryCache(true);

    @Override
    public void displayImage(Context context, ImageView imageView, String url) {
        GlideApp.with(context).load(url).into(imageView);
    }

    @Override
    public void displayThumbnail(Context context, String url, ImageView imageView,
                                 int placeholderRes, int errorRes, int width, int height) {
        // 新建一个options，设置各种参数
        RequestOptions options = new RequestOptions()
                .placeholder(placeholderRes)
                .error(errorRes)
                .override(width, height)
                .fitCenter()
                .dontAnimate();

        GlideApp.with(context)
                .load(url)
                .apply(options)
                .into(imageView);
    }

    @Override
    public void displayGallery(Context context, File file, final ImageView imageView) {
        GlideApp.with(context)
                .load(file)
                .apply(GALLERY_REQUEST_OPTIONS)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                    }
                });
    }

    @Override
    public void displayGalleryFull(Context context, String url, ImageView imageView, final ImageLoaderProgressListener listener) {
        /*// 创建一个Loader
        GlideAppImageLoader loader = GlideAppImageLoader.create(imageView);
        // 设置监听
        loader.setOnGlideAppImageViewListener(url, new OnGlideAppImageViewListener() {
            @Override
            public void onProgress(int percent, boolean isDone, GlideAppException exception) {
                listener.onProgress(percent, isDone, exception);
            }
        });
        // 设置渐变，并且加载到ImageView中
        loader.requestBuilder(url, GALLERY_REQUEST_OPTIONS)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);*/
        // TODO: 2017/10/27 实现一个进度条监听图片加载
        displayGalleryFull(context, url, imageView);
    }

    @Override
    public void displayGalleryFull(Context context, String url, final ImageView imageView) {
        GlideApp.with(context)
                .load(url)
                .apply(GALLERY_REQUEST_OPTIONS)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        imageView.setImageDrawable(resource);
                    }
                });
    }

    @Override
    public void displayGalleryFit(Context context, String url, ImageView imageView) {

        //跳过内存缓存
        RequestOptions options = RequestOptions.fitCenterTransform()
                .dontAnimate()
                .skipMemoryCache(true);

        GlideApp.with(context)
                .load(url)
                .apply(options)
                .into(imageView);
    }

    @Override
    public void displayBlurImage(Context context, String url, ImageView imageView,
                                 int width, int height, int radius, int sampling,
                                 final ImageRequestListener listener) {
        // 设置高斯模糊
        RequestOptions options = RequestOptions
                .bitmapTransform(new BlurTransformation(14, 3))
                .override(165, 220);


        GlideApp.with(context)
                .load(url)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (listener != null) {
                            listener.onException(e);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                   DataSource dataSource, boolean isFirstResource) {
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
        GlideApp.get(context).clearDiskCache();
    }

    @Override
    public void clearMemoryCache(Context context) {
        GlideApp.get(context).clearMemory();
    }

}
