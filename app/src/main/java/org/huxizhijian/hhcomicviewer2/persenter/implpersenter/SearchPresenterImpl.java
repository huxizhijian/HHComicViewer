package org.huxizhijian.hhcomicviewer2.persenter.implpersenter;

import org.huxizhijian.hhcomicviewer2.app.HHApplication;
import org.huxizhijian.hhcomicviewer2.model.Comic;
import org.huxizhijian.hhcomicviewer2.persenter.ISearchPresenter;
import org.huxizhijian.hhcomicviewer2.persenter.viewinterface.ISearchActivity;
import org.huxizhijian.hhcomicviewer2.utils.HHApiProvider;
import org.huxizhijian.sdk.network.service.NormalRequest;
import org.huxizhijian.sdk.network.service.NormalResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果提供者
 * Created by wei on 2017/1/12.
 */

public class SearchPresenterImpl implements ISearchPresenter {

    private ISearchActivity mSearchActivity;
    private boolean mIsSearching = false;
    private List<String> mCancelQueryList = new ArrayList<>();

    public SearchPresenterImpl(ISearchActivity searchActivity) {
        this.mSearchActivity = searchActivity;
    }

    @Override
    public void getResult(final String query) {
        mIsSearching = true;
        String getKey = null;
        try {
            getKey = "?key=" + URLEncoder.encode(query, "GB2312");
        } catch (UnsupportedEncodingException e) {
            mSearchActivity.onException(e);
        }
        getKey += "&button=%CB%D1%CB%F7%C2%FE%BB%AD";
        String url = HHApplication.getInstance().getHHWebVariable().getSearchUrl() + getKey;
        HHApiProvider.getInstance().getWebContentAsyn(url, new NormalResponse<byte[]>() {
            @Override
            public void success(NormalRequest request, byte[] data) {
                if (mCancelQueryList.contains(query)) {
                    mCancelQueryList.remove(query);
                    return;
                }
                String content = null;
                try {
                    content = new String(data, "gb2312");
                    Document doc = Jsoup.parse(content);
                    Element comicSrcs = doc.select("div[class=dSHtm]").first();
                    Elements comicUrls = comicSrcs.select("div");
                    comicUrls.remove(0);

                    List<Comic> comics = new ArrayList<>();
                    Comic comic = null;

                    for (int i = 0; i < comicUrls.size(); i++) {
                        comic = new Comic();
                        Element comicSrc = comicUrls.get(i).select("a").first();
                        String url = comicSrc.attr("href");
                        String[] urlSplit = url.split("/");
                        String end = urlSplit[urlSplit.length - 1];
                        comic.setCid(Integer.parseInt(end.split("\\.")[0]));
                        comic.setTitle(comicSrc.text());
                        Element imgUrl = comicUrls.get(i).select("img").first();
                        comic.setThumbnailUrl(imgUrl.attr("src"));
                        Elements desc = comicUrls.get(i).getElementsByTag("br");
                        comic.setDescription(desc.get(2).text());
                        comics.add(comic);
                    }
                    if (mSearchActivity != null) {
                        mSearchActivity.onSuccess(comics);
                    }
                    mIsSearching = false;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    if (mSearchActivity != null) {
                        mSearchActivity.onException(e);
                    }
                    mIsSearching = false;
                }

            }

            @Override
            public void fail(int errorCode, String errorMsg) {
                if (mCancelQueryList.contains(query)) {
                    mCancelQueryList.remove(query);
                    return;
                }
                if (mSearchActivity != null) {
                    mSearchActivity.onFailure(errorCode, errorMsg);
                }
                mIsSearching = false;
            }
        });
    }

    @Override
    public boolean isSearching() {
        return mIsSearching;
    }

    @Override
    public void cancel(String query) {
        mCancelQueryList.add(query);
    }

    @Override
    public void removeListener() {
        mSearchActivity = null;
    }
}
