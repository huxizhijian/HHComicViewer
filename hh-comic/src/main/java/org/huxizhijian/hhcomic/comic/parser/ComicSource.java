package org.huxizhijian.hhcomic.comic.parser;

import org.huxizhijian.hhcomic.comic.bean.Comic;
import org.huxizhijian.hhcomic.comic.db.DBHelper;
import org.huxizhijian.hhcomic.comic.type.ComicDataSourceType;

import java.util.List;

import okhttp3.Request;

/**
 * 提供分析器{@link BaseParser}的基本实现
 *
 * @Author huxizhijian on 2017/9/25.
 */
public abstract class ComicSource implements BaseParser, DBHelper, ComicDataSourceType {

    @Override
    public Request buildSearchRequest(String keyword, int page) {
        return null;
    }

    @Override
    public List<Comic> getSearchResult(String data, int page) {
        return null;
    }

    @Override
    public Request buildDetailRequest(String cid) {
        return null;
    }

    @Override
    public void parseInfo(String data, Comic comic) {

    }

    @Override
    public Request buildChapterRequest(String data, String cid) {
        return null;
    }

    public abstract String getSourceTag();

    @Override
    public void insert(Comic comic) {

    }

    @Override
    public void update(Comic comic) {

    }

    @Override
    public void delete(Comic comic) {

    }

    @Override
    public Comic get(long comicId) {
        return null;
    }

    @Override
    public List<Comic> getAll() {
        return null;
    }

}
