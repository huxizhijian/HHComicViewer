/*
 * Copyright 2018 huxizhijian
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

package org.huxizhijian.hhcomic.oldcomic.db;

import org.huxizhijian.hhcomic.oldcomic.bean.Comic;

import java.util.List;

/**
 * @author huxizhijian
 * @date 2017/9/26.
 */
public interface IComicDBHelper {

    // 最后阅读时间倒序
    public static final int ORDER_LAST_DESC = 0x0;
    // 最后阅读时间正序
    public static final int ORDER_LAST_ASC = 0x1;
    // 更新时间倒序
    public static final int ORDER_UPDATE_DESC = 0x2;
    // 更新时间正序
    public static final int ORDER_UPDATE_ASC = 0x3;
    // 相对应的属性倒序（如请求获取DownloadList，则为对应Download值的时间戳排序，时间戳会在用户有对应行为时记录）
    public static final int ORDER_CORRESPOND_DESC = 0x4;
    // 相对应的属性正序
    public static final int ORDER_CORRESPOND_ASC = 0x5;

    void insert(Comic comic);

    void update(Comic comic);

    void delete(Comic comic);

    Comic get(int source, String comicId);

    List<Comic> getAll();

    /**
     * 获取历史
     *
     * @param order 排序方式
     */
    List<Comic> getHistoryList(int order);

    /**
     * 获取收藏列表
     *
     * @param order     排序方式
     * @param highLight 是否将标记为高亮的Comic排在前面
     */
    List<Comic> getFavoriteList(int order, boolean highLight);

    /**
     * 获取下载列表
     *
     * @param order 排序方式
     */
    List<Comic> getDownloadList(int order);

}
