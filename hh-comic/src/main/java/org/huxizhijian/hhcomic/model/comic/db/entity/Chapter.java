/*
 * Copyright 2016-2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.hhcomic.model.comic.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * 漫画章节信息实体类
 *
 * @author huxizhijian
 * @date 2018/8/29
 */
@Entity(tableName = "download_chapters",
        indices = {@Index(value = {"source_key", "comic_id", "chapter_id"}, unique = true)})
public class Chapter {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int mId;

    /**
     * 源Key
     */
    @ColumnInfo(name = "source_key")
    private String mSourceKey;

    /**
     * 漫画id
     */
    @ColumnInfo(name = "comic_id")
    private String mComicId;

    /**
     * 章节id
     */
    @ColumnInfo(name = "chapter_id")
    private String mChapterId;

    /**
     * 章节分类
     */
    @ColumnInfo(name = "type")
    private String mType;

    /**
     * 章节名称
     */
    @ColumnInfo(name = "chapter_name")
    private String mChapterName;

    /**
     * 总页数
     */
    @ColumnInfo(name = "page")
    private int mPage;

    /**
     * 是否下载
     */
    @ColumnInfo(name = "is_download")
    private boolean mIsDownload;

    /**
     * 是否下载完毕
     */
    @ColumnInfo(name = "is_download_finish")
    private boolean mIsDownloadFinish;

    /**
     * 下载地址
     */
    @ColumnInfo(name = "download_path")
    private String mDownloadPath;

    public Chapter() {
    }

    public Chapter(String sourceKey, String comicId, String chapterId, String type, String chapterName, int page) {
        mSourceKey = sourceKey;
        mComicId = comicId;
        mChapterId = chapterId;
        mType = type;
        mChapterName = chapterName;
        mPage = page;
    }

    public Chapter(String sourceKey, String comicId, String chapterId, String type, String chapterName,
                   int page, boolean isDownload, boolean isDownloadFinish, String downloadPath) {
        mSourceKey = sourceKey;
        mComicId = comicId;
        mChapterId = chapterId;
        mType = type;
        mChapterName = chapterName;
        mPage = page;
        mIsDownload = isDownload;
        mIsDownloadFinish = isDownloadFinish;
        mDownloadPath = downloadPath;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
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

    public String getDownloadPath() {
        return mDownloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        mDownloadPath = downloadPath;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "mId=" + mId +
                ", mSourceKey='" + mSourceKey + '\'' +
                ", mComicId='" + mComicId + '\'' +
                ", mChapterId='" + mChapterId + '\'' +
                ", mType='" + mType + '\'' +
                ", mChapterName='" + mChapterName + '\'' +
                ", mPage=" + mPage +
                ", mIsDownload=" + mIsDownload +
                ", mIsDownloadFinish=" + mIsDownloadFinish +
                ", mDownloadPath='" + mDownloadPath + '\'' +
                '}';
    }
}
