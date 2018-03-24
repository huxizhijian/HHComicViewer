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

package org.huxizhijian.sdk.imageloader;

import android.content.Context;

import org.huxizhijian.sdk.imageloader.listener.ImageLoaderManager;
import org.huxizhijian.sdk.sharedpreferences.SharedPreferencesManager;

import static org.huxizhijian.sdk.SDKConstant.DECODE_FORMAT_KEY;
import static org.huxizhijian.sdk.SDKConstant.DISK_CACHE_KEY;
import static org.huxizhijian.sdk.SDKConstant.DISK_CACHE_NAME_KEY;
import static org.huxizhijian.sdk.SDKConstant.SHARED_PREFERENCES_NAME;

/**
 * @author huxizhijian 2017/2/23
 */
public class ImageLoaderOptions {

    //判断是否支持glide对象
    private static boolean GLIDE_LOADER = true;

    private SharedPreferencesManager mPreferencesManager;

    public ImageLoaderOptions(Context context) {
        mPreferencesManager = new SharedPreferencesManager(context.getApplicationContext(), SHARED_PREFERENCES_NAME);
    }

    public ImageLoaderOptions setDecodeFormat(String decodeFormat) {
        mPreferencesManager.putString(DECODE_FORMAT_KEY, decodeFormat);
        return this;
    }

    public ImageLoaderOptions setCacheSize(final String cacheSize, final String cacheName) {
        mPreferencesManager.putString(DISK_CACHE_KEY, cacheSize);
        mPreferencesManager.putString(DISK_CACHE_NAME_KEY, cacheName);
        return this;
    }

    public static ImageLoaderManager getImageLoaderManager() {
        if (GLIDE_LOADER) {
            return new GlideUtils();
        }
        //还没有封装其他加载库
        return new GlideUtils();
    }

}
