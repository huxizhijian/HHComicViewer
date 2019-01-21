/*
 * Copyright 2016-2019 huxizhijian
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

package org.huxizhijian.hhcomicviewer.adapter;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import org.huxizhijian.hhcomic.model.comic.db.entity.Comic;

import java.util.List;

/**
 * 多布局item，不变类
 *
 * @author huxizhijian
 * @date 2018/12/22
 */
public final class RecommendMultipleItem implements MultiItemEntity {

    /**
     * 标题
     */
    public static final int TITLE = 1;
    /**
     * 列表
     */
    public static final int LIST = 2;

    /**
     * 变量，保存具体实例item的类型
     */
    private final int mItemType;

    public final List<Comic> mComicList;

    public final String mTitle;

    public RecommendMultipleItem(int itemType, List<Comic> comicList) {
        mItemType = itemType;
        mComicList = comicList;
        mTitle = null;
    }

    public RecommendMultipleItem(int itemType, String title) {
        mItemType = itemType;
        mComicList = null;
        mTitle = title;
    }

    @Override
    public int getItemType() {
        return mItemType;
    }

    public List<Comic> getComicList() {
        return mComicList;
    }

    public String getTitle() {
        return mTitle;
    }
}