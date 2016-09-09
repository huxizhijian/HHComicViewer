package org.huxizhijian.hhcomicviewer2.db;

import android.content.Context;

import org.huxizhijian.hhcomicviewer2.app.HHApplication;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.enities.Comic;
import org.huxizhijian.hhcomicviewer2.enities.ComicCapture;
import org.huxizhijian.hhcomicviewer2.enities.ThreadInfo;
import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ComicCapture的DB操作类
 * Created by wei on 2016/9/5.
 */
public class ComicCaptureDBHelper {

    private static ComicCaptureDBHelper dbHelper;
    private static DbManager db;
    private static HHApplication application;

    private ComicCaptureDBHelper(Context context) {
        db = x.getDb(((HHApplication) context.getApplicationContext()).getDaoConfig());
    }

    public Map<String, List<ComicCapture>> findDownloadCaptureMap(List<Comic> downloadedComics) {
        Map<String, List<ComicCapture>> downloadedCaptureMap = new HashMap<>();
        List<ComicCapture> comicCaptures;
        for (int i = 0; i < downloadedComics.size(); i++) {
            comicCaptures = findByComicUrl(downloadedComics.get(i).getComicUrl());
            downloadedCaptureMap.put(downloadedComics.get(i).getComicUrl(), comicCaptures);
        }
        return downloadedCaptureMap;
    }

    public List<ComicCapture> findUnFinishedCaptures() {
        List<ComicCapture> unFinishedCaptures = null;
        try {
            unFinishedCaptures = db.selector(ComicCapture.class)
                    .where("download_status", "!=", Constants.DOWNLOAD_FINISHED).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return unFinishedCaptures;
    }

    public static ComicCaptureDBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new ComicCaptureDBHelper(context);
            return dbHelper;
        } else {
            return dbHelper;
        }
    }

    public synchronized void add(ComicCapture comicCapture) {
        try {
            db.save(comicCapture);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void delete(ComicCapture comicCapture) {
        WhereBuilder builder = WhereBuilder.b("capture_url", "=", comicCapture.getCaptureUrl());
        try {
            db.delete(ComicCapture.class, builder);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void update(ComicCapture comicCapture) {
        try {
            db.update(comicCapture, "capture_name", "download_status"
                    , "page_count", "comic_title", "comic_url");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void deleteComicCaptureOneComic(String comicUrl) {
        WhereBuilder builder = WhereBuilder.b("comic_url", "=", comicUrl);
        try {
            db.delete(ThreadInfo.class, builder);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private List<ComicCapture> findByComicUrl(String comicUrl) {
        List<ComicCapture> comicCaptures = null;
        try {
            comicCaptures = db.selector(ComicCapture.class).where("comic_url", "=", comicUrl).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return comicCaptures;
    }

    @Deprecated
    public List<ComicCapture> findAll() {
        List<ComicCapture> comicCaptures = new ArrayList<>();
        try {
            comicCaptures = db.findAll(ComicCapture.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return comicCaptures;
    }
}
