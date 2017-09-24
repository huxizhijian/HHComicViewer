/*
 * Copyright 2017 huxizhijian
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

package org.huxizhijian.hhcomicviewer2.db;

import android.content.Context;
import android.util.SparseArray;

import org.huxizhijian.hhcomicviewer2.app.HHApplication;
import org.huxizhijian.hhcomicviewer2.model.Comic;
import org.huxizhijian.hhcomicviewer2.model.ComicChapter;
import org.huxizhijian.hhcomicviewer2.model.ThreadInfo;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Comicchapter的DB操作类
 * Created by wei on 2016/9/5.
 */
public class ComicChapterDBHelper {

    private static ComicChapterDBHelper sDbHelper;
    private static DbManager sDb;

    private ComicChapterDBHelper(Context context) {
        sDb = x.getDb(((HHApplication) context.getApplicationContext()).getDaoConfig());
    }

    public SparseArray<List<ComicChapter>> findDownloadChapterMap(List<Comic> downloadedComics) {
        if (downloadedComics == null || downloadedComics.size() == 0) return new SparseArray<>();
        SparseArray<List<ComicChapter>> downloadedChapterMap = new SparseArray<>();
        List<ComicChapter> comicChapters;
        for (int i = 0; i < downloadedComics.size(); i++) {
            comicChapters = findByComicCid(downloadedComics.get(i).getCid());
            downloadedChapterMap.put(downloadedComics.get(i).getCid(), comicChapters);
        }
        return downloadedChapterMap;
    }

    public SparseArray<List<ComicChapter>> findDownloadedChapterMap(List<Comic> finishedComics) {
        if (finishedComics == null || finishedComics.size() == 0) return new SparseArray<>();
        SparseArray<List<ComicChapter>> downloadedChapterMap = new SparseArray<>();
        List<ComicChapter> comicChapters;
        for (int i = 0; i < finishedComics.size(); i++) {
            comicChapters = findByComicCid(finishedComics.get(i).getCid());
            List<ComicChapter> downloadedChapters = new ArrayList<>();
            for (ComicChapter chapter : comicChapters) {
                if (chapter.getDownloadStatus() == Constants.DOWNLOAD_FINISHED) {
                    downloadedChapters.add(chapter);
                }
            }
            downloadedChapterMap.put(finishedComics.get(i).getCid(), downloadedChapters);
        }
        return downloadedChapterMap;
    }

    public List<Comic> findFinishedComicList(List<Comic> downloadedComics) {
        if (downloadedComics == null || downloadedComics.size() == 0) return new ArrayList<>();
        List<Comic> finishedComics = new ArrayList<>();
        List<ComicChapter> comicChapters;
        for (int i = 0; i < downloadedComics.size(); i++) {
            comicChapters = findByComicCid(downloadedComics.get(i).getCid());
            for (int j = 0; j < comicChapters.size(); j++) {
                if (comicChapters.get(j).getDownloadStatus() == Constants.DOWNLOAD_FINISHED) {
                    finishedComics.add(downloadedComics.get(i));
                    break;
                }
            }
        }
        return finishedComics;
    }

    public List<ComicChapter> findUnFinishedChapters() {
        List<ComicChapter> unFinishedChapters = null;
        try {
            unFinishedChapters = sDb.selector(ComicChapter.class)
                    .where("download_status", "!=", Constants.DOWNLOAD_FINISHED).findAll();
            if (unFinishedChapters != null && unFinishedChapters.size() != 0) {
                Collections.sort(unFinishedChapters);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return unFinishedChapters;
    }

    public ComicChapter findByChapterId(long chid) {
        ComicChapter chapter = null;
        try {
            chapter = sDb.selector(ComicChapter.class).where("chid", "=", chid).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return chapter;
    }

    public static ComicChapterDBHelper getInstance(Context context) {
        if (sDbHelper == null) {
            sDbHelper = new ComicChapterDBHelper(context);
            return sDbHelper;
        } else {
            return sDbHelper;
        }
    }

    public synchronized void add(ComicChapter comicChapter) {
        try {
            sDb.save(comicChapter);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void delete(ComicChapter comicChapter) {
        WhereBuilder builder = WhereBuilder.b("chid", "=", comicChapter.getChid());
        try {
            sDb.delete(ComicChapter.class, builder);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void update(ComicChapter comicChapter) {
        try {
            sDb.update(comicChapter);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void updateProgress(ComicChapter comicChapter) {
        try {
            sDb.update(comicChapter, "download_position");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void deleteComicChapterOneComic(int cid) {
        WhereBuilder builder = WhereBuilder.b("cid", "=", cid);
        try {
            sDb.delete(ThreadInfo.class, builder);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public List<ComicChapter> findByComicCid(int cid) {
        List<ComicChapter> comicChapters = null;
        try {
            comicChapters = sDb.selector(ComicChapter.class).where("cid", "=", cid).findAll();
            if (comicChapters != null) {
                Collections.sort(comicChapters);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return comicChapters;
    }

    @Deprecated
    public List<ComicChapter> findAll() {
        List<ComicChapter> comicChapters = new ArrayList<>();
        try {
            comicChapters = sDb.findAll(ComicChapter.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return comicChapters;
    }
}
