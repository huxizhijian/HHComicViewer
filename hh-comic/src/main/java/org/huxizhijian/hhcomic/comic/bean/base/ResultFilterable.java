package org.huxizhijian.hhcomic.comic.bean.base;

import org.huxizhijian.hhcomic.comic.bean.FilterList;

/**
 * @author huxizhijian
 * @date 2018/10/10
 */
public interface ResultFilterable {
    /**
     * 结果是否可被过滤
     *
     * @return result can filter?
     */
    boolean isResultFilterable();

    /**
     * 返回过滤的实体类列表
     *
     * @return filter list
     */
    FilterList getFilterList();
}
