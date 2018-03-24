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

package org.huxizhijian.hhcomic.comic.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Comic实体类, 使用greenDAO的注解自动生成
 *
 * @author huxizhijian 2017/9/21
 */
@Entity
public class Comic {

    @Id(autoincrement = true)
    private Long id;
    // 来源网站
    @NotNull
    private int source;
    // 来源网站标识的comic id
    @NotNull
    private String cid;
    // 标题
    @NotNull
    private String title;
    // 封面图片url
    @NotNull
    private String cover;
    // 是否高亮显示
    @NotNull
    private boolean highlight = false;
    // 是否来自本地
    @NotNull
    private boolean local = false;

    // 更新时间
    private String update;
    // 是否完结（完结/连载）
    private Boolean finish;
    // 收藏
    private Long favorite;
    // 历史
    private Long history;
    // 下载
    private Long download;
    // 最后一次阅读时间
    private String last;
    // 阅读到的页数
    private Integer page;
    // 阅读到的章节
    private String chapter;

    /**
     * 以下的东西不一定存在
     */
    //章节总数
    private Integer chapterCount;
    // 介绍
    private String intro;
    // 作者
    private String author;
    // 最近更新的章节
    private String newChapter;
    // 评分
    private Float rate;
    // 评分人数
    private Integer ratePeopleCount;
    // 收藏数
    private Float favoriteCount;

    public Comic(int source, String cid, String title, String cover, String update, String author) {
        this(null, source, cid, title, cover == null ? "" : cover, false, false, update,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        this.author = author;
    }

    public Comic(int source, String cid) {
        this.source = source;
        this.cid = cid;
    }

    public Comic(int source, String cid, String title, String cover, long download) {
        this(null, source, cid, title, cover == null ? "" : cover, false, false, null,
                null, null, null, download, null, null, null, null, null, null, null, null, null, null);
    }

    @Generated(hash = 349236900)
    public Comic(Long id, int source, @NotNull String cid, @NotNull String title, @NotNull String cover,
                 boolean highlight, boolean local, String update, Boolean finish, Long favorite,
                 Long history, Long download, String last, Integer page, String chapter,
                 Integer chapterCount, String intro, String author, String newChapter, Float rate,
                 Integer ratePeopleCount, Float favoriteCount) {
        this.id = id;
        this.source = source;
        this.cid = cid;
        this.title = title;
        this.cover = cover;
        this.highlight = highlight;
        this.local = local;
        this.update = update;
        this.finish = finish;
        this.favorite = favorite;
        this.history = history;
        this.download = download;
        this.last = last;
        this.page = page;
        this.chapter = chapter;
        this.chapterCount = chapterCount;
        this.intro = intro;
        this.author = author;
        this.newChapter = newChapter;
        this.rate = rate;
        this.ratePeopleCount = ratePeopleCount;
        this.favoriteCount = favoriteCount;
    }

    @Generated(hash = 1347984162)
    public Comic() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSource() {
        return this.source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getCid() {
        return this.cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return this.cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public boolean getHighlight() {
        return this.highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    public boolean getLocal() {
        return this.local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public String getUpdate() {
        return this.update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public Boolean getFinish() {
        return this.finish;
    }

    public void setFinish(Boolean finish) {
        this.finish = finish;
    }

    public Long getFavorite() {
        return this.favorite;
    }

    public void setFavorite(Long favorite) {
        this.favorite = favorite;
    }

    public Long getHistory() {
        return this.history;
    }

    public void setHistory(Long history) {
        this.history = history;
    }

    public Long getDownload() {
        return this.download;
    }

    public void setDownload(Long download) {
        this.download = download;
    }

    public String getLast() {
        return this.last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public Integer getPage() {
        return this.page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getChapter() {
        return this.chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public Integer getChapterCount() {
        return this.chapterCount;
    }

    public void setChapterCount(Integer chapterCount) {
        this.chapterCount = chapterCount;
    }

    public String getIntro() {
        return this.intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getNewChapter() {
        return this.newChapter;
    }

    public void setNewChapter(String newChapter) {
        this.newChapter = newChapter;
    }

    public Float getRate() {
        return this.rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public Float getFavoriteCount() {
        return this.favoriteCount;
    }

    public void setFavoriteCount(Float favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Integer getRatePeopleCount() {
        return this.ratePeopleCount;
    }

    public void setRatePeopleCount(Integer ratePeopleCount) {
        this.ratePeopleCount = ratePeopleCount;
    }

}
