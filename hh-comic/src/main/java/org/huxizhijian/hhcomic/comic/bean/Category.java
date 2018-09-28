package org.huxizhijian.hhcomic.comic.bean;

/**
 * 漫画分类信息实体类
 *
 * @author huxizhijian
 * @date 2018/8/30
 */
public class Category {

    /**
     * 漫画源
     */
    private String mSourceKey;

    /**
     * 分类名称
     */
    private String mCategoryName;

    /**
     * 分类id
     */
    private String mCategoryId;

    public Category() {
    }

    public Category(String sourceKey, String categoryName, String categoryId) {
        mSourceKey = sourceKey;
        mCategoryName = categoryName;
        mCategoryId = categoryId;
    }

    public String getSourceKey() {
        return mSourceKey;
    }

    public void setSourceKey(String sourceKey) {
        mSourceKey = sourceKey;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public void setCategoryName(String categoryName) {
        mCategoryName = categoryName;
    }

    public String getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(String categoryId) {
        mCategoryId = categoryId;
    }
}
