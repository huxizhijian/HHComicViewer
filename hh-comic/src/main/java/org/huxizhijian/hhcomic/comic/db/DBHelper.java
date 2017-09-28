package org.huxizhijian.hhcomic.comic.db;

import org.huxizhijian.hhcomic.comic.bean.Comic;

import java.util.List;

/**
 * @Author huxizhijian on 2017/9/26.
        */

public interface DBHelper {

    void insert(Comic comic);

    void update(Comic comic);

    void delete(Comic comic);

    Comic get(long comicId);

    List<Comic> getAll();

}
