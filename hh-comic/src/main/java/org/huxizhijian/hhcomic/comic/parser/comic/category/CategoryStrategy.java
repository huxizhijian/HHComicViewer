package org.huxizhijian.hhcomic.comic.parser.comic.category;

import org.huxizhijian.hhcomic.comic.bean.Comic;
import org.huxizhijian.hhcomic.comic.parser.comic.BaseComicParseStrategy;
import org.huxizhijian.hhcomic.comic.type.CategoryType;
import org.huxizhijian.hhcomic.comic.type.RequestFieldType;
import org.huxizhijian.hhcomic.comic.type.ResponseFieldType;
import org.huxizhijian.hhcomic.comic.value.IComicRequest;
import org.huxizhijian.hhcomic.comic.value.IComicResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import okhttp3.Request;

/**
 * 分类策略
 *
 * @Author huxizhijian on 2017/10/11.
 */

public abstract class CategoryStrategy extends BaseComicParseStrategy {

    private Enum<CategoryType> mCategoryType;
    private int mPage;
    private int mSize;
    private int mPageCount;

    /**
     * 获取分类真实网址
     */
    protected abstract String getCategoryUrl(Enum<CategoryType> categoryType, int page, int size);

    /**
     * 获取页数上限
     */
    protected abstract int getPageCount(byte[] data) throws UnsupportedEncodingException;

    /**
     * 解析
     */
    protected abstract List<Comic> parseRecommend(byte[] data) throws UnsupportedEncodingException;

    @Override
    public Request buildRequest(IComicRequest comicRequest) throws UnsupportedEncodingException {
        mCategoryType = comicRequest.getField(RequestFieldType.CATEGORY);
        mPage = comicRequest.getField(RequestFieldType.PAGE);
        mSize = comicRequest.getField(RequestFieldType.SIZE);
        return getRequestGetAndWithUrl(getCategoryUrl(mCategoryType, mPage, mSize));
    }

    @Override
    public IComicResponse parseData(IComicResponse comicResponse, byte[] data) throws IOException {
        comicResponse.addField(ResponseFieldType.PAGE, mPage);
        mPageCount = getPageCount(data);
        comicResponse.addField(ResponseFieldType.PAGE_COUNT, mPageCount);
        comicResponse.setResponse(parseRecommend(data));
        return comicResponse;
    }

}
