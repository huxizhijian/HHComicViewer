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

package org.huxizhijian.sdk.imagedownload.core.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.huxizhijian.sdk.imagedownload.core.model.TaskInfo;

import java.util.ArrayList;

/**
 * @author huxizhijian 2017/3/15
 */
public class DataBaseAdapter {

    private TaskDBHelper dbHelper;

    public DataBaseAdapter(Context context) {
        dbHelper = new TaskDBHelper(context);
    }

    //添加操作
    public synchronized void add(TaskInfo info) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskMetaData.TaskInfo.TASK_POSITION, info.getTaskPosition());
        values.put(TaskMetaData.TaskInfo.TASK_COUNT, info.getTaskCount());
        values.put(TaskMetaData.TaskInfo.DOWNLOAD_POSITION, info.getDownloadPosition());
        values.put(TaskMetaData.TaskInfo.LENGTH, info.getLength());
        values.put(TaskMetaData.TaskInfo.FINISHED, info.getFinished());
        values.put(TaskMetaData.TaskInfo.CHID, info.getChid());
        values.put(TaskMetaData.TaskInfo.DOWNLOAD_PATH, info.getDownloadPath());
        db.insert(TaskMetaData.TaskInfo.TABLE_NAME, null, values);
        db.close();
    }

    //删除操作
    public synchronized void delete(long chid) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = TaskMetaData.TaskInfo.CHID + "=?";
        String[] whereArgs = {String.valueOf(chid)};
        db.delete(TaskMetaData.TaskInfo.TABLE_NAME, whereClause, whereArgs);
        db.close();
    }

    //更新操作
    public synchronized void update(TaskInfo info) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskMetaData.TaskInfo.TASK_POSITION, info.getTaskPosition());
        values.put(TaskMetaData.TaskInfo.TASK_COUNT, info.getTaskCount());
        values.put(TaskMetaData.TaskInfo.DOWNLOAD_POSITION, info.getDownloadPosition());
        values.put(TaskMetaData.TaskInfo.LENGTH, info.getLength());
        values.put(TaskMetaData.TaskInfo.FINISHED, info.getFinished());
        values.put(TaskMetaData.TaskInfo.CHID, info.getChid());
        values.put(TaskMetaData.TaskInfo.DOWNLOAD_PATH, info.getDownloadPath());
        String whereClause = TaskMetaData.TaskInfo._ID + "=?";
        String[] whereArgs = {String.valueOf(info.getId())};
        db.update(TaskMetaData.TaskInfo.TABLE_NAME, values, whereClause, whereArgs);
        db.close();
    }

    //查询
    public synchronized ArrayList<TaskInfo> findByChid(long chid) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query(true, TaskMetaData.TaskInfo.TABLE_NAME, null,
                TaskMetaData.TaskInfo.CHID + "=?", new String[]{String.valueOf(chid)}, null, null, null, null);
        ArrayList<TaskInfo> taskInfoList = new ArrayList<>();
        TaskInfo info = null;
        while (c.moveToNext()) {
            info = new TaskInfo();
            info.setId(c.getInt(c.getColumnIndexOrThrow(TaskMetaData.TaskInfo._ID)));
            info.setTaskPosition(c.getInt(c.getColumnIndexOrThrow(TaskMetaData.TaskInfo.TASK_POSITION)));
            info.setTaskCount(c.getInt(c.getColumnIndexOrThrow(TaskMetaData.TaskInfo.TASK_COUNT)));
            info.setDownloadPosition(c.getInt(c.getColumnIndexOrThrow(TaskMetaData.TaskInfo.DOWNLOAD_POSITION)));
            info.setLength(c.getInt(c.getColumnIndexOrThrow(TaskMetaData.TaskInfo.LENGTH)));
            info.setFinished(c.getInt(c.getColumnIndexOrThrow(TaskMetaData.TaskInfo.FINISHED)));
            info.setChid(c.getLong(c.getColumnIndexOrThrow(TaskMetaData.TaskInfo.CHID)));
            info.setDownloadPath(c.getString(c.getColumnIndexOrThrow(TaskMetaData.TaskInfo.DOWNLOAD_PATH)));
            taskInfoList.add(info);
        }
        c.close();
        db.close();
        return taskInfoList;
    }

    public synchronized void deleteTable() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.deleteTable(db);
    }

}
