package org.huxizhijian.sdk.imageloader.options;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import org.huxizhijian.sdk.sharedpreferences.SharedPreferencesManager;

import java.io.File;

import static org.huxizhijian.sdk.SDKConstant.DECODE_FORMAT_ARGB_8888;
import static org.huxizhijian.sdk.SDKConstant.DECODE_FORMAT_KEY;
import static org.huxizhijian.sdk.SDKConstant.DECODE_FORMAT_RGB_565;
import static org.huxizhijian.sdk.SDKConstant.DEFAULT_CACHE_NAME;
import static org.huxizhijian.sdk.SDKConstant.DISK_CACHE_160MB;
import static org.huxizhijian.sdk.SDKConstant.DISK_CACHE_320MB;
import static org.huxizhijian.sdk.SDKConstant.DISK_CACHE_40MB;
import static org.huxizhijian.sdk.SDKConstant.DISK_CACHE_640MB;
import static org.huxizhijian.sdk.SDKConstant.DISK_CACHE_80MB;
import static org.huxizhijian.sdk.SDKConstant.DISK_CACHE_KEY;
import static org.huxizhijian.sdk.SDKConstant.DISK_CACHE_NAME_KEY;

/**
 * @author huxizhijian
 * @date 2017/10/26
 */
@GlideModule
public class HHGlideModel extends AppGlideModule {

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
            default:
            case DECODE_FORMAT_ARGB_8888:
                format = DecodeFormat.PREFER_ARGB_8888;
                break;
        }
        // 高版本配置，没有禁用Hardware Bitmaps，仅Android O以上版本的新特性
        // 参阅https://muyangmin.github.io/glide-docs-cn/doc/hardwarebitmaps.html了解不能使用Hardware Bitmaps的情况
        // 不禁用可能会导致错误，但是有着节省内存和避免内存抖动的优点
        builder.setDefaultRequestOptions(new RequestOptions()
        .format(format)
        );

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
                    default:
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
                return DiskLruCacheWrapper.create(cacheLocation, sizeInMB * 1024 * 1024);
            }
        });
    }

}
