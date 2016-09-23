package org.huxizhijian.hhcomicviewer2.enities;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 线程实体类
 * Created by wei on 2016/9/5.
 */
@Table(name = "thread_info")
public class ThreadInfo {
    @Column(name = "id", isId = true)
    private int id; //id
    @Column(name = "thread_position")
    private int threadPosition; //该线程为第几个线程
    @Column(name = "thread_count")
    private int threadCount; //线程总数
    @Column(name = "download_position")
    private int downloadPosition; //该线程下载到第几个图片
    @Column(name = "length")
    private int length = -1; //正在下载的图片文件长度
    @Column(name = "finished")
    private int finished = -1; //下载完成的文件长度
    @Column(name = "comic_capture_url")
    private String ComicCaptureUrl; //漫画章节url，可以找出同一个漫画章节的所有线程

    public ThreadInfo() {
    }

    public ThreadInfo(int threadPosition, int threadCount, int downloadPosition, int length, int finished, String comicCaptureUrl) {
        this.threadPosition = threadPosition;
        this.threadCount = threadCount;
        this.downloadPosition = downloadPosition;
        this.length = length;
        this.finished = finished;
        ComicCaptureUrl = comicCaptureUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getThreadPosition() {
        return threadPosition;
    }

    public void setThreadPosition(int threadPosition) {
        this.threadPosition = threadPosition;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getDownloadPosition() {
        return downloadPosition;
    }

    public void setDownloadPosition(int downloadPosition) {
        this.downloadPosition = downloadPosition;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public String getComicCaptureUrl() {
        return ComicCaptureUrl;
    }

    public void setComicCaptureUrl(String comicCaptureUrl) {
        ComicCaptureUrl = comicCaptureUrl;
    }
}
