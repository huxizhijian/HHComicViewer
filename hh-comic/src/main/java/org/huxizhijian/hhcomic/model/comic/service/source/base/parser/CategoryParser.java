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

package org.huxizhijian.hhcomic.model.comic.service.source.base.parser;

import org.huxizhijian.hhcomic.model.comic.service.bean.Category;
import org.huxizhijian.hhcomic.model.comic.service.bean.FilterList;
import org.huxizhijian.hhcomic.model.comic.service.bean.Sort;
import org.huxizhijian.hhcomic.model.comic.service.bean.result.ComicResultList;

import java.io.UnsupportedEncodingException;

import okhttp3.Request;

/**
 * 分类列表解析器
 *
 * @author huxizhijian
 * @date 2018/8/31
 */
public interface CategoryParser {

    /**
     * 获取分类列表的请求Request
     *
     * @param category 分类实体类
     * @param page     列表第几页
     * @param picker   过滤选择器
     * @param sort     选择的排序
     * @return request
     */
    Request buildCategoryRequest(Category category, int page, FilterList.FilterPicker picker, Sort sort);

    /**
     * 分析请求返回的数据，将网页转换成实体类的结果列表
     *
     * @param html     网页数据
     * @param category 分类
     * @param page     请求的列表第几页
     * @return 返回结果列表实体类，包含一些额外信息
     * @throws UnsupportedEncodingException 转码中可能出现的异常
     */
    ComicResultList parseCategoryList(byte[] html, Category category, int page) throws UnsupportedEncodingException;
}
