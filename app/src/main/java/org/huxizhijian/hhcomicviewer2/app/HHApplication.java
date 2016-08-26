package org.huxizhijian.hhcomicviewer2.app;

import android.app.Application;

import org.xutils.DbManager;
import org.xutils.x;

/**
 * 本工程的application
 * Created by wei on 2016/8/23.
 */
public class HHApplication extends Application {

    private DbManager.DaoConfig daoConfig;

    public DbManager.DaoConfig getDaoConfig() {
        return this.daoConfig;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化xUtils
        x.Ext.init(this);
        //开启debug模式
        x.Ext.setDebug(true);
        //初始化数据库
        daoConfig = new DbManager.DaoConfig()
                .setDbName("comic_db")
                .setDbVersion(1)
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {

                    }
                });
    }
}
