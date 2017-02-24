package org.huxizhijian.hhcomicviewer2.adapter.entity;

/**
 * Created by wei on 2017/1/10.
 */

public class RankTitleEntity {

    private int imgResId;
    private String title;
    private String link;

    public RankTitleEntity() {
    }

    public RankTitleEntity(int imgResId, String title, String link) {
        this.imgResId = imgResId;
        this.title = title;
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getImgResId() {
        return imgResId;
    }

    public void setImgResId(int imgResId) {
        this.imgResId = imgResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
