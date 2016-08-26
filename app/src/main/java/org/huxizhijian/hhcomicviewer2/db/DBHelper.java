package org.huxizhijian.hhcomicviewer2.db;

import android.content.Context;

import org.huxizhijian.hhcomicviewer2.app.HHApplication;
import org.huxizhijian.hhcomicviewer2.vo.Comic;
import org.huxizhijian.hhcomicviewer2.vo.ComicCapture;
import org.xutils.DbManager;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * DB类，管理全局数据
 * Created by wei on 2016/8/23.
 */
public class DBHelper {

    private static DBHelper dbHelper;
    private DbManager db;
    ArrayList<Comic> comics;
    ArrayList<Comic> markedComics;
    ArrayList<Comic> downloadedComics;
    HashMap<String, ArrayList<ComicCapture>> downloadCaptures;

    private DBHelper(Context context) {
        db = x.getDb(((HHApplication) context.getApplicationContext()).getDaoConfig());
        try {
            comics = (ArrayList<Comic>) db.findAll(Comic.class);
            if (comics != null) {
                downloadCaptures = new HashMap<>();
                markedComics = new ArrayList<>();
                downloadedComics = new ArrayList<>();
                List<DbModel> models;
                ArrayList<ComicCapture> captures;
                ComicCapture capture;
                for (Comic comic : comics) {
                    //一重循环，遍历漫画下载的章节
                    if (comic.isDownload()) {
                        comic.getCaptureNameInString();
                        downloadedComics.add(comic);
                        models = db.findDbModelAll(new SqlInfo("select * from download where comic_title = " + comic.getTitle()));
                        captures = new ArrayList<>();
                        for (DbModel model : models) {
                            //二重循环，把每个章节循环赋值
                            capture = new ComicCapture(model.getString("comic_title"), model.getString("capture_name")
                                    , model.getString("capture_url"));
                            capture.setDownloadProgress(model.getInt("download_progress"));
                            capture.setId(model.getInt("id"));
                            capture.setDownloadStatus(model.getInt("download_status"));
                            capture.setPageCount(model.getInt("page_count"));
                            captures.add(capture);
                        }
                        downloadCaptures.put(comic.getComicUrl(), captures);
                    }
                    //同时将收藏的漫画遍历出来
                    if (comic.isMark()) {
                        if (comic.getCaptureName() == null) {
                            comic.getCaptureNameInString();
                        }
                        markedComics.add(comic);
                    }
                }
                //将漫画按照阅读时间排序
                Collections.sort(comics);
                Collections.sort(markedComics);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void add(Comic comic) {
        try {
            db.save(comic);
            comics.add(comic);
            Collections.sort(comics);
            if (comic.isMark()) {
                markedComics.add(comic);
                Collections.sort(markedComics);
            }
            if (comic.isDownload()) {
                downloadedComics.add(comic);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void update(Comic comic) {
        try {
            Comic savedComic = findByUrlInTable(comic.getComicUrl());
            db.update(comic, "id", "comic_url", "title", "author", "description", "is_mark", "is_download",
                    "read_capture", "read_page", "last_read_time", "capture_count", "capture_name_list");
            comics.set(comics.indexOf(comic), comic);
            if (comic.isMark()) {
                markedComics.set(comics.indexOf(savedComic), comic);
                Collections.sort(markedComics);
            }
            if (comic.isDownload()) {
                downloadedComics.set(comics.indexOf(savedComic), comic);
            }
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
            DbModel model = db.findDbModelFirst(new SqlInfo("select * from comic where comic_url = " + comicUrl));
            if (model == null) return null;
            comic = new Comic();
            comic.setId(model.getInt("id"));
            comic.setComicUrl(model.getString("comic_url"));
            comic.setAuthor(model.getString("author"));
            comic.setDescription(model.getString("description"));
            comic.setThumbnailUrl(model.getString("thumbnail_url"));
            comic.setDownload(model.getBoolean("is_download"));
            comic.setMark(model.getBoolean("is_mark"));
            comic.setLastReadTime(model.getLong("last_read_time"));
            comic.setTitle(model.getString("title"));
            comic.setReadPage(model.getInt("read_page"));
            comic.setReadCapture(model.getInt("read_capture"));
            comic.setCaptureNameList(model.getString("capture_name_list"));
            comic.setCaptureCount(model.getInt("capture_count"));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return comic;
    }

    //单例模式
    public static DBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
            return dbHelper;
        } else {
            return dbHelper;
        }
    }
}
