package org.huxizhijian.sdk.imagedownload.core.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author huxizhijian 2017/3/15
 */
public class TaskDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "task.db";
    private static final int VERSION = 1;

    private static final String CREATE_TABLE_TASK = "CREATE TABLE IF NOT EXISTS task_table(" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT ," +
            TaskMetaData.TaskInfo.TASK_POSITION + " INTEGER ," +
            TaskMetaData.TaskInfo.TASK_COUNT + " INTEGER ," +
            TaskMetaData.TaskInfo.DOWNLOAD_POSITION + " INTEGER ," +
            TaskMetaData.TaskInfo.LENGTH + " INTEGER ," +
            TaskMetaData.TaskInfo.FINISHED + " INTEGER ," +
            TaskMetaData.TaskInfo.CHID + " BIGINT ," +
            TaskMetaData.TaskInfo.DOWNLOAD_PATH + " TEXT" +
            ")";
    private static final String DROP_TABLE_TASK = "DROP TABLE IF EXISTS task_table";

    public TaskDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TASK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_TASK);
        db.execSQL(CREATE_TABLE_TASK);
    }

    public void deleteTable(SQLiteDatabase db) {
        db.execSQL(DROP_TABLE_TASK);
        db.execSQL(CREATE_TABLE_TASK);
    }
}