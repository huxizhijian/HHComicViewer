package org.huxizhijian.hhcomicviewer2.db;

import android.content.Context;

import org.huxizhijian.hhcomicviewer2.app.HHApplication;
import org.huxizhijian.hhcomicviewer2.vo.ThreadInfo;
import org.xutils.DbManager;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 下载线程DB操作
 * Created by wei on 2016/9/5.
 */
public class DownloadThreadDBHelper {
    private static DownloadThreadDBHelper dbHelper;
    private DbManager db;

    private DownloadThreadDBHelper(Context context) {
        db = x.getDb(((HHApplication) context.getApplicationContext()).getDaoConfig());
    }

    public static DownloadThreadDBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DownloadThreadDBHelper(context);
            return dbHelper;
        } else {
            return dbHelper;
        }
    }

    public synchronized void add(ThreadInfo threadInfo) {
        try {
            db.save(threadInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void update(ThreadInfo threadInfo) {
        try {
            db.update(threadInfo, "id", "thread_position", "thread_count", "download_position"
                    , "length", "finished", "comic_capture_url");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void deleteAllCaptureThread(String comicCaptureUrl) {
        WhereBuilder builder = WhereBuilder.b("comic_capture_url", "=", comicCaptureUrl);
        try {
            db.delete(ThreadInfo.class, builder);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public List<ThreadInfo> findByCaptureUrl(String captureUrl) {
        List<ThreadInfo> threadInfos = new ArrayList<>();
        ThreadInfo threadInfo;
        try {
            List<DbModel> dbModels = db.findDbModelAll(new SqlInfo("select * from thread where capture_url = " + captureUrl));
            for (DbModel dbModel : dbModels) {
                threadInfo = new ThreadInfo();
                threadInfo.setId(dbModel.getInt("id"));
                threadInfo.setThreadPosition(dbModel.getInt("thread_position"));
                threadInfo.setThreadCount(dbModel.getInt("thread_count"));
                threadInfo.setDownloadPosition(dbModel.getInt("download_position"));
                threadInfo.setLength(dbModel.getInt("length"));
                threadInfo.setFinished(dbModel.getInt("finished"));
                threadInfo.setComicCaptureUrl(dbModel.getString("comic_capture_url"));
                threadInfos.add(threadInfo);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return threadInfos;
    }

    public List<ThreadInfo> findAll() {
        List<ThreadInfo> threadInfos = new ArrayList<>();
        try {
            db.findAll(ThreadInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return threadInfos;
    }
}
