/*
 * Copyright 2018 huxizhijian
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
