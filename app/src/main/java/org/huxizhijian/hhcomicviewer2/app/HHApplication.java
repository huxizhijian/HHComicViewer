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

package org.huxizhijian.hhcomicviewer2.app;

import android.app.Application;

import org.xutils.DbManager;
import org.xutils.x;

/**
 * 本工程的application
 * Created by wei on 2016/8/23.
 */
public class HHApplication extends Application {


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

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化xUtils
        x.Ext.init(this);
        //开启debug模式
        x.Ext.setDebug(true);
    }
}
