package org.huxizhijian.hhcomicviewer.base;

/**
 * @author huxizhijian
 * @date 2017/11/13
 */
public interface BaseView<T extends BasePresenter> {
    /**
     * 可由present调用
     *
     * @param presenter presenter
     */
    void setPresenter(T presenter);
}
