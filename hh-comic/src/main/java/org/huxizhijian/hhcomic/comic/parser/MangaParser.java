package org.huxizhijian.hhcomic.comic.parser;

import org.huxizhijian.hhcomic.comic.bean.Comic;

import java.util.List;

import okhttp3.Request;

/**
 * 提供漫画分析器{@link BaseParser}的基本实现
 *
 * @Author huxizhijian on 2017/9/25.
 */

public abstract class MangaParser implements BaseParser {

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

}
