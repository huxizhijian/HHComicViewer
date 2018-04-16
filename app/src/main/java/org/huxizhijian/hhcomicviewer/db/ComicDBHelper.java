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

package org.huxizhijian.hhcomicviewer.db;

import android.content.Context;

import org.huxizhijian.hhcomicviewer.app.HHApplication;
import org.huxizhijian.hhcomicviewer.model.Comic;
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
                if (comic.getChapterNameList() == null || "".equals(comic.getChapterNameList())) {
                    comic.saveChapterNameList();
                }
                if (comic.getChapterIdList() == null || "".equals(comic.getChapterIdList())) {
                    comic.saveChapterIdList();
                }
            }
            if (comic.isUpdate()) {
                comic.saveChapterNameList();
                comic.saveChapterIdList();
            }
            sDb.save(comic);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void update(Comic comic) {
        try {
            if (comic.isMark() || comic.isDownload()) {
                if (comic.getChapterNameList() == null || "".equals(comic.getChapterNameList())) {
                    comic.saveChapterNameList();
                }
                if (comic.getChapterIdList() == null || "".equals(comic.getChapterIdList())) {
                    comic.saveChapterIdList();
                }
            }
            if (comic.isUpdate()) {
                comic.saveChapterNameList();
                comic.saveChapterIdList();
                comic.setUpdate(false);
            }
            sDb.update(comic);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public Comic findByCid(int cid) {
        Comic comic = findByCidInTable(cid);
        if (comic != null) {
            if (comic.isMark() || comic.isDownload()) {
                comic.initChapterNameAndList();
            }
        }
        return comic;
    }

    //直接读取表内相符url的Comic类
    private Comic findByCidInTable(int cid) {
        Comic comic = null;
        try {
            comic = sDb.selector(Comic.class).where("cid", "=", cid).findFirst();
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
