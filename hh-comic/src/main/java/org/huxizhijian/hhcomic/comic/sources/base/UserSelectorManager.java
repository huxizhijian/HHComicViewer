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

package org.huxizhijian.hhcomic.comic.sources.base;

import android.support.annotation.NonNull;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 用户选择器manager, 不可变类, 该类提供排行, 分类, 推荐的结果的可选排序, 筛选等
 *
 * @author huxizhijian
 * @date 2018/4/23
 */
public interface UserSelectorManager<T extends UserSelectorManager.UserSelector.Builder> {

    /**
     * 获取filter的组别(同一组别的filter只能单选, 每个组别可选一个filter)
     *
     * @return subjectList
     */
    List<String> getFilterSubjects();

    /**
     * 获取组包含的filter
     *
     * @param subject 组名
     * @return filter
     */
    List<SelectItem> getOneSubjectFilters(@NonNull String subject);

    /**
     * 获取所有组及组内的filter
     *
     * @return 有序的的map, 我们不希望组是无序的
     */
    LinkedHashMap<String, List<SelectItem>> getAllSubjectFilters();

    /**
     * 获取可选的排序
     *
     * @return sortList
     */
    List<SelectItem> getSort();

    /**
     * 用户开始选择一个filter, 返回一个Builder, Builder类可以复用
     *
     * @param subject filter组别
     * @param item    filter
     * @return selector
     */
    T startSelectFilter(@NonNull String subject, @NonNull SelectItem item);

    /**
     * 用户开始选择一个sort, 返回一个Builder, 该类可以复用
     *
     * @param item sort
     * @return selector
     */
    T startSelectSort(@NonNull SelectItem item);

    /**
     * 用户选择器, 不可变类, 目的是不产生不可预知的结果
     */
    interface UserSelector {

        /**
         * 获取被用户选中的filter的组
         *
         * @return subjectList
         */
        List getSelectFilterSubject();

        /**
         * 获取组内被选择的filter(组内仅能单选)
         *
         * @param subject 组名
         * @return filter
         */
        SelectItem getSelectFilter(@NonNull String subject);

        /**
         * 获取所有被选中的filter
         *
         * @return filterList
         */
        List<SelectItem> getAllSelectFilter();

        /**
         * 获取选择的排序
         *
         * @return sort
         */
        SelectItem getSelectSort();

        /**
         * Selector的构造器类
         * 我们希望Selector是不可变类, 所以它会有一个可变的辅助构造类, 以帮助完成代码复用
         */
        interface Builder<T extends UserSelector, U extends Builder> {

            /**
             * 构造方法
             *
             * @return selector instance
             */
            T build();

            /**
             * 用户选中(当选中相同组的filter时, 我们会取消之前的选中)
             *
             * @param subject 组名
             * @param filter  filter
             * @return this
             */
            U filter(@NonNull String subject, @NonNull SelectItem filter);

            /**
             * 用户选择排序(所有的排序都是互斥的, 选择新的会取消选择旧的, 这也是当然的)
             *
             * @param sort 排序
             * @return this
             */
            U sort(@NonNull SelectItem sort);
        }
    }
}
