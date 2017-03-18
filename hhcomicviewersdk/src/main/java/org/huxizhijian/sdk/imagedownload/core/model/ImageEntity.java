package org.huxizhijian.sdk.imagedownload.core.model;

import java.util.List;

/**
 * @author huxizhijian 2017/3/16
 */
public class ImageEntity {

    private long chid;
    private String uri;
    private String downloadPath;
    private int serverId;
    private List<String> picList;

    public ImageEntity() {
    }

    public ImageEntity(long chid, String uri, String downloadPath, int serverId) {
        this.chid = chid;
        this.uri = uri;
        this.downloadPath = downloadPath;
        this.serverId = serverId;
    }

    public ImageEntity(long chid, String uri, String downloadPath, List<String> picList) {
        this.chid = chid;
        this.uri = uri;
        this.downloadPath = downloadPath;
        this.picList = picList;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public long getChid() {
        return chid;
    }

    public void setChid(long chid) {
        this.chid = chid;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public List<String> getPicList() {
        return picList;
    }

    public void setPicList(List<String> picList) {
        this.picList = picList;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
