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
import org.huxizhijian.hhcomicviewer.model.ThreadInfo;
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

    private static DownloadThreadDBHelper sDbHelper;
    private static DbManager sDb;

    private DownloadThreadDBHelper(Context context) {
        sDb = x.getDb(((HHApplication) context.getApplicationContext()).getDaoConfig());
    }

    public static DownloadThreadDBHelper getInstance(Context context) {
        if (sDbHelper == null) {
            sDbHelper = new DownloadThreadDBHelper(context);
            return sDbHelper;
        } else {
            return sDbHelper;
        }
    }

    public synchronized void add(ThreadInfo threadInfo) {
        try {
            sDb.save(threadInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void update(ThreadInfo threadInfo) {
        try {
            sDb.update(threadInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public synchronized void deleteAllChapterThread(long chid) {
        WhereBuilder builder = WhereBuilder.b("chid", "=", chid);
        try {
            sDb.delete(ThreadInfo.class, builder);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public List<ThreadInfo> findByChapterUrl(long chid) {
        List<ThreadInfo> threadInfos = new ArrayList<>();
        ThreadInfo threadInfo;
        try {
            List<DbModel> dbModels = sDb.findDbModelAll(new SqlInfo("select * from thread_info where chid = " +
                    "'" + chid + "'"));
            for (DbModel dbModel : dbModels) {
                threadInfo = new ThreadInfo();
                threadInfo.setId(dbModel.getInt("id"));
                threadInfo.setThreadPosition(dbModel.getInt("thread_position"));
                threadInfo.setThreadCount(dbModel.getInt("thread_count"));
                threadInfo.setDownloadPosition(dbModel.getInt("download_position"));
                threadInfo.setLength(dbModel.getInt("length"));
                threadInfo.setFinished(dbModel.getInt("finished"));
                threadInfo.setChid(dbModel.getLong("chid"));
                threadInfos.add(threadInfo);
            }
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
        return threadInfos;
    }

    public List<ThreadInfo> findAll() {
        List<ThreadInfo> threadInfos = new ArrayList<>();
        try {
            sDb.findAll(ThreadInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
        return threadInfos;
    }
}
