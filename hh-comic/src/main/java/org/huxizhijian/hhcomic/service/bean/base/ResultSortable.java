package org.huxizhijian.hhcomic.service.bean.base;

import org.huxizhijian.hhcomic.service.bean.Sort;

import java.util.List;

/**
 * @author huxizhijian
 * @date 2018/10/11
 */
public interface ResultSortable {
    /**
     * 结果是否有可选择的排序
     *
     * @return is result sortable
     */
    boolean isResultSortable();

    /**
     * 获取排序实体列表
     *
     * @return sort list
     */
    List<Sort> getResultSortList();
}
