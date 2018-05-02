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

import android.support.annotation.NonNull;

import org.huxizhijian.hhcomic.comic.sources.base.SelectItem;
import org.huxizhijian.hhcomic.comic.sources.base.SelectorManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huxizhijian
 * @date 2018/4/28
 */
public class ComicUserSelectorManager implements SelectorManager<ComicUserSelectorManager.ComicUserSelector.Builder> {

    private LinkedHashMap<String, List<SelectItem>> mFilters;
    private List<SelectItem> mSorts;

    private ComicUserSelectorManager(Builder builder) {
        mFilters = new LinkedHashMap<>();
        mSorts = new ArrayList<>();
        mFilters.putAll(builder.filterListMap);
        mSorts.addAll(builder.sortList);
    }

    @Override
    public List<String> getFilterSubjects() {
        return new ArrayList<>(mFilters.keySet());
    }

    @Override
    public List<SelectItem> getOneSubjectFilters(@NonNull String subject) {
        return new ArrayList<>(mFilters.get(subject));
    }

    @Override
    public LinkedHashMap<String, List<SelectItem>> getAllSubjectFilters() {
        return new LinkedHashMap<>(mFilters);
    }

    @Override
    public List<SelectItem> getSort() {
        return new ArrayList<>(mSorts);
    }

    @Override
    public ComicUserSelector.Builder startSelectFilter(@NonNull String subject, @NonNull SelectItem item) {
        return new ComicUserSelector.Builder(this).filter(subject, item);
    }

    @Override
    public ComicUserSelector.Builder startSelectSort(@NonNull SelectItem item) {
        return new ComicUserSelector.Builder(this).sort(item);
    }

    private boolean hasSubject(@NonNull String subject) {
        return mFilters.containsKey(subject);
    }

    private boolean hasItem(@NonNull String subject, @NonNull SelectItem item) {
        return hasSubject(subject) && mFilters.get(subject).contains(item);
    }

    private boolean hasSort(@NonNull SelectItem sort) {
        return mSorts.contains(sort);
    }

    /**
     * builder模式, 可以让manager复用
     */
    public static class Builder {

        Map<String, List<SelectItem>> filterListMap;
        List<SelectItem> sortList;
        /**
         * 辅助类, 让添加一组filter变得更加流式
         */
        SubjectStream subjectStream;

        public Builder() {
            filterListMap = new LinkedHashMap<>();
            sortList = new ArrayList<>();
        }

        public SubjectStream startSubject(@NonNull String subjectName) {
            if (subjectName.trim().isEmpty()) {
                throw new IllegalArgumentException("subject name should not be empty!");
            }
            return newSubjectStream(subjectName);
        }

        public Builder addSort(@NonNull String name, @NonNull String path) {
            if (name.trim().isEmpty() || path.trim().isEmpty()) {
                throw new IllegalArgumentException("name or path should not be empty!");
            }
            sortList.add(SelectItem.create(name, path));
            return this;
        }

        public ComicUserSelectorManager build() {
            return new ComicUserSelectorManager(this);
        }

        private SubjectStream newSubjectStream(String subject) {
            if (subjectStream != null) {
                subjectStream.subject = subject;
            } else {
                subjectStream = new SubjectStream(subject, this);
            }
            return subjectStream;
        }

        /**
         * 流式操作subject的添加
         */
        public static class SubjectStream {

            Builder builder;
            String subject;
            List<SelectItem> filterList;

            private SubjectStream(@NonNull String subject, @NonNull Builder builder) {
                this.subject = subject;
                filterList = new ArrayList<>();
                this.builder = builder;
            }

            public SubjectStream addFilter(@NonNull String name, @NonNull String path) {
                if (name.trim().isEmpty() || path.trim().isEmpty()) {
                    throw new IllegalArgumentException("name or path should not be empty!");
                }
                filterList.add(SelectItem.create(name, path));
                return this;
            }

            public Builder addLastFilter(@NonNull String name, @NonNull String path) {
                addFilter(name, path)
                        .addFilterSubject();
                return builder;
            }

            public Builder end() {
                addFilterSubject();
                return builder;
            }

            private void addFilterSubject() {
                builder.filterListMap.put(subject, filterList);
            }
        }
    }

    /**
     * 用户选择, 用于返回到source去构造url
     */
    public static class ComicUserSelector implements SelectorManager.UserSelector {

        private final Map<String, SelectItem> mFilterSelect;

        private final SelectItem mSortSelect;

        private ComicUserSelector(Builder builder) {
            mFilterSelect = new HashMap<>();
            mFilterSelect.putAll(builder.mFilterSelect);
            mSortSelect = builder.mSortSelect;
        }

        @Override
        public List<String> getSelectFilterSubject() {
            return new ArrayList<>(mFilterSelect.keySet());
        }

        @Override
        public SelectItem getSelectFilter(@NonNull String subject) {
            return mFilterSelect.get(subject);
        }

        @Override
        public List<SelectItem> getAllSelectFilter() {
            return new ArrayList<>(mFilterSelect.values());
        }

        @Override
        public SelectItem getSelectSort() {
            return mSortSelect;
        }

        /**
         * selector的构造器
         */
        public static class Builder implements SelectorManager.UserSelector.Builder
                <ComicUserSelector, ComicUserSelector.Builder> {

            private ComicUserSelectorManager mManager;

            private Map<String, SelectItem> mFilterSelect;

            private SelectItem mSortSelect;

            public Builder(@NonNull ComicUserSelectorManager manager) {
                mManager = manager;
                // 多线程安全的map
                mFilterSelect = new Hashtable<>();
            }

            @Override
            public ComicUserSelector build() {
                return new ComicUserSelector(this);
            }

            @Override
            public Builder filter(@NonNull String subject, @NonNull SelectItem filter) {
                if (mManager.hasItem(subject, filter)) {
                    // 安全检查, 只有manager存在这个item才加入选择
                    // 会覆盖之前的选择
                    mFilterSelect.put(subject, filter);
                }
                return this;
            }

            @Override
            public Builder sort(@NonNull SelectItem sort) {
                if (mManager.hasSort(sort)) {
                    // 会取消之前的sortSelect
                    mSortSelect = sort;
                }
                return this;
            }
        }
    }
}
