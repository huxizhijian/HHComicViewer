package org.huxizhijian.hhcomic.comic.bean;

import org.huxizhijian.hhcomic.comic.entity.Comic;

import java.util.List;

/**
 * 漫画结果列表返回
 * @author huxizhijian
 * @date 2018/8/31
 */
public final class ComicResultList {

    /**
     * 结果列表
     */
    private final List<Comic> mComicList;

    /**
     * 当前页码
     */
    private final int mPage;

    /**
     * 总页码
     */
    private final int mPageCount;

    public ComicResultList(List<Comic> comicList, int page, int pageCount) {
        mComicList = comicList;
        mPage = page;
        mPageCount = pageCount;
    }

    public List<Comic> getComicList() {
        return mComicList;
    }

    public int getPage() {
        return mPage;
    }

    public int getPageCount() {
        return mPageCount;
    }
}
