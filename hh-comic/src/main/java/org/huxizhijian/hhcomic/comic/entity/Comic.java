package org.huxizhijian.hhcomic.comic.entity;

import java.util.List;

/**
 * 漫画信息实体类
 *
 * @author huxizhijian
 * @date 2018/8/29
 */
public class Comic {

    /**
     * 漫画id
     */
    private String mComicId;

    /**
     * 源信息
     */
    private String mSourceKey;

    /**
     * 标题
     */
    private String mTitle;

    /**
     * 作者
     */
    private String mAuthor;

    /**
     * 封面图片地址
     */
    private String mCover;

    /**
     * 简介
     */
    private String mDescription;

    /**
     * 漫画最后更新时间
     */
    private String mComicLastUpdateTime;

    /**
     * 连载状态是否为已完结
     */
    private boolean mIsEnd;

    /**
     * 额外信息，用于获取章节首页时额外需要的信息
     */
    private String mExtra;

    /**
     * 最后一次观看漫画的时间
     */
    private long mLastTime;

    /**
     * 最后一次观看的章节
     */
    private String mLastReadChapter;

    /**
     * 最后一次观看的章节页码
     */
    private int mLastReadPage;

    /**
     * 是否存在下载的章节
     */
    private boolean mHasDownloadChapter;

    /**
     * 是否在收藏中
     */
    private boolean mIsInFavorite;

    /**
     * 章节列表
     */
    private List<Chapter> mChapterList;

    public Comic() {
    }

    public Comic(String comicId, String sourceKey, String title, String author, String cover, String description,
                 String comicLastUpdateTime, boolean isEnd, String extra, long lastTime, String lastReadChapter,
                 int lastReadPage, boolean hasDownloadChapter, boolean isInFavorite, List<Chapter> chapterList) {
        mComicId = comicId;
        mSourceKey = sourceKey;
        mTitle = title;
        mAuthor = author;
        mCover = cover;
        mDescription = description;
        mComicLastUpdateTime = comicLastUpdateTime;
        mIsEnd = isEnd;
        mExtra = extra;
        mLastTime = lastTime;
        mLastReadChapter = lastReadChapter;
        mLastReadPage = lastReadPage;
        mHasDownloadChapter = hasDownloadChapter;
        mIsInFavorite = isInFavorite;
        mChapterList = chapterList;
    }

    public String getComicId() {
        return mComicId;
    }

    public void setComicId(String comicId) {
        mComicId = comicId;
    }

    public String getSourceKey() {
        return mSourceKey;
    }

    public void setSourceKey(String sourceKey) {
        mSourceKey = sourceKey;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getCover() {
        return mCover;
    }

    public void setCover(String cover) {
        mCover = cover;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getComicLastUpdateTime() {
        return mComicLastUpdateTime;
    }

    public void setComicLastUpdateTime(String comicLastUpdateTime) {
        mComicLastUpdateTime = comicLastUpdateTime;
    }

    public boolean isEnd() {
        return mIsEnd;
    }

    public void setEnd(boolean end) {
        mIsEnd = end;
    }

    public long getLastTime() {
        return mLastTime;
    }

    public void setLastTime(long lastTime) {
        mLastTime = lastTime;
    }

    public String getLastReadChapter() {
        return mLastReadChapter;
    }

    public void setLastReadChapter(String lastReadChapter) {
        mLastReadChapter = lastReadChapter;
    }

    public int getLastReadPage() {
        return mLastReadPage;
    }

    public void setLastReadPage(int lastReadPage) {
        mLastReadPage = lastReadPage;
    }

    public boolean isHasDownloadChapter() {
        return mHasDownloadChapter;
    }

    public void setHasDownloadChapter(boolean hasDownloadChapter) {
        mHasDownloadChapter = hasDownloadChapter;
    }

    public boolean isInFavorite() {
        return mIsInFavorite;
    }

    public void setInFavorite(boolean inFavorite) {
        mIsInFavorite = inFavorite;
    }

    public String getExtra() {
        return mExtra;
    }

    public void setExtra(String extra) {
        mExtra = extra;
    }

    public List<Chapter> getChapterList() {
        return mChapterList;
    }

    public void setChapterList(List<Chapter> chapterList) {
        mChapterList = chapterList;
    }

    @Override
    public String toString() {
        return "Comic{" +
                "mComicId='" + mComicId + '\'' +
                ", mSourceKey='" + mSourceKey + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mAuthor='" + mAuthor + '\'' +
                ", mCover='" + mCover + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mComicLastUpdateTime='" + mComicLastUpdateTime + '\'' +
                ", mIsEnd=" + mIsEnd +
                ", mExtra='" + mExtra + '\'' +
                ", mLastTime=" + mLastTime +
                ", mLastReadChapter='" + mLastReadChapter + '\'' +
                ", mLastReadPage=" + mLastReadPage +
                ", mHasDownloadChapter=" + mHasDownloadChapter +
                ", mIsInFavorite=" + mIsInFavorite +
                ", mChapterList=" + mChapterList +
                '}';
    }
}
