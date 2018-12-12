/*
 * Copyright 2016-2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.hhcomic.model.comic.service.bean.result;

import org.huxizhijian.hhcomic.model.comic.service.bean.base.Tag;
import org.huxizhijian.hhcomic.model.comic.db.entity.Comic;

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
     * 总页码，-1为unknown
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
