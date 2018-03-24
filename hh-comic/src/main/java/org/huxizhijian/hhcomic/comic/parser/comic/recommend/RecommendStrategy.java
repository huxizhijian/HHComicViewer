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

package org.huxizhijian.hhcomic.comic.parser.comic.recommend;

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
 * 主页推荐的策略
 *
 * @Author huxizhijian on 2017/10/11.
 */

public abstract class RecommendStrategy extends BaseComicParseStrategy {

    /**
     * 获取推荐url
     *
     * @param recommendType 推荐类别，首页推荐等等
     * @param page          本页页数
     * @param size          一页展示的Comic
     */
    protected abstract String getRecommendUrl(String recommendType, int page, int size);

    /**
     * 获取总页数
     *
     * @return 页数
     */
    protected abstract int getPageCount(byte[] data);

    /**
     * 解析Rank的Comic
     */
    protected abstract List<Comic> parseRecommendComics(byte[] data, String recommendType) throws UnsupportedEncodingException;

    private String mRecommendType;
    private int mPage;
    private Integer mSize;

    @Override
    public Request buildRequest(IComicRequest comicRequest) throws UnsupportedEncodingException, NullPointerException {
        if (comicRequest.getField(RequestFieldType.RECOMMEND_TYPE) == null) {
            throw new NullPointerException("recommend type should not be null!");
        }
        mRecommendType = comicRequest.getField(RequestFieldType.RECOMMEND_TYPE);
        mPage = comicRequest.getField(RequestFieldType.PAGE);
        mSize = comicRequest.getField(RequestFieldType.SIZE);
        if (mSize == null) {
            mSize = 0;
        }
        return getRequestGetAndWithUrl(getRecommendUrl(mRecommendType, mPage, mSize));
    }

    @Override
    public IComicResponse parseData(IComicResponse comicResponse, byte[] data) throws IOException {
        comicResponse.addField(ResponseFieldType.PAGE_COUNT, getPageCount(data));
        comicResponse.addField(ResponseFieldType.PAGE, mPage);
        comicResponse.setComicResponse(parseRecommendComics(data, mRecommendType));
        return comicResponse;
    }

}
