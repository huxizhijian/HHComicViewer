package org.huxizhijian.hhcomicviewer2.adapter.entity;

import org.huxizhijian.hhcomicviewer2.model.Comic;

import java.io.Serializable;
import java.util.List;

/**
 * 首页推荐bean类
 * Created by wei on 2017/1/4.
 */

public class ComicTabList implements Serializable {

    private List<Comic> comics;
    private String tabName;

    public ComicTabList(List<Comic> comics, String tabName) {
        this.comics = comics;
        this.tabName = tabName;
    }

    public List<Comic> getComics() {
        return comics;
    }

    public void setComics(List<Comic> comics) {
        this.comics = comics;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

}
