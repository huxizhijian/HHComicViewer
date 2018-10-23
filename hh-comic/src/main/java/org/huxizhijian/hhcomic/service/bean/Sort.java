package org.huxizhijian.hhcomic.service.bean;

import org.huxizhijian.hhcomic.service.bean.base.BaseListBuilder;
import org.huxizhijian.hhcomic.service.bean.base.Tag;

/**
 * @author huxizhijian
 * @date 2018/10/17
 */
public final class Sort extends Tag {

    public Sort(String id, String name, String sourceId) {
        super(null, id, name, sourceId);
    }

    public static class ListBuilder extends BaseListBuilder<Sort> {

        private String mSourceId;

        public ListBuilder(String sourceId) {
            mSourceId = sourceId;
        }

        public ListBuilder add(String id, String name) {
            mList.add(new Sort(id, name, mSourceId));
            return this;
        }
    }
}
