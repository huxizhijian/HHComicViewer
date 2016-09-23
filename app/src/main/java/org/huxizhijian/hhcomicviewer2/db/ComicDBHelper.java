package org.huxizhijian.hhcomicviewer2.db;

import android.content.Context;

import org.huxizhijian.hhcomicviewer2.app.HHApplication;
import org.huxizhijian.hhcomicviewer2.enities.Comic;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

/**
 * DB类，管理全局数据
 * Created by wei on 2016/8/23.
 */
public class ComicDBHelper {

    private static ComicDBHelper sComicDbHelper;
    private static DbManager sDb;

    private ComicDBHelper(Context context) {
        sDb = x.getDb(((HHApplication) context.getApplicationContext()).getDaoConfig());
    }

    public List<Comic> findMarkedComics() {
        List<Comic> markedComics = null;
        try {
            markedComics = sDb.selector(Comic.class).where("is_mark", "=", true).orderBy("last_read_time", true).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return markedComics;
    }

    public List<Comic> findDownloadedComics() {
        List<Comic> downloadedComics = null;
        try {
            downloadedComics = sDb.selector(Comic.class).where("is_download", "=", true).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return downloadedComics;
    }

    public synchronized void add(Comic comic) {
        try {
            if (comic.isMark() || comic.isDownload()) {
                if (comic.getCaptureNameList() == null || comic.getCaptureNameList().equals("")) {
                    comic.saveCaptureNameList();
                }
                if (comic.getCaptureUrlList() == null || comic.getCaptureUrlList().equals("")) {
                    comic.saveCaptureUrlList();
                }
            }
            if (comic.isUpdate()) {
                comic.saveCaptureNameList();
                comic.saveCaptureUrlList();
            }
            sDb.save(comic);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void update(Comic comic) {
        try {
            if (comic.isMark() || comic.isDownload()) {
                if (comic.getCaptureNameList() == null || comic.getCaptureNameList().equals("")) {
                    comic.saveCaptureNameList();
                }
                if (comic.getCaptureUrlList() == null || comic.getCaptureUrlList().equals("")) {
                    comic.saveCaptureUrlList();
                }
            }
            if (comic.isUpdate()) {
                comic.saveCaptureNameList();
                comic.saveCaptureUrlList();
                comic.setUpdate(false);
            }
            sDb.update(comic);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public Comic findByUrl(String url) {
        Comic comic = findByUrlInTable(url);
        if (comic != null) {
            if (comic.isMark() || comic.isDownload()) {
                comic.initCaptureNameAndList();
            }
        }
        return comic;
    }

    //直接读取表内相符url的Comic类
    private Comic findByUrlInTable(String comicUrl) {
        Comic comic = null;
        try {
            comic = sDb.selector(Comic.class).where("comic_url", "=", comicUrl).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return comic;
    }

    //单例模式
    public static ComicDBHelper getInstance(Context context) {
        if (sComicDbHelper == null) {
            sComicDbHelper = new ComicDBHelper(context);
            return sComicDbHelper;
        } else {
            return sComicDbHelper;
        }
    }

    public List<Comic> findAll() {
        List<Comic> comics = null;
        try {
            comics = sDb.selector(Comic.class).orderBy("last_read_time", true).limit(100).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return comics;
    }
}
