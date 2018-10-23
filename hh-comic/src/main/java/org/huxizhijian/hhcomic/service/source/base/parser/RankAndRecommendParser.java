package org.huxizhijian.hhcomic.service.source.base.parser;

import org.huxizhijian.hhcomic.service.bean.ComicListBean;
import org.huxizhijian.hhcomic.service.bean.FilterList;
import org.huxizhijian.hhcomic.service.bean.result.ComicResultList;

import java.io.UnsupportedEncodingException;

import okhttp3.Request;

/**
 * 漫画排行/推荐列表的解析器
 *
 * @author huxizhijian
 * @date 2018/8/31
 */
public interface RankAndRecommendParser {

    /**
     * 构建排行榜请求Request
     *
     * @param listBean 排行的实体类
     * @param page     页码
     * @param picker   过滤选择器
     * @return request
     */
    Request buildRankRequest(ComicListBean listBean, int page, FilterList.FilterPicker picker);

    /**
     * 解析排行结果
     *
     * @param html     html
     * @param listBean 列表信息实体类
     * @param page     页码
     * @return 结果
     * @throws UnsupportedEncodingException 可能出现的解析错误
     */
    ComicResultList parseRankList(byte[] html, ComicListBean listBean, int page) throws UnsupportedEncodingException;

    /**
     * 构建推荐请求Request，推荐通常是主页推荐，所以只需一次请求（如果需要二次请求在parse里进行）并且只需只需一页
     *
     * @return request
     */
    Request buildRecommendRequest();

    /**
     * 解析推荐结果
     *
     * @param html     html
     * @param listBean 列表信息实体类
     * @return 结果
     * @throws UnsupportedEncodingException 可能出现的解析错误
     */
    ComicResultList parseRecommendList(byte[] html, ComicListBean listBean) throws UnsupportedEncodingException;
}
