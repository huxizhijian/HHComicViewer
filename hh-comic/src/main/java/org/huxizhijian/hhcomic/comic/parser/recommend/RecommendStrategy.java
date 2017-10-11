package org.huxizhijian.hhcomic.comic.parser.recommend;

import org.huxizhijian.hhcomic.comic.bean.Comic;
import org.huxizhijian.hhcomic.comic.parser.BaseParseStrategy;
import org.huxizhijian.hhcomic.comic.type.RecommendType;
import org.huxizhijian.hhcomic.comic.type.RequestFieldType;
import org.huxizhijian.hhcomic.comic.type.ResponseFieldType;
import org.huxizhijian.hhcomic.comic.value.IHHComicRequest;
import org.huxizhijian.hhcomic.comic.value.IHHComicResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import okhttp3.Request;

/**
 * 主页推荐的策略
 *
 * @Author huxizhijian on 2017/10/11.
 */

public abstract class RecommendStrategy extends BaseParseStrategy {

    /**
     * 获取推荐url
     *
     * @param recommendType 推荐类别，首页推荐等等
     * @param page          本页页数
     * @param size          一页展示的Comic
     */
    protected abstract String getRecommendUrl(Enum<RecommendType> recommendType, int page, int size);

    /**
     * 获取总页数
     *
     * @return 页数
     */
    protected abstract int getPageCount(byte[] data);

    /**
     * 解析Rank的Comic
     */
    protected abstract List<Comic> parseRecommendComics(byte[] data, Enum<RecommendType> recommendType) throws UnsupportedEncodingException;

    private Enum<RecommendType> mRecommendType;
    private int mPage;
    private int mSize;

    @Override
    public Request buildRequest(IHHComicRequest comicRequest) throws UnsupportedEncodingException {
        mRecommendType = comicRequest.getField(RequestFieldType.RECOMMEND_TYPE);
        mPage = comicRequest.getField(RequestFieldType.PAGE);
        mSize = comicRequest.getField(RequestFieldType.SIZE);
        return getRequestGetAndWithUrl(getRecommendUrl(mRecommendType, mPage, mSize));
    }

    @Override
    public IHHComicResponse parseData(IHHComicResponse comicResponse, byte[] data) throws IOException {
        comicResponse.addField(ResponseFieldType.PAGE_COUNT, getPageCount(data));
        comicResponse.addField(ResponseFieldType.PAGE, mPage);
        comicResponse.setResponse(parseRecommendComics(data, mRecommendType));
        return comicResponse;
    }

}
