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

package org.huxizhijian.sdk;

/**
 * 本类存放SDK中的常量
 * 本model依赖第三方SDK - glide, glide-transformations, okhttp, gson, okhttp3-integration
 * <p>
 * network - 网络sdk，封装了okhttp和HttpURLConnection两种访问网络的sdk
 * imageloader - 图片加载sdk，封装glide
 * util - 各种工具类
 * sharedpreferences - 对原版SharedPreferences的封装
 * <p>
 * <p>
 * #okhttp,okio混淆设置
 * -dontwarn com.squareup.okhttp.**
 * -keep class com.squareup.okhttp.** { *;}
 * -keep interface com.squareup.okhttp.** { *; }
 * -dontwarn okio.**
 * <p>
 * #glide配置文件混淆设置
 * -keep public class * implements com.bumptech.glide.module.GlideModule
 *
 * @author huxizhijian 2017/2/22
 */
public class SDKConstant {

    //sdk sp名字
    public static final String SHARED_PREFERENCES_NAME = "sdk.pre";

    //sp数据的key
    public static final String DECODE_FORMAT_KEY = "decode_format_key";
    public static final String DISK_CACHE_KEY = "disk_cache_key";
    public static final String DISK_CACHE_NAME_KEY = "disk_cache_name";

    //图片缓存文件名
    public static final String DEFAULT_CACHE_NAME = "image_cache";

    //加载图片的解码模式
    public static final String DECODE_FORMAT_ARGB_8888 = "rgb_8888";
    public static final String DECODE_FORMAT_RGB_565 = "rgb_565";

    //磁盘缓存的大小
    public static final String DISK_CACHE_40MB = "40MB";
    public static final String DISK_CACHE_80MB = "80MB";
    public static final String DISK_CACHE_160MB = "160MB";
    public static final String DISK_CACHE_320MB = "320MB";
    public static final String DISK_CACHE_640MB = "640MB";

}
