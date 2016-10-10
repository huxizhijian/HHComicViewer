/*
 * Copyright 2016 huxizhijian
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

package org.huxizhijian.hhcomicviewer2.enities;

import android.support.annotation.NonNull;

import org.huxizhijian.hhcomicviewer2.utils.ParsePicUrlList;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 章节实体类，主要保存下载状态
 * Created by wei on 2016/8/23.
 */
@Table(name = "comic_capture")
public class ComicCapture implements Serializable, Comparable {
    @Column(name = "id", isId = true)
    private int id; //id
    @Column(name = "capture_name")
    private String captureName; //章节名
    @Column(name = "capture_url")
    private String captureUrl; //章节的url
    @Column(name = "download_status")
    private int downloadStatus; //下载状态
    @Column(name = "download_position")
    private int downloadPosition; //下载完成的页数
    @Column(name = "page_count")
    private int pageCount; //章节的页数
    @Column(name = "comic_title")
    private String comicTitle; //漫画名
    @Column(name = "comic_url")
    private String comicUrl; //漫画URL
    @Column(name = "save_path")
    private String savePath; //保存目录

    private ArrayList<String> picList;

    public ArrayList<String> getPicList() {
        return picList;
    }

    public void setPicList(ArrayList<String> picList) {
        this.picList = picList;
    }

    public ComicCapture() {
    }

    public ComicCapture(String url, String content) {
        this.picList = ParsePicUrlList.scanPicInPage(url, content);
        this.captureUrl = url;
        this.pageCount = picList.size();
    }

    public ComicCapture(String comicTitle, String captureName, String captureUrl, String comicUrl) {
        this.comicTitle = comicTitle;
        this.captureName = captureName;
        this.captureUrl = captureUrl;
        this.comicUrl = comicUrl;
        this.downloadPosition = 0;
    }

    public void updatePicList(String url, String content) {
        this.picList = ParsePicUrlList.scanPicInPage(url, content);
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

    public String getComicUrl() {
        return comicUrl;
    }

    public void setComicUrl(String comicUrl) {
        this.comicUrl = comicUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCaptureName() {
        return captureName;
    }

    public void setCaptureName(String captureName) {
        this.captureName = captureName;
    }

    public String getCaptureUrl() {
        return captureUrl;
    }

    public void setCaptureUrl(String captureUrl) {
        this.captureUrl = captureUrl;
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

    @Override
    public String toString() {
        return "ComicCapture{" +
                "id=" + id +
                ", captureName='" + captureName + '\'' +
                ", captureUrl='" + captureUrl + '\'' +
                ", downloadStatus=" + downloadStatus +
                ", pageCount=" + pageCount +
                ", comicTitle='" + comicTitle + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        ComicCapture compareCapture = (ComicCapture) obj;
        return this.captureUrl.equals(compareCapture.getCaptureUrl());
    }

    @Override
    public int compareTo(@NonNull Object o) {
        ComicCapture compareCapture = (ComicCapture) o;
        return this.captureName.compareTo(compareCapture.getCaptureName());
    }
}