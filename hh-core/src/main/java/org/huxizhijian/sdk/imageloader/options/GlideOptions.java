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

package org.huxizhijian.sdk.imageloader.options;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.module.GlideModule;

import org.huxizhijian.sdk.sharedpreferences.SharedPreferencesManager;

import java.io.File;

import static org.huxizhijian.sdk.SDKConstant.DECODE_FORMAT_KEY;
import static org.huxizhijian.sdk.SDKConstant.DECODE_FORMAT_RGB_565;
import static org.huxizhijian.sdk.SDKConstant.DECODE_FORMAT_ARGB_8888;
import static org.huxizhijian.sdk.SDKConstant.DEFAULT_CACHE_NAME;
import static org.huxizhijian.sdk.SDKConstant.DISK_CACHE_160MB;
import static org.huxizhijian.sdk.SDKConstant.DISK_CACHE_320MB;
import static org.huxizhijian.sdk.SDKConstant.DISK_CACHE_40MB;
import static org.huxizhijian.sdk.SDKConstant.DISK_CACHE_640MB;
import static org.huxizhijian.sdk.SDKConstant.DISK_CACHE_80MB;
import static org.huxizhijian.sdk.SDKConstant.DISK_CACHE_KEY;
import static org.huxizhijian.sdk.SDKConstant.DISK_CACHE_NAME_KEY;


/**
 * @author huxizhijian 2017/2/23
 */
public class GlideOptions implements GlideModule {

    @Override
    public void applyOptions(final Context context, GlideBuilder builder) {

        SharedPreferencesManager manager = new SharedPreferencesManager(context);
        final String cacheSize = manager.getString(DISK_CACHE_KEY, DISK_CACHE_160MB);
        String decodeFormat = manager.getString(DECODE_FORMAT_KEY, DECODE_FORMAT_ARGB_8888);
        final String cacheName = manager.getString(DISK_CACHE_NAME_KEY, DEFAULT_CACHE_NAME);

        DecodeFormat format = null;
        switch (decodeFormat) {
            case DECODE_FORMAT_RGB_565:
                format = DecodeFormat.PREFER_RGB_565;
                break;
            case DECODE_FORMAT_ARGB_8888:
                format = DecodeFormat.PREFER_ARGB_8888;
                break;
        }
        builder.setDecodeFormat(format);

        builder.setDiskCache(new DiskCache.Factory() {
            @Override
            public DiskCache build() {
                int sizeInMB = 160;
                switch (cacheSize) {
                    case DISK_CACHE_40MB:
                        sizeInMB = 40;
                        break;
                    case DISK_CACHE_80MB:
                        sizeInMB = 80;
                        break;
                    case DISK_CACHE_160MB:
                        sizeInMB = 160;
                        break;
                    case DISK_CACHE_320MB:
                        sizeInMB = 320;
                        break;
                    case DISK_CACHE_640MB:
                        sizeInMB = 640;
                        break;
                }

                File path = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    path = context.getExternalCacheDirs()[(context.getExternalCacheDirs().length - 1)];
                } else {
                    path = context.getExternalCacheDir();
                }
                File cacheLocation = new File(path, cacheName);
                cacheLocation.mkdirs();
                return DiskLruCacheWrapper.get(cacheLocation, sizeInMB * 1024 * 1024);
            }
        });
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {

    }

}
