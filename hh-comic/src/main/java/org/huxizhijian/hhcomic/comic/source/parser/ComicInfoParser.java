package org.huxizhijian.hhcomic.comic.source.parser;

import org.huxizhijian.hhcomic.comic.entity.Chapter;
import org.huxizhijian.hhcomic.comic.entity.Comic;

import java.util.List;

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
     */
    Comic getComicInfo(byte[] html, String comicId);

    /**
     * 获取章节详情
     *
     * @param html    html
     * @param comicId comic id
     * @return list of chapter
     */
    List<Chapter> getChaptersInfo(byte[] html, String comicId);
}
