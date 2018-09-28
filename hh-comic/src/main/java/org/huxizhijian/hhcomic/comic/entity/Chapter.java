package org.huxizhijian.hhcomic.comic.entity;

/**
 * 漫画章节信息实体类
 *
 * @author huxizhijian
 * @date 2018/8/29
 */
public class Chapter {

    /**
     * 源Key
     */
    private String mSourceKey;

    /**
     * 漫画id
     */
    private String mComicId;

    /**
     * 章节id
     */
    private String mChapterId;

    /**
     * 章节分类
     */
    private String mType;

    /**
     * 章节名称
     */
    private String mChapterName;

    /**
     * 总页数
     */
    private int mPage;

    /**
     * 是否下载
     */
    private boolean mIsDownload;

    /**
     * 是否下载完毕
     */
    private boolean mIsDownloadFinish;

}
