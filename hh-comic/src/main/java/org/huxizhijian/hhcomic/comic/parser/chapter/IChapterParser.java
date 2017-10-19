package org.huxizhijian.hhcomic.comic.parser.chapter;

/**
 * 章节分析器，解析漫画图片地址
 *
 * @author huxizhijian
 * @date 2017/10/12
 */

public interface IChapterParser {

    /**
     * 是否有下一页
     */
    boolean hasNext();

    /**
     * 获取下一页的ImageUrl
     */
    String moveToNext();

}
