/*
 * Copyright 2016-2018 huxizhijian
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 保存于数据库的实体Comic类
 * will deprecated see {@link org.huxizhijian.hhcomic.oldcomic.bean.Comic}
 *
 * @author huxizhijian
 * @date 2016/8/23
 */
@Table(name = "comic")
public class Comic implements Serializable, Comparable {
    @Column(name = "id", isId = true)
    private int id; //id
    @Column(name = "cid")
    private int cid; //漫画id
    @Column(name = "title")
    private String title; //漫画名
    @Column(name = "author")
    private String author; //作者
    @Column(name = "description")
    private String description; //漫画说明
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;  //缩略图的网址
    @Column(name = "comic_status")
    private String comicStatus; //漫画连载状态
    @Column(name = "comic_update_time")
    private String comicUpdateTime; //漫画更新时间
    @Column(name = "comic_favorite")
    private String comicFavorite; //漫画收藏人数
    @Column(name = "rating_number")
    private float ratingNumber; //评分系统的分数
    @Column(name = "rating_people_num")
    private int ratingPeopleNum; //评分的人数
    @Column(name = "is_mark")
    private boolean isMark = false; //是否收藏
    @Column(name = "is_download")
    private boolean isDownload = false; //是否下载
    @Column(name = "read_chapter")
    private int readChapter = -1; //阅读的章节
    @Column(name = "read_page")
    private int readPage = -1; //阅读的页数
    @Column(name = "last_read_time")
    private long lastReadTime;  //最后一次阅读时间，用于排序
    @Column(name = "chapter_count")
    private int chapterCount; //章节总数
    @Column(name = "chapter_name_list")
    private String chapterNameList = ""; //为了更好的离线观看，将以字符串的方式记录章节名称
    @Column(name = "chapter_id_list")
    private String chapterIdList = ""; //同上，这是章节id
    @Column(name = "is_update")
    private boolean isUpdate = false; //是否有更新(章节数变化)

    // 无法保存在数据库里，如果isDownload为true将会创建数个实体类保存在download表里
    private List<String> chapterName; //章节名
    private List<Long> chapterId; //章节Id
    private int serverId; //章节服务器Id

