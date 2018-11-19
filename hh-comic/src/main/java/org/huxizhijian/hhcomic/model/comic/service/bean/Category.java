package org.huxizhijian.hhcomic.model.comic.service.bean;

import org.huxizhijian.hhcomic.model.comic.service.bean.base.BaseListBuilder;
import org.huxizhijian.hhcomic.model.comic.service.bean.base.ResultFilterable;
import org.huxizhijian.hhcomic.model.comic.service.bean.base.ResultSortable;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * 漫画分类信息实体类
 *
 * @author huxizhijian
 * @date 2018/8/30
 */
public final class Category implements ResultFilterable, ResultSortable {

    /**
     * 漫画源
     */
    private final String mSourceKey;

    /**
     * 分类名称
     */
    private final String mCategoryName;

    /**
     * 分类id
     */
    private final String mCategoryId;

    /**
     * 结果筛选的主题，如果主题和分类id一样，就当做该筛选被选中
     */
    private final String mSubtitle;

    /**
     * 分类图片
     */
    private final String mCover;

    /**
     * 结果是否能被筛选
     */
    private final boolean mIsFilterable;

    /**
     * 结果筛选实体列表
     */
    private final FilterList mFilterList;

    /**
     * 结果是否能被排序
     */
    private final boolean mIsSortable;

    /**
     * 结果排序实体列表
     */
    private final List<Sort> mSortList;

    public Category(String sourceKey, String categoryName, String categoryId, String cover) {
        mSourceKey = sourceKey;
        mCategoryName = categoryName;
        mCategoryId = categoryId;
        mCover = cover;
        mFilterList = null;
        mIsFilterable = false;
        mSortList = null;
        mIsSortable = false;
        mSubtitle = null;
    }

    public Category(String sourceKey, String categoryName, String categoryId, String cover, FilterList filterList) {
        mSourceKey = sourceKey;
        mCategoryName = categoryName;
        mCategoryId = categoryId;
        mCover = cover;
        mFilterList = filterList;
        mIsFilterable = true;
        mSortList = null;
        mIsSortable = false;
        mSubtitle = null;
    }

    public Category(String sourceKey, String categoryName, String categoryId, String cover,
                    String subtitle, FilterList filterList) {
        mSourceKey = sourceKey;
        mCategoryName = categoryName;
        mCategoryId = categoryId;
        mCover = cover;
        mFilterList = filterList;
        mIsFilterable = true;
        mSortList = null;
        mIsSortable = false;
        mSubtitle = subtitle;
    }

    public Category(String sourceKey, String categoryName, String categoryId, String cover, List<Sort> sortList) {
        mSourceKey = sourceKey;
        mCategoryName = categoryName;
        mCategoryId = categoryId;
        mCover = cover;
        mFilterList = null;
        mIsFilterable = false;
        mSortList = sortList;
        mIsSortable = true;
        mSubtitle = null;
    }

    public Category(String sourceKey, String categoryName, String categoryId, String cover,
                    FilterList filterList, List<Sort> sortList) {
        mSourceKey = sourceKey;
        mCategoryName = categoryName;
        mCategoryId = categoryId;
        mCover = cover;
        mFilterList = filterList;
        mIsFilterable = true;
        mSortList = sortList;
        mIsSortable = true;
        mSubtitle = null;
    }

    public Category(String sourceKey, String categoryName, String categoryId, String cover, String subtitle,
                    FilterList filterList, List<Sort> sortList) {
        mSourceKey = sourceKey;
        mCategoryName = categoryName;
        mCategoryId = categoryId;
        mCover = cover;
        mFilterList = filterList;
        mIsFilterable = true;
        mSortList = sortList;
        mIsSortable = true;
        mSubtitle = subtitle;
    }

    public String getSourceKey() {
        return mSourceKey;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public String getCategoryId() {
        return mCategoryId;
    }

    @Override
    public boolean isResultFilterable() {
        return mIsFilterable;
    }

    @Override
    public FilterList getFilterList() {
        return mFilterList;
    }

    @Override
    public boolean isResultSortable() {
        return mIsSortable;
    }

    @Override
    public List<Sort> getResultSortList() {
        return mSortList;
    }

    /**
     * 帮助构造category list的构建者类
     * 当该分类的subtitle，id和筛选的subtitle，id一样时，第一次展示结果时将该筛选改为选中状态
     */
    public static final class ListBuilder extends BaseListBuilder<Category> {

        private final String mSourceKey;

        public ListBuilder(final String sourceKey) {
            mSourceKey = sourceKey;
        }

        public ListBuilder addCategory(final String categoryName, final String categoryId) {
            mList.add(new Category(mSourceKey, categoryName, categoryId, null));
            return this;
        }

        public ListBuilder addCategory(final String categoryName, final String categoryId, final @Nullable String subtitle, FilterList filterList) {
            mList.add(new Category(mSourceKey, categoryName, categoryId, null, subtitle, filterList));
            return this;
        }

        public ListBuilder addCategory(final String categoryName, final String categoryId, List<Sort> sortList) {
            mList.add(new Category(mSourceKey, categoryName, categoryId, null, sortList));
            return this;
        }

        public ListBuilder addCategory(final String categoryName, final String categoryId,
                                       final @Nullable String subtitle, FilterList filterList, List<Sort> sortList) {
            mList.add(new Category(mSourceKey, categoryName, categoryId, null, subtitle, filterList, sortList));
            return this;
        }

        public ListBuilder addCategory(final String categoryName, final String categoryId, String cover) {
            mList.add(new Category(mSourceKey, categoryName, categoryId, cover));
            return this;
        }

        public ListBuilder addCategory(final String categoryName, final String categoryId, String cover, final @Nullable String subtitle, FilterList filterList) {
            mList.add(new Category(mSourceKey, categoryName, categoryId, cover, subtitle, filterList));
            return this;
        }

        public ListBuilder addCategory(final String categoryName, final String categoryId, String cover, List<Sort> sortList) {
            mList.add(new Category(mSourceKey, categoryName, categoryId, cover, sortList));
            return this;
        }

        public ListBuilder addCategory(final String categoryName, final String categoryId, String cover, final @Nullable String subtitle,
                                       final FilterList filterList, final List<Sort> sortList) {
            mList.add(new Category(mSourceKey, categoryName, categoryId, cover, subtitle, filterList, sortList));
            return this;
        }

        public ListBuilder addCategory(Category category) {
            mList.add(category);
            return this;
        }
    }
}
