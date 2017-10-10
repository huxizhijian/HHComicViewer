package org.huxizhijian.hhcomic.comic.parser.rank;

import org.huxizhijian.hhcomic.comic.bean.Comic;
import org.huxizhijian.hhcomic.comic.parser.BaseParseStrategy;
import org.huxizhijian.hhcomic.comic.type.RankType;
import org.huxizhijian.hhcomic.comic.type.RequestFieldType;
import org.huxizhijian.hhcomic.comic.type.ResponseFieldType;
import org.huxizhijian.hhcomic.comic.value.IHHComicRequest;
import org.huxizhijian.hhcomic.comic.value.IHHComicResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import okhttp3.Request;

/**
 * @Author huxizhijian on 2017/10/10.
 */

public abstract class RankStrategy extends BaseParseStrategy {

    /**
     * 获取排行url
     *
     * @param rankType 排行榜类别，如人气榜、更新榜等等
     * @param page     本页页数
     * @param size     一页展示的Comic
     */
    protected abstract String getRankUrl(Enum<RankType> rankType, int page, int size);

    /**
     * 获取总页数
     *
     * @return 页数
     */
    protected abstract int getPageCount(byte[] data);

    /**
     * 解析Rank的Comic
     */
    protected abstract List<Comic> parseRankComics(byte[] data) throws UnsupportedEncodingException;

    private Enum<RankType> mRankType;
    private int mPage;
    private int mSize;

    @Override
    public Request buildRequest(IHHComicRequest comicRequest) throws UnsupportedEncodingException {
        mRankType = comicRequest.getField(RequestFieldType.RANK_TYPE);
        mPage = comicRequest.getField(RequestFieldType.PAGE);
        mSize = comicRequest.getField(RequestFieldType.SIZE);
        return getRequestGetAndWithUrl(getRankUrl(mRankType, mPage, mSize));
    }

    @Override
    public IHHComicResponse parseData(IHHComicResponse comicResponse, byte[] data) throws IOException {
        comicResponse.addField(ResponseFieldType.PAGE_COUNT, getPageCount(data));
        comicResponse.setResponse(parseRankComics(data));
        return comicResponse;
    }

}
