package org.huxizhijian.hhcomic.service.bean;

import org.huxizhijian.hhcomic.service.bean.base.BaseListBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 过滤列表，不是一般的表结构，类似于Map结构
 *
 * @author huxizhijian
 * @date 2018/10/18
 */
public final class FilterList {

    private final Map<String, List<Filter>> mFilterListMap;

    private FilterList(Map<String, List<Filter>> filterListMap) {
        mFilterListMap = filterListMap;
    }

    public List<String> getSubtitleList() {
        Set<String> keySet = mFilterListMap.keySet();
        return new ArrayList<>(keySet);
    }

    public List<Filter> getFilterList(String subtitle) {
        // 克隆一个新的list实例，因为这是不可变类，Filter实例没有克隆，因为它也是不可变类
        return new ArrayList<>(mFilterListMap.get(subtitle));
    }

    public FilterPicker pick(String subtitle, Filter filter) {
        return new FilterPicker(getSubtitleList()).pick(subtitle, filter);
    }

    public static Builder newFilterList(String sourceId) {
        return new Builder(sourceId);
    }

    /**
     * 辅助构造FilterList这个不可变类的builder
     */
    public static class Builder {

        private String mSourceId;
        private Map<String, List<Filter>> mFilterListMap;

        public Builder(String sourceId) {
            mSourceId = sourceId;
            mFilterListMap = new HashMap<>();
        }

        public FilterItemListBuilder beginSubtitle(String subtitle) {
            return new FilterItemListBuilder(subtitle, this);
        }

        private Builder endSubtitle(FilterItemListBuilder builder) {
            mFilterListMap.put(builder.mSubtitle, builder.build());
            return this;
        }

        public FilterList build() {
            return new FilterList(mFilterListMap);
        }

        public static class FilterItemListBuilder extends BaseListBuilder<Filter> {

            private String mSubtitle;
            private Builder mBuilder;

            FilterItemListBuilder(String subtitle, Builder builder) {
                mSubtitle = subtitle;
                mBuilder = builder;
            }

            public FilterItemListBuilder add(String id, String name) {
                mList.add(new Filter(mSubtitle, id, name, mBuilder.mSourceId));
                return this;
            }

            public Builder endSubtitle() {
                return mBuilder.endSubtitle(this);
            }
        }
    }

    /**
     * 帮助选择filter的类
     */
    public static class FilterPicker {

        private final List<String> mSubtitleList;
        private final Map<String, Filter> mPickMap;

        FilterPicker(List<String> subtitleList) {
            mPickMap = new HashMap<>();
            for (String subtitle : subtitleList) {
                mPickMap.put(subtitle, null);
            }
            mSubtitleList = subtitleList;
        }

        public FilterPicker pick(String subtitle, Filter filter) {
            mPickMap.put(subtitle, filter);
            return this;
        }

        public FilterPicker unPick(String subtitle) {
            mPickMap.put(subtitle, null);
            return this;
        }

        public Map<String, Filter> getPickMap() {
            return mPickMap;
        }

        public Filter getPick(String subtitle) {
            return mPickMap.get(subtitle);
        }

        public List<String> getSubtitleList() {
            return mSubtitleList;
        }
    }
}
