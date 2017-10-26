package org.huxizhijian.sdk.imageloader.listener;

import com.bumptech.glide.load.engine.GlideException;

/**
 * @author huxizhijian
 * @date 2017/10/26
 */
public interface ImageLoaderProgressListener {
    void onProgress(int percent, boolean isDone, GlideException exception);
}
