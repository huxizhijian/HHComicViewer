package org.huxizhijian.hhcomic.comic.bean.result;

import org.huxizhijian.hhcomic.comic.bean.base.Tag;
import org.huxizhijian.hhcomic.comic.entity.Comic;

import java.util.List;
import java.util.Map;

/**
 * 漫画结果列表返回
 *
 * @author huxizhijian
 * @date 2018/8/31
 */
public final class ComicResultList {

    /**
     * 结果列表
     */
    private final List<Comic> mComicList;

    /**
     * 结果名称
     */
    private final String mResultName;

    /**
     * 当前页码
     */
    private final int mPage;

    /**
     * 总页码
     */
    private final int mPageCount;

    /**
     * 是否为空
     */
    private final boolean mIsEmpty;

    /**
     * 结果筛选实体map
     */
    private Map<String, List<Tag>> mFilterMap;

    public ComicResultList(List<Comic> comicList, String resultName, int page, int pageCount) {
        this(comicList, resultName, page, pageCount, false);
    }

    public ComicResultList(List<Comic> comicList, String resultName, int page, int pageCount, boolean isEmpty) {
        mComicList = comicList;
        mResultName = resultName;
        mPage = page;
        mPageCount = pageCount;
        mIsEmpty = isEmpty;
    }

    public List<Comic> getComicList() {
        return mComicList;
    }

    public String getResultName() {
        return mResultName;
    }

    public int getPage() {
        return mPage;
    }

    public int getPageCount() {
        return mPageCount;
    }

    public boolean isEmpty() {
        return mIsEmpty;
    }
}
