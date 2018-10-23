package org.huxizhijian.hhcomic.service.bean;

import android.support.annotation.NonNull;

import org.huxizhijian.hhcomic.service.bean.base.BaseListBuilder;
import org.huxizhijian.hhcomic.service.bean.base.ResultFilterable;

/**
 * 推荐、排行信息实体类
 *
 * @author huxizhijian
 * @date 2018/8/31
 */
public final class ComicListBean implements ResultFilterable {

    /**
     * 源key
     */
    private final String mSourceKey;

    /**
     * 推荐、排行的名称
     */
    private final String mListName;

    /**
     * 漫画推荐或者排行的id
     */
    private final String mListId;

    /**
     * 是 - 推荐
     * 否 - 排行
     */
    private final boolean mIsRecommended;

    /**
     * 结果是否可被过滤
     */
    private final boolean mIsResultFilterable;

    /**
     * 过滤实体类（按组别对应的实力类列表）
     */
    private final FilterList mFilterList;

    public ComicListBean(String sourceKey, String listName, String listId, boolean isRecommended) {
        mSourceKey = sourceKey;
        mListName = listName;
        mListId = listId;
        mIsRecommended = isRecommended;
        mIsResultFilterable = false;
        mFilterList = null;
    }

    public ComicListBean(String sourceKey, String listName, String listId, boolean isRecommended,
                         @NonNull FilterList filterList) {
        mSourceKey = sourceKey;
        mListName = listName;
        mListId = listId;
        mIsRecommended = isRecommended;
        mIsResultFilterable = true;
        mFilterList = filterList;
    }

    public String getSourceKey() {
        return mSourceKey;
    }

    public String getListName() {
        return mListName;
    }

    public String getListId() {
        return mListId;
    }

    public boolean isRecommended() {
        return mIsRecommended;
    }

    @Override
    public boolean isResultFilterable() {
        return mIsResultFilterable;
    }

    @Override
    public FilterList getFilterList() {
        return mFilterList;
    }

    public static final class ListBuilder extends BaseListBuilder<ComicListBean> {

        private final String mSourceKey;
        private final boolean mIsRecommended;

        public ListBuilder(String sourceKey, boolean isRecommended) {
            mSourceKey = sourceKey;
            mIsRecommended = isRecommended;
        }

        public ListBuilder addListBean(String listName, String listId) {
            mList.add(new ComicListBean(mSourceKey, listName, listId, mIsRecommended));
            return this;
        }

        public ListBuilder addListBean(String listName, String listId, FilterList filterList) {
            mList.add(new ComicListBean(mSourceKey, listName, listId, mIsRecommended, filterList));
            return this;
        }

        public ListBuilder addListBean(ComicListBean comicListBean) {
            mList.add(comicListBean);
            return this;
        }
    }
}
