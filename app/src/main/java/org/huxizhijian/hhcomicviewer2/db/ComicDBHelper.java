package org.huxizhijian.hhcomicviewer2.db;

import android.content.Context;

import org.huxizhijian.hhcomicviewer2.app.HHApplication;
import org.huxizhijian.hhcomicviewer2.vo.Comic;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

/**
 * DB类，管理全局数据
 * Created by wei on 2016/8/23.
 */
public class ComicDBHelper {

    private static ComicDBHelper comicDbHelper;
    private static DbManager db;

    private ComicDBHelper(Context context) {
        db = x.getDb(((HHApplication) context.getApplicationContext()).getDaoConfig());
    }

    public List<Comic> findMarkedComics() {
        List<Comic> markedComics = null;
        try {
            markedComics = db.selector(Comic.class).where("is_mark", "=", true).orderBy("last_read_time").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return markedComics;
    }

    public List<Comic> findDownloadedComics() {
        List<Comic> downloadedComics = null;
        try {
            downloadedComics = db.selector(Comic.class).where("is_download", "=", true).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return downloadedComics;
    }

    public synchronized void add(Comic comic) {
        try {
            comic.saveCaptureNameList();
            db.save(comic);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void update(Comic comic) {
        try {
            comic.saveCaptureNameList();
            db.update(comic, "title", "author", "description", "is_mark", "is_download",
                    "read_capture", "read_page", "last_read_time", "capture_count", "capture_name_list", "is_update");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public Comic findByUrl(String url) {
        Comic comic = findByUrlInTable(url);
        if (comic != null) {
            comic.getCaptureNameInString();
        }
        return comic;
    }

    //直接读取表内相符url的Comic类
    private Comic findByUrlInTable(String comicUrl) {
        Comic comic = null;
        try {
            comic = db.selector(Comic.class).where("comic_url", "=", comicUrl).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return comic;
    }

    //单例模式
    public static ComicDBHelper getInstance(Context context) {
        if (comicDbHelper == null) {
            comicDbHelper = new ComicDBHelper(context);
            return comicDbHelper;
        } else {
            return comicDbHelper;
        }
    }

    public List<Comic> findAll() {
        List<Comic> comics = null;
        try {
            comics = db.selector(Comic.class).orderBy("last_read_time", true).limit(100).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return comics;
    }
}
