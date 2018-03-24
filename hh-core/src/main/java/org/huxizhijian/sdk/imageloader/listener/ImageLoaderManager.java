/*
 * Copyright 2018 huxizhijian
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

package org.huxizhijian.sdk.imageloader.listener;

import android.content.Context;
import android.widget.ImageView;

import java.io.File;

/**
 * 封装的图片装载类
 *
 * @author huxizhijian 2017/2/22
 */
public interface ImageLoaderManager {

    /**
     * 最普通的加载图片加载方法
     */
    void displayImage(Context context, ImageView imageView, String url);

    /**
     * 图片缩略图加载
     */
    void displayThumbnail(Context context, String url, ImageView imageView,
                          int placeholderRes, int errorRes, int width, int height);

    /**
     * Gallery中加载本地图片
     */
    void displayGallery(Context context, File file, ImageView imageView);

    /**
     * Gallery中加载在线图片，带有下载进度监听
     */
    void displayGalleryFull(Context context, String url, ImageView imageView, ImageLoaderProgressListener listener);

    /**
     * Gallery中在线加载全尺寸图片
     */
    void displayGalleryFull(Context context, String url, ImageView imageView);

    /**
     * Gallery中适配设备大小加载图片
     */
    void displayGalleryFit(Context context, String url, ImageView imageView);

    /**
     * 加载高斯模糊后的图片
     */
    void displayBlurImage(Context context, String url, ImageView imageView, int width, int height,
                          int radius, int sampling, ImageRequestListener listener);

    /**
     * 清除磁盘缓存
     */
    void clearDiskCache(Context context);

    /**
     * 清除内存缓存
     */
    void clearMemoryCache(Context context);

}
