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

package org.huxizhijian.core.app;

import android.app.Application;
import android.support.annotation.NonNull;

import com.blankj.utilcode.util.Utils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * 配置器，根据配置类型{@link ConfigKeys}保存配置并对第三方库进行初始化
 *
 * @author huxizhijian 2017/9/17
 */
public class Configurator {

    private static final HashMap<Object, Object> HH_CONFIGS = new HashMap<>();
    private static final List<Interceptor> INTERCEPTORS = new ArrayList<>();
    private static final OkHttpClient.Builder BUILDER = new OkHttpClient.Builder();

    private Configurator() {
        // 开始配置，标记为未完成配置
        HH_CONFIGS.put(ConfigKeys.CONFIG_READY, false);
    }

    final HashMap<Object, Object> getHHConfigs() {
        return HH_CONFIGS;
    }

    static Configurator getInstance() {
        // 采用静态内部类实现的单例模式
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final Configurator INSTANCE = new Configurator();
    }

    public final void configure() {
        //初始化OkHttpClient
        if (HH_CONFIGS.get(ConfigKeys.OKHTTP_CLIENT) == null) {
            //添加interceptor
            for (Interceptor interceptor : INTERCEPTORS) {
                BUILDER.addInterceptor(interceptor);
            }
            HH_CONFIGS.put(ConfigKeys.OKHTTP_CLIENT, BUILDER.build());
        }
        //初始化Utils库
        Utils.init((Application) HHGlobalVariable.getApplicationContext());
        //初始化logger库
        Logger.addLogAdapter(new AndroidLogAdapter());
        HH_CONFIGS.put(ConfigKeys.CONFIG_READY, true);
    }

    public final Configurator withConnectTimeOut(long timeOut, @NonNull TimeUnit unit) {
        BUILDER.connectTimeout(timeOut, unit);
        return this;
    }

    public final Configurator withOkHttpClient(OkHttpClient client) {
        HH_CONFIGS.put(ConfigKeys.OKHTTP_CLIENT, client);
        return this;
    }


    public final Configurator withInterceptor(Interceptor interceptor) {
        INTERCEPTORS.add(interceptor);
        HH_CONFIGS.put(ConfigKeys.INTERCEPTOR, INTERCEPTORS);
        return this;
    }

    public final Configurator withInterceptors(ArrayList<Interceptor> interceptors) {
        INTERCEPTORS.addAll(interceptors);
        HH_CONFIGS.put(ConfigKeys.INTERCEPTOR, INTERCEPTORS);
        return this;
    }

    private void checkConfiguration() {
        final boolean isReady = (boolean) HH_CONFIGS.get(ConfigKeys.CONFIG_READY);
        if (!isReady) {
            throw new RuntimeException("Configurations is not ready, call configure()");
        }
    }

    @SuppressWarnings("unchecked")
    final <T> T getConfiguration(Object key) {
        checkConfiguration();
        return (T) HH_CONFIGS.get(key);
    }
}
