package org.huxizhijian.hhcomic.db.entity;

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

    public Chapter() {
    }

    public Chapter(String sourceKey, String comicId, String chapterId,
                   String type, String chapterName, int page, boolean isDownload, boolean isDownloadFinish) {
        mSourceKey = sourceKey;
        mComicId = comicId;
        mChapterId = chapterId;
        mType = type;
        mChapterName = chapterName;
        mPage = page;
        mIsDownload = isDownload;
        mIsDownloadFinish = isDownloadFinish;
    }

    public String getSourceKey() {
        return mSourceKey;
    }

    public void setSourceKey(String sourceKey) {
        mSourceKey = sourceKey;
    }

    public String getComicId() {
        return mComicId;
    }

    public void setComicId(String comicId) {
        mComicId = comicId;
    }

    public String getChapterId() {
        return mChapterId;
    }

    public void setChapterId(String chapterId) {
        mChapterId = chapterId;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getChapterName() {
        return mChapterName;
    }

    public void setChapterName(String chapterName) {
        mChapterName = chapterName;
    }

    public int getPage() {
        return mPage;
    }

    public void setPage(int page) {
        mPage = page;
    }

    public boolean isDownload() {
        return mIsDownload;
    }

    public void setDownload(boolean download) {
        mIsDownload = download;
    }

    public boolean isDownloadFinish() {
        return mIsDownloadFinish;
    }

    public void setDownloadFinish(boolean downloadFinish) {
        mIsDownloadFinish = downloadFinish;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "mSourceKey='" + mSourceKey + '\'' +
                ", mComicId='" + mComicId + '\'' +
                ", mChapterId='" + mChapterId + '\'' +
                ", mType='" + mType + '\'' +
                ", mChapterName='" + mChapterName + '\'' +
                ", mPage=" + mPage +
                ", mIsDownload=" + mIsDownload +
                ", mIsDownloadFinish=" + mIsDownloadFinish +
                '}';
    }
}
