package org.huxizhijian.hhcomic.comic.source.parser;

import org.huxizhijian.hhcomic.comic.bean.Category;
import org.huxizhijian.hhcomic.comic.bean.ComicResultList;

import okhttp3.Request;

/**
 * 分类列表分析接口
 *
 * @author huxizhijian
 * @date 2018/8/31
 */
public interface CategoryParser {

    /**
     * 获取分类列表的请求Request
     *
     * @param category 分类实体类
     * @param page     列表第几页
     * @return request
     */
    Request buildCategoryRequest(Category category, int page);

    /**
     * 分析请求返回的数据，将网页转换成实体类的结果列表
     *
     * @param html     网页数据
     * @param category 分类
     * @param page     请求的列表第几页
     * @return 返回结果列表实体类，包含一些额外信息
     */
    ComicResultList parseCategoryList(byte[] html, Category category, int page);
}
