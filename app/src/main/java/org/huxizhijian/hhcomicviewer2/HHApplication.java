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

package org.huxizhijian.hhcomicviewer2;

import android.support.multidex.MultiDexApplication;

import org.huxizhijian.hhcomicviewer2.option.HHComicWebVariable;
import org.huxizhijian.sdk.SDKConstant;
import org.huxizhijian.sdk.imageloader.ImageLoaderOptions;
import org.huxizhijian.sdk.sharedpreferences.SharedPreferencesManager;
import org.xutils.DbManager;
import org.xutils.x;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 本工程的application
 * Created by wei on 2016/8/23.
 */
public class HHApplication extends MultiDexApplication {

    private static HHApplication sApplication;

    public static HHApplication getInstance() {
        return sApplication;
    }

    private volatile HHComicWebVariable mWebVariable;

    public HHComicWebVariable getHHWebVariable() {
        if (mWebVariable == null) {
            synchronized (HHComicWebVariable.class) {
                if (mWebVariable == null) {
                    mWebVariable = new HHComicWebVariable(this);
                }
            }
        }
        return mWebVariable;
    }

    private volatile OkHttpClient mClient;

    public DbManager.DaoConfig getDaoConfig() {
        //初始化数据库
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("comic_db")
                .setDbVersion(1)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        db.getDatabase().enableWriteAheadLogging();
                    }
                });
        return daoConfig;
    }

    public OkHttpClient getClient() {
        if (mClient == null) {
            createClient();
        }
        return mClient;
    }

    private void createClient() {
        synchronized (OkHttpClient.class) {
            if (mClient == null) {
                mClient = new OkHttpClient.Builder()
                        .connectTimeout(10000, TimeUnit.SECONDS)
                        .readTimeout(180000, TimeUnit.SECONDS)
                        .build();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initXUtils();
        createClient();
        sApplication = this;
        mWebVariable = new HHComicWebVariable(this);
        initSDK();
    }

    private void initSDK() {
        SharedPreferencesManager manager = new SharedPreferencesManager(this);
        String cacheSize = manager.getString("reading_cache_size", "160MB");
        new ImageLoaderOptions(this)
                .setDecodeFormat(SDKConstant.DECODE_FORMAT_ARGB_8888)
                .setCacheSize(cacheSize, SDKConstant.DEFAULT_CACHE_NAME);
    }

    private void initXUtils() {
        //初始化xUtils
        x.Ext.init(this);
        if (org.huxizhijian.sdk.util.Utils.isApkDebugable(this)) {
            //开启debug模式
            x.Ext.setDebug(true);
        } else {
            x.Ext.setDebug(false);
        }
    }

}
