package org.huxizhijian.hhcomic.comic.request;

import org.huxizhijian.hhcomic.comic.request.managerinterface.RequestManager;

import okhttp3.OkHttpClient;

/**
 * @author huxizhijian
 * @date 2018/9/25
 */
abstract class RxRequestManager implements RequestManager {

    final OkHttpClient mOkHttpClient;

    RxRequestManager(OkHttpClient okHttpClient) {
        mOkHttpClient = okHttpClient;
    }
}
