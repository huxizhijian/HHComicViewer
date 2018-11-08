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

package org.huxizhijian.hhcomicviewer.app;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import org.huxizhijian.hhcomic.HHComic;

/**
 * 本工程的application
 *
 * @author huxizhijian on 2016/8/23.
 */
public class HHApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initLeakCanary();
        // 初始化core集成的第三方库和一些全局变量
        HHComic.init(this);
    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
