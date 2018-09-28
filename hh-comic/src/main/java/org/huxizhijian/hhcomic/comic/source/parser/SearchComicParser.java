package org.huxizhijian.hhcomic.comic.source.parser;

import org.huxizhijian.hhcomic.comic.bean.ComicResultList;

import okhttp3.Request;

/**
 * 漫画搜索结果解析器
 *
 * @author huxizhijian
 * @date 2018/8/31
 */
public interface SearchComicParser {

    /**
     * 构建搜索请求Request
     *
     * @param searchKey 搜索关键词
     * @param page      结果页码
     * @return request
     */
    Request buildSearchRequest(String searchKey, int page);

    /**
     * 解析搜索结果
     *
     * @param html      html
     * @param searchKey 搜索关键词
     * @param page      页码
     * @return result list
     */
    ComicResultList parseSearchList(byte[] html, String searchKey, int page);
}
