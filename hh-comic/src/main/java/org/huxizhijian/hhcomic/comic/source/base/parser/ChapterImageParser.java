package org.huxizhijian.hhcomic.comic.source.base.parser;

import org.huxizhijian.hhcomic.comic.bean.result.ChapterImage;

import java.io.UnsupportedEncodingException;

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
     * @param extra     额外信息，比如图片服务器信息，这些信息可以在comic实体类中获得
     * @return request
     */
    Request buildChapterRequest(String comicId, String chapterId, String extra);

    /**
     * 章节image实体类分析
     *
     * @param html      HTML数据
     * @param comicId   comic id
     * @param chapterId chapter id
     * @param extra     额外信息，比如图片服务器信息，这些信息可以在comic实体类中获得
     * @return chapter image
     * @throws UnsupportedEncodingException 可能出现的转码异常
     */
    ChapterImage getChapterImage(byte[] html, String comicId, String chapterId, String extra)
            throws UnsupportedEncodingException;
}
