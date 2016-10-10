/*
 * Copyright 2016 huxizhijian
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

package org.huxizhijian.hhcomicviewer2.option;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.module.GlideModule;

import java.io.File;

/**
 * glide配置文件
 * Created by wei on 2016/8/21.
 */
public class GlideConfiguration implements GlideModule {
    @Override
    public void applyOptions(final Context context, GlideBuilder glideBuilder) {
        //设置为无损格式
        glideBuilder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        glideBuilder.setDiskCache(new DiskCache.Factory() {
            @Override
            public DiskCache build() {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String cacheSize = sharedPreferences.getString("reading_cache_size", "160MB");
                int sizeInMB = 160;
                switch (cacheSize) {
                    case "40MB":
                        sizeInMB = 40;
                        break;
                    case "80MB":
                        sizeInMB = 80;
                        break;
                    case "160MB":
                        sizeInMB = 160;
                        break;
                    case "320MB":
                        sizeInMB = 320;
                        break;
                    case "640MB":
                        sizeInMB = 640;
                        break;
                }

                File path = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    path = context.getExternalCacheDirs()[(context.getExternalCacheDirs().length - 1)];
                } else {
                    path = context.getExternalCacheDir();
                }
                File cacheLocation = new File(path, "cache_dir_name");
                cacheLocation.mkdirs();
                return DiskLruCacheWrapper.get(cacheLocation, sizeInMB * 1024 * 1024);
            }
        });
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
    }
}
