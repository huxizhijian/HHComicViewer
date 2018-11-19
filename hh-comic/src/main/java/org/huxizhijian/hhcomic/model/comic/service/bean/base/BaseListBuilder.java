package org.huxizhijian.hhcomic.model.comic.service.bean.base;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huxizhijian
 * @date 2018/10/17
 */
public abstract class BaseListBuilder<T> {

    protected final List<T> mList = new ArrayList<>();

    public BaseListBuilder add(T bean) {
        mList.add(bean);
        return this;
    }

    public List<T> build() {
        return mList;
    }
}
