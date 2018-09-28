package org.huxizhijian.hhcomic.comic.bean;

/**
 * 推荐、排行信息实体类
 *
 * @author huxizhijian
 * @date 2018/8/31
 */
public class ComicRank {

    /**
     * 源key
     */
    private String mSourceKey;

    /**
     * 推荐、排行的名称
     */
    private String mListName;

    /**
     * 漫画推荐或者排行的id
     */
    private String mListId;

    /**
     * 是 - 推荐
     * 否 - 排行
     */
    private boolean mIsRecommended;

    public ComicRank() {
    }

    public ComicRank(String sourceKey, String listName, String listId, boolean isRecommended) {
        mSourceKey = sourceKey;
        mListName = listName;
        mListId = listId;
        mIsRecommended = isRecommended;
    }

    public String getSourceKey() {
        return mSourceKey;
    }

    public void setSourceKey(String sourceKey) {
        mSourceKey = sourceKey;
    }

    public String getListName() {
        return mListName;
    }

    public void setListName(String listName) {
        mListName = listName;
    }

    public String getListId() {
        return mListId;
    }

    public void setListId(String listId) {
        mListId = listId;
    }

    public boolean isRecommended() {
        return mIsRecommended;
    }

    public void setRecommended(boolean recommended) {
        mIsRecommended = recommended;
    }
}
