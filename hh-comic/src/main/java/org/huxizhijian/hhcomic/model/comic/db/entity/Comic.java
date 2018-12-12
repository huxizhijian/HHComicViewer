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

import org.huxizhijian.hhcomic.model.comic.db.entity.convert.MapJSONConvert;

import java.util.List;
import java.util.Map;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

/**
 * 漫画信息实体类
 *
 * @author huxizhijian
 * @date 2018/8/29
 */
@Entity(tableName = "comics", indices = {@Index(value = {"source_key", "comic_id"}, unique = true)})
@TypeConverters(MapJSONConvert.class)
public class Comic {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int mId;

    /**
     * 漫画id
     */
    @ColumnInfo(name = "comic_id")
    private String mComicId;

    /**
     * 源信息
     */
    @ColumnInfo(name = "source_key")
    private String mSourceKey;

    /**
     * 标题
     */
    @ColumnInfo(name = "title")
    private String mTitle;

    /**
     * 作者
     */
    @ColumnInfo(name = "author")
    private String mAuthor;

    /**
     * 封面图片地址
     */
    @ColumnInfo(name = "cover")
    private String mCover;

    /**
     * 简介
     */
    @ColumnInfo(name = "description")
    private String mDescription;

    /**
     * 漫画最后更新时间
     */
    @ColumnInfo(name = "comic_last_update_time")
    private String mComicLastUpdateTime;

    /**
     * 连载状态是否为已完结
     */
    @ColumnInfo(name = "is_end")
    private boolean mIsEnd;

    /**
     * 额外信息，用于获取章节首页时额外需要的信息
     */
    @ColumnInfo(name = "extra")
    private String mExtra;

    /**
     * 最后一次观看漫画的时间
     */
    @ColumnInfo(name = "last_time")
    private long mLastTime;

    /**
     * 最后一次观看的章节
     */
    @ColumnInfo(name = "last_read_chapter")
    private String mLastReadChapter;

    /**
     * 最后一次观看的章节页码
     */
    @ColumnInfo(name = "last_read_page")
    private int mLastReadPage;

    /**
     * 是否存在下载的章节
     */
    @ColumnInfo(name = "has_download_chapter")
    private boolean mHasDownloadChapter;

    /**
     * 最后一次启动下载任务时间
     */
    @ColumnInfo(name = "last_download_time")
    private long mLastDownloadTime;

    /**
     * 是否在收藏中
     */
    @ColumnInfo(name = "is_favorite")
    private boolean mIsFavorite;

    /**
     * 章节列表，保存时通过type convert转化成json
     */
    @ColumnInfo(name = "chapter_json")
    private Map<String, List<Chapter>> mChapterMap;

    public Comic() {
    }

    public Comic(int id, String comicId, String sourceKey, String title, String author, String cover, String description,
                 String comicLastUpdateTime, boolean isEnd, String extra, long lastTime, String lastReadChapter, int lastReadPage,
                 boolean hasDownloadChapter, long lastDownloadTime, boolean isFavorite, Map<String, List<Chapter>> chapterMap) {
        mId = id;
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
        mLastDownloadTime = lastDownloadTime;
        mIsFavorite = isFavorite;
        mChapterMap = chapterMap;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
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

    public boolean isFavorite() {
        return mIsFavorite;
    }

    public void setFavorite(boolean favorite) {
        mIsFavorite = favorite;
    }

    public String getExtra() {
        return mExtra;
    }

    public void setExtra(String extra) {
        mExtra = extra;
    }

    public long getLastDownloadTime() {
        return mLastDownloadTime;
    }

    public void setLastDownloadTime(long lastDownloadTime) {
        mLastDownloadTime = lastDownloadTime;
    }

    public Map<String, List<Chapter>> getChapterMap() {
        return mChapterMap;
    }

    public void setChapterMap(Map<String, List<Chapter>> chapterMap) {
        mChapterMap = chapterMap;
    }

    @Override
    public String toString() {
        return "Comic{" +
                "mId=" + mId +
                ", mComicId='" + mComicId + '\'' +
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
                ", mLastDownloadTime=" + mLastDownloadTime +
                ", mIsFavorite=" + mIsFavorite +
                ", mChapterMap=" + mChapterMap +
                '}';
    }
}
