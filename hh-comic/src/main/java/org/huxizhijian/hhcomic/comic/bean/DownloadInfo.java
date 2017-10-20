package org.huxizhijian.hhcomic.comic.bean;

import android.os.Parcel;

/**
 * 下载内容
 *
 * @author huxizhijian
 * @date 2017/10/20
 */
public class DownloadInfo extends Chapter {

    public int state;
    public int legacy;
    public long time;

    public DownloadInfo(String title, String chapterId, int count, boolean complete, boolean download, long tid) {
        super(title, chapterId, count, complete, download, tid);
    }

    public DownloadInfo(String title, String path, long tid) {
        super(title, path, tid);
    }

    public DownloadInfo(String title, String path) {
        super(title, path);
    }

    public DownloadInfo(Parcel source) {
        super(source);
    }

}
