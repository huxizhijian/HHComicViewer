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
