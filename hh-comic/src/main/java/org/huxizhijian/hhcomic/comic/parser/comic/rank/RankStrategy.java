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

package org.huxizhijian.hhcomic.comic.parser.comic.rank;

import org.huxizhijian.hhcomic.comic.bean.Comic;
import org.huxizhijian.hhcomic.comic.parser.comic.BaseComicParseStrategy;
import org.huxizhijian.hhcomic.comic.type.RequestFieldType;
import org.huxizhijian.hhcomic.comic.type.ResponseFieldType;
import org.huxizhijian.hhcomic.comic.value.IComicRequest;
import org.huxizhijian.hhcomic.comic.value.IComicResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import okhttp3.Request;

/**
 * @author huxizhijian
 * @date 2017/10/10
 */

public abstract class RankStrategy extends BaseComicParseStrategy {

    /**
     * 获取排行url
     *
     * @param rankType 排行榜类别，如人气榜、更新榜等等
     * @param page     本页页数
     * @param size     一页展示的Comic
     * @return url
     */
    protected abstract String getRankUrl(String rankType, int page, int size);

    /**
     * 获取总页数
     *
     * @param data 网页内容
     * @return 页数
     */
    protected abstract int getPageCount(byte[] data);

    /**
     * 解析Rank的Comic
     *
     * @param data 网页内容
     * @return 结果集合
     * @throws UnsupportedEncodingException 解析时可能抛出的异常
     */
    protected abstract List<Comic> parseRankComics(byte[] data) throws UnsupportedEncodingException;

    private String mRankType;
    private int mPage;
    private int mSize;

    @Override
    public Request buildRequest(IComicRequest comicRequest) throws UnsupportedEncodingException {
        if (comicRequest.getField(RequestFieldType.RANK_TYPE)) {
            throw new NullPointerException("rank type should not be null!");
        }
        mRankType = comicRequest.getField(RequestFieldType.RANK_TYPE);
        mPage = comicRequest.getField(RequestFieldType.PAGE);
        mSize = comicRequest.getField(RequestFieldType.SIZE);
        return getRequestGetAndWithUrl(getRankUrl(mRankType, mPage, mSize));
    }

    @Override
    public IComicResponse parseData(IComicResponse comicResponse, byte[] data) throws IOException {
        comicResponse.addField(ResponseFieldType.PAGE_COUNT, getPageCount(data));
        comicResponse.addField(ResponseFieldType.PAGE, mPage);
        comicResponse.setComicResponse(parseRankComics(data));
        return comicResponse;
    }

}
