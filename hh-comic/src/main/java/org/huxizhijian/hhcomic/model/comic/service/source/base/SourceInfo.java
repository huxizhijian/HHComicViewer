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

package org.huxizhijian.hhcomic.model.comic.service.source.base;

import org.huxizhijian.hhcomic.model.comic.service.bean.Category;
import org.huxizhijian.hhcomic.model.comic.service.bean.ComicListBean;

import java.util.List;

/**
 * 源信息接口，用于给view提供信息
 *
 * @author huxizhijian
 * @date 2018/11/19
 */
public interface SourceInfo {

    /**
     * 获取源的名称
     *
     * @return source name
     */
    String getSourceName();

    /**
     * 获取源的唯一id
     *
     * @return source key
     */
    String getSourceKey();

    /**
     * 获取源的分类
     *
     * @return 分类列表
     */
    List<Category> getCategory();

    /**
     * 是否有排序列表
     *
     * @return has rank
     */
    boolean hasRank();

    /**
     * 获取源支持查询的排行榜
     *
     * @return rank list
     */
    List<ComicListBean> getRank();

    /**
     * 是否有推荐列表
     *
     * @return has recommend
     */
    boolean hasRecommend();

    /**
     * 获取源支持查询的推荐列表
     *
     * @return recommend list
     */
    List<ComicListBean> getRecommend();
}