    // 离线时解析出章节名，章节url
    public void initChapterNameAndList() {
        if (chapterNameList == null || chapterIdList == null ||
                "".equals(chapterNameList) || "".equals(chapterIdList)) {
            return;
        }
        String[] names = chapterNameList.split("@");
        String[] ids = chapterIdList.split("@");
        List<String> chapterName = new ArrayList<>();
        List<Long> chapterId = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            chapterName.add(names[i]);
            chapterId.add(Long.parseLong(ids[i]));
        }
        this.chapterName = chapterName;
        this.chapterId = chapterId;
    }

    //存储章节名
    public void saveChapterNameList() {
        if (chapterName == null) {
            return;
        }
        StringBuilder nameList = new StringBuilder();
        String splitString = "@";
        for (String splitchapterName : chapterName) {
            nameList.append(splitchapterName).append(splitString);
        }
        this.chapterNameList = nameList.toString();
    }

    //存储章节url
    public void saveChapterIdList() {
        if (chapterId == null) return;
        StringBuilder nameList = new StringBuilder();
        String splitString = "@";
        for (Long splitchapterName : chapterId) {
            nameList.append(splitchapterName).append(splitString);
        }
        this.chapterIdList = nameList.toString();
    }

    public Comic() {
    }

    public Comic(int cid, String content) {
        this.cid = cid;
        //获取到网页内容时自动完善内容
        Document doc = Jsoup.parse(content);
        Element comicInfoDiv = doc.select("div[class=product]").first();

        this.title = comicInfoDiv.getElementsByTag("h1").first().text();
        this.thumbnailUrl = comicInfoDiv.select("div[id=about_style]").first()
                .getElementsByTag("img").first().attr("src");

        Element about_kit = comicInfoDiv.select("div[id=about_kit]").first();
        Elements comicInfoList = about_kit.select("li");
        comicInfoList.remove(0);
        for (Element comicInfo : comicInfoList) {
            switch (comicInfo.getElementsByTag("b").first().text()) {
                case "作者:":
                    this.author = comicInfo.text().split(":")[1];
                    break;
                case "状态:":
                    this.comicStatus = comicInfo.text();
                    break;
                case "集数:":
                    this.comicStatus += (" " + comicInfo.text().split("\\)")[0] + ")");
                    break;
                case "更新:":
                    this.comicUpdateTime = comicInfo.text();
                    break;
                case "收藏:":
                    this.comicFavorite = comicInfo.text();
                    break;
                case "评价:":
                    this.ratingNumber = Float.valueOf(comicInfo.getElementsByTag("span").first().text());
                    this.ratingPeopleNum = Integer.valueOf(comicInfo.text().split("\\(")[1].split("人")[0]);
                    break;
                case "简介":
                    this.description = comicInfo.text();
                    break;
            }
        }

        //章节目录解析
        Element volListSrc = doc.select("div[class=cVolList]").first();
        Elements tagsSrc = volListSrc.select("div[class=cVolTag]");
        Elements tagchapterSrc = volListSrc.select("ul[class=cVolUl]");

        this.chapterName = new ArrayList<>();
        this.chapterId = new ArrayList<>();
        for (int i = 0; i < tagsSrc.size(); i++) {
//            this.tags.add(tagsSrc.get(i).text());
            Elements chaptersSrc = tagchapterSrc.get(i).select("a[class=l_s]");
//            tagCounts.add(chaptersSrc.size());
            for (int j = chaptersSrc.size() - 1; j > -1; j--) {
                //这个倒数循环把原本的倒序的章节顺序变为正序
                chapterName.add(chaptersSrc.get(j).attr("title"));
                //地址需要做一个变换，因为需要另外一个网站的网址，更好解析
                String urlSrc = chaptersSrc.get(j).attr("href");
                //图片服务器编号
                String domainNum = urlSrc.split("=")[1];
                //章节编号
                String chapterNum = urlSrc.split("/")[1].substring(4);
                chapterId.add(Long.parseLong(chapterNum));
                if (i == 0) {
                    serverId = Integer.parseInt(domainNum);
                }
            }
        }
        this.chapterCount = this.chapterName.size();
    }

    public String getChapterIdList() {
        return chapterIdList;
    }

    public void setChapterIdList(String chapterIdList) {
        this.chapterIdList = chapterIdList;
    }

    public boolean checkUpdate(String content) {
        //查看是否有更新;
        //获取到网页内容时自动完善内容
        Document doc = Jsoup.parse(content);
        Element comicInfoDiv = doc.select("div[id=permalink]").first();

        this.title = comicInfoDiv.getElementsByTag("h1").first().text();

        Element about_kit = comicInfoDiv.select("div[id=about_kit]").first();
        Elements comicInfoList = about_kit.select("li");
        comicInfoList.remove(0);
        for (Element comicInfo : comicInfoList) {
            switch (comicInfo.getElementsByTag("b").first().text()) {
                case "作者:":
                    this.author = comicInfo.text().split(":")[1];
                    break;
                case "状态:":
                    this.comicStatus = comicInfo.text();
                    break;
                case "集数:":
                    this.comicStatus += (" " + comicInfo.text().split("\\)")[0] + ")");
                    break;
                case "更新:":
                    this.comicUpdateTime = comicInfo.text();
                    break;
                case "收藏:":
                    this.comicFavorite = comicInfo.text();
                    break;
                case "评价:":
                    this.ratingNumber = Float.valueOf(comicInfo.getElementsByTag("span").first().text());
                    this.ratingPeopleNum = Integer.valueOf(comicInfo.text().split("\\(")[1].split("人")[0]);
                    break;
                case "简介":
                    this.description = comicInfo.text();
                    break;
            }
        }

        //章节目录解析
        Element volListSrc = doc.select("div[class=cVolList]").first();
        Elements tagsSrc = volListSrc.select("div[class=cVolTag]");
        Elements tagchapterSrc = volListSrc.select("ul[class=cVolUl]");

        this.chapterName = new ArrayList<>();
        this.chapterId = new ArrayList<>();
        for (int i = 0; i < tagsSrc.size(); i++) {
//            this.tags.add(tagsSrc.get(i).text());
            Elements chaptersSrc = tagchapterSrc.get(i).select("a[class=l_s]");
//            tagCounts.add(chaptersSrc.size());
            for (int j = chaptersSrc.size() - 1; j > -1; j--) {
                //这个倒数循环把原本的倒序的章节顺序变为正序
                chapterName.add(chaptersSrc.get(j).attr("title"));
                //地址需要做一个变换，因为需要另外一个网站的网址，更好解析
                String urlSrc = chaptersSrc.get(j).attr("href");
                //图片服务器编号
                String domainNum = urlSrc.split("=")[1];
                //章节编号
                String chapterNum = urlSrc.split("/")[1].substring(4);
                chapterId.add(Long.parseLong(chapterNum));
                if (i == 0) {
                    serverId = Integer.parseInt(domainNum);
                }
            }
        }
        if (this.chapterCount != this.chapterName.size()) {
            this.isUpdate = true;
        }
        this.chapterCount = this.chapterName.size();
        return isUpdate;
    }

    public List<Long> getChapterId() {
        return chapterId;
    }

    public void setChapterId(List<Long> chapterId) {
        this.chapterId = chapterId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getComicStatus() {
        return comicStatus;
    }

    public void setComicStatus(String comicStatus) {
        this.comicStatus = comicStatus;
    }

    public String getComicUpdateTime() {
        return comicUpdateTime;
    }

    public void setComicUpdateTime(String comicUpdateTime) {
        this.comicUpdateTime = comicUpdateTime;
    }

    public String getComicFavorite() {
        return comicFavorite;
    }

    public void setComicFavorite(String comicFavorite) {
        this.comicFavorite = comicFavorite;
    }

    public float getRatingNumber() {
        return ratingNumber;
    }

    public void setRatingNumber(float ratingNumber) {
        this.ratingNumber = ratingNumber;
    }

    public int getRatingPeopleNum() {
        return ratingPeopleNum;
    }

    public void setRatingPeopleNum(int ratingPeopleNum) {
        this.ratingPeopleNum = ratingPeopleNum;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public String getChapterNameList() {
        return chapterNameList;
    }

    public void setChapterNameList(String chapterNameList) {
        this.chapterNameList = chapterNameList;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isMark() {
        return isMark;
    }

    public void setMark(boolean mark) {
        isMark = mark;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public List<String> getChapterName() {
        return chapterName;
    }

    public void setChapterName(List<String> chapterName) {
        this.chapterName = chapterName;
    }

    public int getReadChapter() {
        return readChapter;
    }

    public void setReadChapter(int readChapter) {
        this.readChapter = readChapter;
    }

    public int getReadPage() {
        return readPage;
    }

    public void setReadPage(int readPage) {
        this.readPage = readPage;
    }

    public long getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(long lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    @Override
    public String toString() {
        return "Comic{" +
                "id=" + id +
                ", cid='" + cid + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", isMark=" + isMark +
                ", isDownload=" + isDownload +
                ", readChapter=" + readChapter +
                ", readPage=" + readPage +
                ", lastReadTime=" + lastReadTime +
                ", chapterCount=" + chapterCount +
                ", thumbnail_url=" + thumbnailUrl +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        Comic comicOther = (Comic) obj;
        return this.cid == comicOther.getCid();
    }

    @Override
    public int compareTo(Object o) {
        Comic comicOther = (Comic) o;
        if (this.cid == comicOther.getCid()) {
            //同一个comic
            return 0;
        }
        if (this.getLastReadTime() > comicOther.getLastReadTime()) {
            return 1;
        } else {
            return 0;
        }
    }
}
