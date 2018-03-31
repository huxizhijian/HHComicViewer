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

package org.huxizhijian.hhcomic.comic.type;

import org.huxizhijian.hhcomic.comic.source.base.SourceEnum;

/**
 * 加载请求时传入的参数key
 *
 * @author huxizhijian 2017/10/1
 */
@SuppressWarnings("unused")
public class RequestFieldType {
    /**
     * 搜索关键字
     */
    public static final int KEY_WORD = 0x0;
    /**
     * 页码，从0开始
     */
    public static final int PAGE = 0x1;
    /**
     * 总计页数
     */
    public static final int SIZE = 0x2;
    /**
     * 附加数据
     */
    public static final int BUNDLE = 0x3;
    /**
     * ComicId
     */
    public static final int COMIC_ID = 0x4;
    /**
     * Comic实例
     */
    public static final int COMIC = 0x5;
    /**
     * 站点源{@link SourceEnum}枚举的hashcode
     */
    public static final int COMIC_SOURCE_TYPE = 0x6;
    /**
     * 确切数据来源{@link DataSourceType}
     */
    public static final int DATA_SOURCE_TYPE = 0x7;
    /**
     * 类别map的key
     */
    public static final int CATEGORY = 0x8;
    /**
     * rank map的key
     */
    public static final int RANK_TYPE = 0x9;
    /**
     * recommend map的key
     */
    public static final int RECOMMEND_TYPE = 0xa;
    /**
     * 获取最新信息（不使用缓存）
     */
    public static final int GET_UPDATE = 0xb;
    /**
     * 排序
     */
    public static final int ORDER = 0xc;
    /**
     * 是否高亮
     */
    public static final int HIGH_LIGHT = 0xd;
}
