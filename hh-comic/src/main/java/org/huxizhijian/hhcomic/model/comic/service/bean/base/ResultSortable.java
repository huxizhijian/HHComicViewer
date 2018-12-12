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

package org.huxizhijian.hhcomic.model.comic.service.bean.base;

import org.huxizhijian.hhcomic.model.comic.service.bean.Sort;

import java.util.List;

/**
 * @author huxizhijian
 * @date 2018/10/11
 */
public interface ResultSortable {
    /**
     * 结果是否有可选择的排序
     *
     * @return is result sortable
     */
    boolean isResultSortable();

    /**
     * 获取排序实体列表
     *
     * @return sort list
     */
    List<Sort> getResultSortList();
}
