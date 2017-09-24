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

package org.huxizhijian.core.app;

import android.content.Context;
import android.os.Handler;

import java.util.HashMap;

/**
 * 封装配置内容的入口类，通常用户不会直接调用{@link Configurator}的获取配置方法
 * 调用init后直接返回{@link Configurator}的实例
 *
 * @author huxizhijian 2017/9/17
 */
public final class HHEngine {

    public static Configurator init(Context context) {
        getConfigurators().put(ConfigKeys.APPLICATION_CONTEXT, context);
        return getConfigurator();
    }

    public static Configurator getConfigurator() {
        return Configurator.getInstance();
    }

    public static HashMap<Object, Object> getConfigurators() {
        return getConfigurator().getHHConfigs();
    }

    public static Handler getHandler() {
        return getConfiguration(ConfigKeys.HANDLER);
    }

    public static <T> T getConfiguration(Object key) {
        return getConfigurator().getConfiguration(key);
    }

    public static Context getApplicationContext() {
        return (Context) getConfigurators().get(ConfigKeys.APPLICATION_CONTEXT);
    }

}
