package org.huxizhijian.sdk.imagedownload.core.db;

import android.provider.BaseColumns;

/**
 * @author huxizhijian 2017/3/15
 */
public class TaskMetaData {

    private TaskMetaData() {
    }

    public static abstract class TaskInfo implements BaseColumns {
        public static final String TABLE_NAME = "task_table";
        public static final String TASK_POSITION = "task_position";
        public static final String TASK_COUNT = "task_count";
        public static final String DOWNLOAD_POSITION = "download_position";
        public static final String LENGTH = "length";
        public static final String FINISHED = "finished";
        public static final String CHID = "chid";
        public static final String DOWNLOAD_PATH = "download_path";
    }

}
