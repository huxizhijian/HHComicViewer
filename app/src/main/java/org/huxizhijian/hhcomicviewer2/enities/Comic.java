package org.huxizhijian.hhcomicviewer2.enities;

import org.huxizhijian.hhcomicviewer2.utils.Constants;
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
 * Created by wei on 2016/8/23.
 */
@Table(name = "comic")
public class Comic implements Serializable, Comparable {
    @Column(name = "id", isId = true)
    private int id; //id
    @Column(name = "comic_url")
    private String comicUrl; //漫画url
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
    @Column(name = "read_capture")
    private int readCapture = 0; //阅读的章节
    @Column(name = "read_page")
    private int readPage = 0; //阅读的页数
    @Column(name = "last_read_time")
    private long lastReadTime;  //最后一次阅读时间，用于排序
    @Column(name = "capture_count")
    private int captureCount; //章节总数
    @Column(name = "capture_name_list")
    private String captureNameList = ""; //为了更好的离线观看，将以字符串的方式记录章节名称
    @Column(name = "capture_url_list")
    private String captureUrlList = ""; //同上，这是章节url
    @Column(name = "is_update")
    private boolean isUpdate = false; //是否有更新(章节数变化)

    //无法保存在数据库里，如果isDownload为true将会创建数个实体类保存在download表里
    private List<String> captureName; //章节名
    private List<String> captureUrl; //章节url

    //离线时解析出章节名，章节url
    public void initCaptureNameAndList() {
        if (captureNameList == null || captureUrlList == null ||
                captureNameList.equals("") || captureUrlList.equals("")) return;
        String[] names = captureNameList.split("@");
        String[] urls = captureUrlList.split("@");
        List<String> captureName = new ArrayList<>();
        List<String> captureUrl = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            captureName.add(names[i]);
            captureUrl.add(urls[i]);
        }
        this.captureName = captureName;
        this.captureUrl = captureUrl;
    }

    //存储章节名
    public void saveCaptureNameList() {
        if (captureName == null) return;
        StringBuilder nameList = new StringBuilder();
        String splitString = "@";
        for (String splitCaptureName : captureName) {
            nameList.append(splitCaptureName).append(splitString);
        }
        this.captureNameList = nameList.toString();
    }

    //存储章节url
    public void saveCaptureUrlList() {
        if (captureUrl == null) return;
        StringBuilder nameList = new StringBuilder();
        String splitString = "@";
        for (String splitCaptureName : captureUrl) {
            nameList.append(splitCaptureName).append(splitString);
        }
        this.captureUrlList = nameList.toString();
    }

    public Comic() {
    }

    public Comic(String comicUrl, String content) {
        this.comicUrl = comicUrl;
        //解析出comic编号
        String[] comicSplits = comicUrl.split("/");
        String comicNum = comicSplits[comicSplits.length - 1].split("\\.")[0];
        //获取到网页内容时自动完善内容
        Document doc = Jsoup.parse(content);
        Element comicInfoDiv = doc.select("div[id=permalink]").first();

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
                    this.comicStatus += (" " + comicInfo.text().split("\\)")[0] + "\\)");
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
        Elements tagCaptureSrc = volListSrc.select("ul[class=cVolUl]");
        /*this.tags = new ArrayList<>();
        this.tagCounts = new ArrayList<>();
        this.tagCount = tagsSrc.size();*/
        this.captureName = new ArrayList<>();
        this.captureUrl = new ArrayList<>();
        for (int i = 0; i < tagsSrc.size(); i++) {
//            this.tags.add(tagsSrc.get(i).text());
            Elements capturesSrc = tagCaptureSrc.get(i).select("a[class=l_s]");
//            tagCounts.add(capturesSrc.size());
            for (int j = capturesSrc.size() - 1; j > -1; j--) {
                //这个倒数循环把原本的倒序的章节顺序变为正序
                captureName.add(capturesSrc.get(j).attr("title"));
                //地址需要做一个变换，因为需要另外一个网站的网址，更好解析
                String urlSrc = capturesSrc.get(j).attr("href");
                //图片服务器编号
                String domainNum = urlSrc.split("=")[1];
                //章节编号
                String captureNum = urlSrc.split("/")[1].substring(4);
                captureUrl.add(Constants.COMIC_VOL_PAGE + comicNum + "/" + captureNum + ".htm?s=" + domainNum);
            }
        }
        this.captureCount = this.captureName.size();
    }

    public String getCaptureUrlList() {
        return captureUrlList;
    }

    public void setCaptureUrlList(String captureUrlList) {
        this.captureUrlList = captureUrlList;
    }

    public boolean checkUpdate(String content) {
        //查看是否有更新;
        //解析出comic编号
        String[] comicSplits = comicUrl.split("/");
        String comicNum = comicSplits[comicSplits.length - 1].split("\\.")[0];
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
                    this.comicStatus += (" " + comicInfo.text().split("\\)")[0]);
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
        Elements tagCaptureSrc = volListSrc.select("ul[class=cVolUl]");

        this.captureName = new ArrayList<>();
        this.captureUrl = new ArrayList<>();
        for (int i = 0; i < tagsSrc.size(); i++) {
//            this.tags.add(tagsSrc.get(i).text());
            Elements capturesSrc = tagCaptureSrc.get(i).select("a[class=l_s]");
//            tagCounts.add(capturesSrc.size());
            for (int j = capturesSrc.size() - 1; j > -1; j--) {
                //这个倒数循环把原本的倒序的章节顺序变为正序
                captureName.add(capturesSrc.get(j).attr("title"));
                //地址需要做一个变换，因为需要另外一个网站的网址，更好解析
                String urlSrc = capturesSrc.get(j).attr("href");
                //图片服务器编号
                String domainNum = urlSrc.split("=")[1];
                //章节编号
                String captureNum = urlSrc.split("/")[1].substring(4);
                captureUrl.add(Constants.COMIC_VOL_PAGE + comicNum + "/" + captureNum + ".htm?s=" + domainNum);
            }
        }
        if (this.captureCount != this.captureName.size()) {
            this.isUpdate = true;
        }
        this.captureCount = this.captureName.size();
        return isUpdate;
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

    public String getCaptureNameList() {
        return captureNameList;
    }

    public void setCaptureNameList(String captureNameList) {
        this.captureNameList = captureNameList;
    }

    public int getCaptureCount() {
        return captureCount;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setCaptureCount(int captureCount) {
        this.captureCount = captureCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComicUrl() {
        return comicUrl;
    }

    public void setComicUrl(String comicUrl) {
        this.comicUrl = comicUrl;
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

    public List<String> getCaptureName() {
        return captureName;
    }

    public void setCaptureName(List<String> captureName) {
        this.captureName = captureName;
    }

    public List<String> getCaptureUrl() {
        return captureUrl;
    }

    public void setCaptureUrl(List<String> captureUrl) {
        this.captureUrl = captureUrl;
    }

    public int getReadCapture() {
        return readCapture;
    }

    public void setReadCapture(int readCapture) {
        this.readCapture = readCapture;
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
                ", comicUrl='" + comicUrl + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", isMark=" + isMark +
                ", isDownload=" + isDownload +
                ", readCapture=" + readCapture +
                ", readPage=" + readPage +
                ", lastReadTime=" + lastReadTime +
                ", captureCount=" + captureCount +
                ", thumbnail_url=" + thumbnailUrl +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        Comic comicOther = (Comic) obj;
        return this.comicUrl.equals(comicOther.getComicUrl());
    }

    @Override
    public int compareTo(Object o) {
        Comic comicOther = (Comic) o;
        if (this.comicUrl.equals(comicOther.getComicUrl())) {
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
