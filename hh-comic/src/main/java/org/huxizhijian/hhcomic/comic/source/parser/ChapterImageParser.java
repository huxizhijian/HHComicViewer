package org.huxizhijian.hhcomic.comic.source.parser;

import org.huxizhijian.hhcomic.comic.bean.ChapterImage;

import okhttp3.Request;

/**
 * 章节图片地址分析
 *
 * @author huxizhijian
 * @date 2018/8/31
 */
public interface ChapterImageParser {

    /**
     * 章节详情获取request
     *
     * @param comicId   comic id
     * @param chapterId chapter id
     * @return request
     */
    Request buildChapterRequest(String comicId, String chapterId);

    /**
     * 章节image实体类分析
     *
     * @param html      HTML数据
     * @param comicId   comic id
     * @param chapterId chapter id
     * @return chapter image
     */
    ChapterImage getChapterImage(byte[] html, String comicId, String chapterId);
}
