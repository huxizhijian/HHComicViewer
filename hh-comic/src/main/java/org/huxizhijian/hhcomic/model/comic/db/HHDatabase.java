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
