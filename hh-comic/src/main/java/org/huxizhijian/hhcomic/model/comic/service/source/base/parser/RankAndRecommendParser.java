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

import org.huxizhijian.hhcomic.model.comic.service.bean.ComicListBean;
import org.huxizhijian.hhcomic.model.comic.service.bean.FilterList;
import org.huxizhijian.hhcomic.model.comic.service.bean.result.ComicResultList;

import java.io.UnsupportedEncodingException;

import androidx.annotation.Nullable;
import okhttp3.Request;

/**
 * 漫画排行/推荐列表的解析器
 *
 * @author huxizhijian
 * @date 2018/8/31
 */
public interface RankAndRecommendParser {

    /**
     * 构建排行榜请求Request
     *
     * @param listBean 排行的实体类
     * @param page     页码
     * @param picker   过滤选择器
     * @return request
     */
    Request buildRankRequest(ComicListBean listBean, int page,@Nullable FilterList.FilterPicker picker);

    /**
     * 解析排行结果
     *
     * @param html     html
     * @param listBean 列表信息实体类
     * @param page     页码
     * @return 结果
     * @throws UnsupportedEncodingException 可能出现的解析错误
     */
    ComicResultList parseRankList(byte[] html, ComicListBean listBean, int page) throws UnsupportedEncodingException;

    /**
     * 构建推荐请求Request，推荐通常是主页推荐，所以只需一次请求（如果需要二次请求在parse里进行）并且只需只需一页
     *
     * @return request
     */
    Request buildRecommendRequest();

    /**
     * 解析推荐结果
     *
     * @param html     html
     * @param listBean 列表信息实体类
     * @return 结果
     * @throws UnsupportedEncodingException 可能出现的解析错误
     */
    ComicResultList parseRecommendList(byte[] html, ComicListBean listBean) throws UnsupportedEncodingException;
}
