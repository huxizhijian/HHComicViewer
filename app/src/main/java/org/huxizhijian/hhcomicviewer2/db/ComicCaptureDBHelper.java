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

    private static ComicCaptureDBHelper sDbHelper;
    private static DbManager sDb;

    private ComicCaptureDBHelper(Context context) {
        sDb = x.getDb(((HHApplication) context.getApplicationContext()).getDaoConfig());
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
            unFinishedCaptures = sDb.selector(ComicCapture.class)
                    .where("download_status", "!=", Constants.DOWNLOAD_FINISHED).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return unFinishedCaptures;
    }

    public ComicCapture findByCaptureUrl(String captureUrl) {
        ComicCapture capture = null;
        try {
            capture = sDb.selector(ComicCapture.class).where("capture_url", "=", captureUrl).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return capture;
    }

    public static ComicCaptureDBHelper getInstance(Context context) {
        if (sDbHelper == null) {
            sDbHelper = new ComicCaptureDBHelper(context);
            return sDbHelper;
        } else {
            return sDbHelper;
        }
    }

    public synchronized void add(ComicCapture comicCapture) {
        try {
            sDb.save(comicCapture);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void delete(ComicCapture comicCapture) {
        WhereBuilder builder = WhereBuilder.b("capture_url", "=", comicCapture.getCaptureUrl());
        try {
            sDb.delete(ComicCapture.class, builder);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void update(ComicCapture comicCapture) {
        try {
            sDb.update(comicCapture);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void updateProgress(ComicCapture comicCapture){
        try {
            sDb.update(comicCapture,"download_position");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void deleteComicCaptureOneComic(String comicUrl) {
        WhereBuilder builder = WhereBuilder.b("comic_url", "=", comicUrl);
        try {
            sDb.delete(ThreadInfo.class, builder);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public List<ComicCapture> findByComicUrl(String comicUrl) {
        List<ComicCapture> comicCaptures = null;
        try {
            comicCaptures = sDb.selector(ComicCapture.class).where("comic_url", "=", comicUrl).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return comicCaptures;
    }

    @Deprecated
    public List<ComicCapture> findAll() {
        List<ComicCapture> comicCaptures = new ArrayList<>();
        try {
            comicCaptures = sDb.findAll(ComicCapture.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return comicCaptures;
    }
}
