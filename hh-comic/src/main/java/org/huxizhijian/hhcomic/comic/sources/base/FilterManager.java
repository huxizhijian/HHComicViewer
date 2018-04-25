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

import java.util.Hashtable;
import java.util.List;

/**
 * @author huxizhijian
 * @date 2018/4/23
 */
public interface FilterManager {

    /**
     * 获取filter的组别(同一组别的filter只能单选)
     *
     * @return subjectList
     */
    List<String> getFilterSubjects();

    /**
     * 获取组下包含的filter
     *
     * @param subject 组名
     * @return filter
     */
    List<Filter> getOneSubjectFilters(String subject);

    /**
     * 获取所有组及组内的filter
     *
     * @return 有顺序的的map, 我们不希望组是无序的
     */
    Hashtable<String, List<Filter>> getAllSubjectFilters();

    /**
     * 获取可选的排序
     *
     * @return sortList
     */
    List<Filter> getSort();

    /**
     * 用户filter选择器
     */
    public interface FilterSelector {

        /**
         * 获取被用户选中的filter的组
         *
         * @return subjectList
         */
        List<String> getSelectFilterSubject();

        /**
         * 获取组内被选择的filter(组内仅能单选)
         *
         * @param subject 组名
         * @return filter
         */
        Filter getSelectFilter(String subject);

        /**
         * 获取所有被选中的filter
         *
         * @return filterList
         */
        List<Filter> getAllSelectFitlter();

        /**
         * 获取选择的排序
         *
         * @return sort
         */
        Filter getSort();

        /**
         * Selector的构造器类
         * 我们希望Selector是不可变类, 所以它会有一个可变的辅助构造类, 以帮助完成代码复用
         */
        public interface Builder {

            /**
             * 构造方法
             *
             * @return selector instance
             */
            FilterSelector build();

            /**
             * 当没有调用init方法时, 不能调用build方法
             * 这是为了安全检查, 不会导致不存在的filter或者sort被选中
             *
             * @param manager filterManager
             * @return this
             */
            Builder init(FilterManager manager);

            /**
             * 用户选中(当选中相同组的filter时, 我们会取消之前的选中)
             *
             * @param subject 组名
             * @param filter  filter
             * @return this
             */
            Builder select(String subject, Filter filter);

            /**
             * 选择排序
             *
             * @param sort 排序
             * @return this
             */
            Builder sort(Filter sort);
        }
    }
}
