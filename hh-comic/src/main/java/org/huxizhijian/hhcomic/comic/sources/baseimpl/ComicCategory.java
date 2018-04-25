/*
 * Copyright 2016-2018 huxizhijian
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

package org.huxizhijian.hhcomic.comic.sources.baseimpl;

import org.huxizhijian.hhcomic.comic.sources.base.Category;
import org.huxizhijian.hhcomic.comic.sources.base.FilterManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * @author huxizhijian
 * @date 2018/4/24
 */
public class ComicCategory implements Category {

    private FilterManager mFilterManager;
    /**
     * 有序的map
     */
    private LinkedHashMap<String, String> mCategory = new LinkedHashMap<>();
    private GetUrl mGetUrl;

    private ComicCategory(FilterManager filterManager, GetUrl getUrl) {
        mFilterManager = filterManager;
        mGetUrl = getUrl;
    }

    @Override
    public FilterManager getFilter() {
        return mFilterManager;
    }

    @Override
    public List<String> getCategoryNames() {
        Set<String> keys = mCategory.keySet();
        Iterator<String> keyName = keys.iterator();
        List<String> categoryNames = new ArrayList<>();
        while (keyName.hasNext()) {
            categoryNames.add(keyName.next());
        }
        return categoryNames;
    }

    @Override
    public String getCategoryPath(String name) {
        return mCategory.get(name);
    }

    @Override
    public String getUrl(String name, int page, FilterManager.FilterSelector selector) {
        return mGetUrl.getUrl(getCategoryPath(name), page, selector);
    }

    public static ComicCategory init(FilterManager filterManager, GetUrl getUrl) {
        return new ComicCategory(filterManager, getUrl);
    }

    public ComicCategory addCategory(String name, String path) {
        mCategory.put(name, path);
        return this;
    }

    public interface GetUrl {
        /**
         * 根据path和page以及filter构造Url
         *
         * @param categoryPath 设置的path, 用于辅助构造url
         * @param page         页码
         * @param selector     selector
         * @return url
         */
        String getUrl(String categoryPath, int page, FilterManager.FilterSelector selector);
    }
}
