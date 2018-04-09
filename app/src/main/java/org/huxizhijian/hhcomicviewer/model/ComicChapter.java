/*
 * Copyright 2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.hhcomicviewer.model;

import android.support.annotation.NonNull;

import org.huxizhijian.hhcomicviewer.utils.ParsePicUrlList;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 章节实体类，主要保存下载状态
 * Created by wei on 2016/8/23.
 */
@Table(name = "comic_chapter")
public class ComicChapter implements Serializable, Comparable {
    @Column(name = "id", isId = true)
    protected int id; //id
    @Column(name = "chapter_name")
    private String chapterName; //章节名
    @Column(name = "chid")
    private long chid; //章节id
    @Column(name = "server_id")
    private int serverId;
    @Column(name = "download_status")
    private int downloadStatus; //下载状态
    @Column(name = "download_position")
    private int downloadPosition; //下载完成的页数
    @Column(name = "page_count")
    private int pageCount; //章节的页数
    @Column(name = "comic_title")
    private String comicTitle; //漫画名
    @Column(name = "cid")
    private int cid; //漫画id
    @Column(name = "save_path")
    private String savePath; //保存目录

    private ArrayList<String> picList;

    public ArrayList<String> getPicList() {
        return picList;
    }

    public void setPicList(ArrayList<String> picList) {
        this.picList = picList;
    }

    public ComicChapter() {
    }

    public ComicChapter(long chid, int serverId, String content) {
        this.picList = ParsePicUrlList.scanPicInPage(serverId, content);
        this.chid = chid;
        this.pageCount = picList.size();
    }

    public ComicChapter(String comicTitle, int cid, long chid, String chapterName, int serverId) {
        this.comicTitle = comicTitle;
        this.cid = cid;
        this.chid = chid;
        this.chapterName = chapterName;
        this.serverId = serverId;
    }

    public void updatePicList(int serverId, String content) {
        this.picList = ParsePicUrlList.scanPicInPage(serverId, content);
        this.pageCount = this.picList.size();
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public int getDownloadPosition() {
        return downloadPosition;
    }

    public void setDownloadPosition(int downloadPosition) {
        this.downloadPosition = downloadPosition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }


    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getComicTitle() {
        return comicTitle;
    }

    public void setComicTitle(String comicTitle) {
        this.comicTitle = comicTitle;
    }

    public long getChid() {
        return chid;
    }

    public void setChid(long chid) {
        this.chid = chid;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    @Override
    public String toString() {
        return "ComicChapter{" +
                "id=" + id +
                ", chapterName='" + chapterName + '\'' +
                ", chid=" + chid +
                ", serverId=" + serverId +
                ", downloadStatus=" + downloadStatus +
                ", downloadPosition=" + downloadPosition +
                ", pageCount=" + pageCount +
                ", comicTitle='" + comicTitle + '\'' +
                ", cid=" + cid +
                ", savePath='" + savePath + '\'' +
                ", picList=" + picList +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        ComicChapter compareChapter = (ComicChapter) obj;
        return this.chid == compareChapter.getChid();
    }

    @Override
    public int compareTo(@NonNull Object o) {
        ComicChapter compareChapter = (ComicChapter) o;
        return this.chapterName.compareTo(compareChapter.getChapterName());
    }
}
