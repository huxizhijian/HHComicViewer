package org.huxizhijian.hhcomic.comic.entity;

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

}
