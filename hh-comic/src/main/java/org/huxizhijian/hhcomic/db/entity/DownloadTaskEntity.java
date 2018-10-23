package org.huxizhijian.hhcomic.db.entity;

/**
 * 章节下载任务信息实体类
 *
 * @author huxizhijian
 * @date 2018/8/31
 */
public class DownloadTaskEntity {

    /**
     * 源Key
     */
    private String mSourceKey;

    /**
     * 漫画id
     */
    private String mComicId;

    /**
     * 下载的章节id
     */
    private String mChapterId;

    /**
     * 是否下载完毕
     */
    private boolean mIsFinished;

    /**
     * 下载到第几个图片
     */
    private int mDownloadingPosition = -1;

    /**
     * 正在下载的图片文件长度
     */
    private int length = -1;

    /**
     * 已经下载的文件大小
     */
    private int finished = -1;

}
