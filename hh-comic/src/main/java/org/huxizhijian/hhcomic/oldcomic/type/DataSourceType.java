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

package org.huxizhijian.hhcomic.oldcomic.type;

/**
 * Comic获取来源类型
 *
 * @author huxizhijian 2017/9/21
 */
public class DataSourceType {
    /**
     * 数据库中标记{@link ComicDataState}为历史的comic列表
     */
    public static final int DB_HISTORY = 0x0;
    /**
     * 数据库中标记为收藏的comic列表
     */
    public static final int DB_FAVORITE = 0x1;
    /**
     * 数据库中标记为下载的comic列表
     */
    public static final int DB_DOWNLOADED = 0x2;
    /**
     * 网络搜索,返回结果列表
     */
    public static final int WEB_SEARCH = 0x3;
    /**
     * 网络获取comic详情
     */
    public static final int WEB_DETAIL = 0x4;
    /**
     * 网络获取推荐列表
     */
    public static final int WEB_RECOMMENDED = 0x5;
    /**
     * 网络获取comic排名列表
     */
    public static final int WEB_RANK = 0x6;
    /**
     * 网络获取comic分类列表
     */
    public static final int WEB_CATEGORY = 0x7;
    /**
     * 网络获取comic对应的chapter
     */
    public static final int WEB_GET_CHAPTER = 0x8;
}
