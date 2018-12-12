/*
 * Copyright 2016-2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.hhcomic.model.comic.db;

import org.huxizhijian.hhcomic.model.comic.db.dao.ChapterDao;
import org.huxizhijian.hhcomic.model.comic.db.dao.ComicDao;
import org.huxizhijian.hhcomic.model.comic.db.entity.Chapter;
import org.huxizhijian.hhcomic.model.comic.db.entity.Comic;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * 数据库持有者
 *
 * @author huxizhijian
 * @date 2018/11/13
 */
@Database(entities = {Comic.class, Chapter.class}, version = 1)
public abstract class HHDatabase extends RoomDatabase {

    /**
     * 自动实现的实例初始化方法
     *
     * @return comicDao
     */
    public abstract ComicDao comicDao();

    /**
     * 自动实现的实例初始化方法
     *
     * @return Chapter
     */
    public abstract ChapterDao chapterDao();
}
