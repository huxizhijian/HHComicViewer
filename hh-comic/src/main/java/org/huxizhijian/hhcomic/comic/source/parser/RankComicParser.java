package org.huxizhijian.hhcomic.comic.source.parser;

import org.huxizhijian.hhcomic.comic.bean.ComicRank;
import org.huxizhijian.hhcomic.comic.bean.ComicResultList;

import okhttp3.Request;

/**
 * 漫画排行/推荐列表的解析器
 *
 * @author huxizhijian
 * @date 2018/8/31
 */
public interface RankComicParser {

    /**
     * 构建排行/推荐榜请求Request
     *
     * @param rank 排行的实体类
     * @param page 页码，排行大概率只有一页
     * @return request
     */
    Request buildRankRequest(ComicRank rank, int page);

    /**
     * 解析排行/推荐结果
     *
     * @param html html
     * @param page 页码
     * @return 结果
     */
    ComicResultList parseRankList(byte[] html, int page);
}
