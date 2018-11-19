package org.huxizhijian.hhcomic.model.comic.service.source.base.parser;

import org.huxizhijian.hhcomic.model.comic.db.entity.Chapter;
import org.huxizhijian.hhcomic.model.comic.db.entity.Comic;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

/**
 * 漫画详情解析器
 *
 * @author huxizhijian
 * @date 2018/8/31
 */
public interface ComicInfoParser {

    /**
     * 漫画详情网页获取Request构造
     *
     * @param comicId comic id
     * @return request
     */
    Request buildComicInfoRequest(String comicId);

    /**
     * 获取漫画详情
     *
     * @param html    html
     * @param comicId comic id
     * @return comic
     * @throws UnsupportedEncodingException 可能出现的转码异常
     */
    Comic getComicInfo(byte[] html, String comicId) throws UnsupportedEncodingException;

    /**
     * 获取章节详情（如果需要网络加载，可以在方法实现中访问网络）
     *
     * @param html    html
     * @param comicId comic id
     * @return map of chapter
     * @throws UnsupportedEncodingException 可能出现的转码异常
     */
    Map<String, List<Chapter>> getChaptersInfo(byte[] html, String comicId) throws UnsupportedEncodingException;
}
