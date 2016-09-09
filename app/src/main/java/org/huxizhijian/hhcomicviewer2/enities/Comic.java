package org.huxizhijian.hhcomicviewer2.enities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.ArrayList;

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
    private String captureNameList; //为了更好的离线观看，将以字符串的方式记录章节名称
    @Column(name = "is_update")
    private boolean isUpdate = false; //是否有更新(章节数变化)

    //无法保存在数据库里，如果isDownload为true将会创建数个实体类保存在download表里
    private String[] captureName; //章节名
    private String[] captureUrl; //章节url
    private ArrayList<ComicCapture> comicCaptures; //如果下载，则初始化该字段

    //离线时解析出章节名
    public String[] getCaptureNameInString() {
        return captureNameList.split(":");
    }

    //存储章节名
    public void saveCaptureNameList() {
        if (captureName == null) return;
        StringBuilder nameList = new StringBuilder();
        String splitString = ":";
        for (String splitCaptureName : captureName) {
            nameList.append(splitCaptureName).append(splitString);
        }
        this.captureNameList = nameList.toString();
    }

    public ArrayList<ComicCapture> initDownloadList() {
        this.comicCaptures = new ArrayList<>();
        ComicCapture capture = null;
        for (int i = 0; i < captureName.length; i++) {
            capture = new ComicCapture(title, captureName[i], captureUrl[i]);
            this.comicCaptures.add(capture);
        }
        return this.comicCaptures;
    }

    public Comic() {
    }

    public Comic(String comicUrl, String content) {
        this.comicUrl = comicUrl;
        //获取到网页内容时自动完善内容
        Document doc = Jsoup.parse(content);
        this.title = doc.title().split(",")[0];
        Element meta = doc.select("meta[name=Keywords]").first();
        String authorSrc = meta.attr("content").split(",")[2];
//        this.author = authorSrc.substring(0, authorSrc.length() - (title.length() + 5)).split("：")[1];
        this.author = authorSrc.split(" ")[0].split("：")[1];
        Element src = doc.select("div[class=2replh]").first();
        String src1 = src.text();
        String src2 = src1.split(",")[1];
        this.description = src2.substring(title.length() + 3, src2.length() - 4);
        Element replcSrc = doc.select("div[class=replc]").first();
        Element imgSrc = replcSrc.getElementsByTag("img").first();
        this.thumbnailUrl = imgSrc.attr("src");
        Element volSrc = doc.select("div[class=vol]").first();
        Elements vols = volSrc.select("a[target=_blank]");
        this.captureName = new String[vols.size()];
        this.captureUrl = new String[vols.size()];
        for (int i = 0; i < vols.size(); i++) {
            this.captureName[i] = vols.get(vols.size() - i - 1).text();
            this.captureUrl[i] = vols.get(vols.size() - i - 1).attr("href");
        }
        this.captureCount = captureName.length;
    }

    public boolean checkUpdate(String content) {
        //查看是否有更新
        Document doc = Jsoup.parse(content);
        Element volSrc = doc.select("div[class=vol]").first();
        Elements vols = volSrc.select("a[target=_blank]");
        this.captureName = new String[vols.size()];
        this.captureUrl = new String[vols.size()];
        for (int i = 0; i < vols.size(); i++) {
            this.captureName[i] = vols.get(vols.size() - i - 1).text();
            this.captureUrl[i] = vols.get(vols.size() - i - 1).attr("href");
        }
        if (this.captureCount != this.captureName.length) {
            this.isUpdate = true;
        }
        this.captureCount = this.captureName.length;
        return isUpdate;
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


    public ArrayList<ComicCapture> getComicCaptures() {
        return comicCaptures;
    }

    public void setComicCaptures(ArrayList<ComicCapture> comicCaptures) {
        this.comicCaptures = comicCaptures;
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

    public String[] getCaptureName() {
        return captureName;
    }

    public void setCaptureName(String[] captureName) {
        this.captureName = captureName;
    }

    public String[] getCaptureUrl() {
        return captureUrl;
    }

    public void setCaptureUrl(String[] captureUrl) {
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
    public int compareTo(Object o) {
        Comic comicOther = (Comic) o;
        if (this.getLastReadTime() > comicOther.getLastReadTime()) {
            return 1;
        } else {
            return 0;
        }
    }
}
