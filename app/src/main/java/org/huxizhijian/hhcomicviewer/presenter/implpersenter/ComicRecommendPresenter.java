/*
 * Copyright 2017 huxizhijian
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
package org.huxizhijian.hhcomicviewer.presenter.implpersenter;

import org.huxizhijian.hhcomicviewer.app.HHApplication;
import org.huxizhijian.hhcomicviewer.adapter.entity.ComicTabList;
import org.huxizhijian.hhcomicviewer.model.Comic;
import org.huxizhijian.hhcomicviewer.presenter.IComicRecommendPresenter;
import org.huxizhijian.hhcomicviewer.presenter.viewinterface.IComicRecommendFragment;
import org.huxizhijian.hhcomicviewer.utils.HHApiProvider;
import org.huxizhijian.sdk.network.service.NormalRequest;
import org.huxizhijian.sdk.network.service.NormalResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei on 2017/1/4.
 */

public class ComicRecommendPresenter implements IComicRecommendPresenter {

    private boolean isConnecting = false;
    private IComicRecommendFragment mFragment;

    private final static String NEW_COMIC = "iTabHotHtm0";
    private final static String NEW_COMIC_TITLE = "新加漫画";

    private final static String HOT_COMIC = "iTabHotHtm2";
    private final static String HOT_COMIC_TITLE = "热点漫画";

    private final static String POP_COMIC = "iTabHotHtm1";
    private final static String POP_COMIC_TITLE = "人气榜漫";

    private final static String MUST_COMIC = "iTabHotHtm3";
    private final static String MUST_COMIC_TITLE = "必看漫画";

    private final static String RECOMMEND_COMIC = "iTabHotHtm4";
    private final static String RECOMMEND_COMIC_TITLE = "漫迷推荐";

    public ComicRecommendPresenter(IComicRecommendFragment fragment) {
        this.mFragment = fragment;
    }

    public void getRecommendList() {
        /*Request request = new Request.Builder().get()
                .url(HHApplication.getInstance().getHHWebVariable().getCsite())
                .build();
        HHApplication.getInstance().getClient().newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mFragment.onFailure(e);
                        isConnecting = false;
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        List<ComicTabList> tabLists = null;
                        try {
                            final String content = new String(response.body().bytes(), "utf-8");
                            //Jsoup识别网页信息

                            tabLists = new ArrayList<>();

                            Document doc = Jsoup.parse(content);

                            //热点漫画
                            ComicTabList hotTab = getComicTabList(doc, HOT_COMIC, HOT_COMIC_TITLE);
                            tabLists.add(hotTab);

                            //新加漫画
                            ComicTabList newTab = getComicTabList(doc, NEW_COMIC, NEW_COMIC_TITLE);
                            tabLists.add(newTab);

                            //人气
                            ComicTabList popTab = getComicTabList(doc, POP_COMIC, POP_COMIC_TITLE);
                            tabLists.add(popTab);

                            //必看
                            ComicTabList mustTab = getComicTabList(doc, MUST_COMIC, MUST_COMIC_TITLE);
                            tabLists.add(mustTab);

                            //推荐
                            ComicTabList recommendTab = getComicTabList(doc, RECOMMEND_COMIC, RECOMMEND_COMIC_TITLE);
                            tabLists.add(recommendTab);

                            mFragment.onSuccess(tabLists);
                            isConnecting = false;
                        } catch (UnsupportedEncodingException e) {
                            mFragment.onFailure(e);
                            isConnecting = false;
                        }
                    }
                });*/
        HHApiProvider.getInstance().getWebContentAsyn(HHApplication.getInstance().getHHWebVariable().getCsite(),
                new NormalResponse<byte[]>() {
                    @Override
                    public void success(NormalRequest request, byte[] data) {
                        List<ComicTabList> tabLists = null;
                        try {
                            final String content = new String(data, "utf-8");
                            //Jsoup识别网页信息

                            tabLists = new ArrayList<>();

                            Document doc = Jsoup.parse(content);

                            //热点漫画
                            ComicTabList hotTab = getComicTabList(doc, HOT_COMIC, HOT_COMIC_TITLE);
                            tabLists.add(hotTab);

                            //新加漫画
                            ComicTabList newTab = getComicTabList(doc, NEW_COMIC, NEW_COMIC_TITLE);
                            tabLists.add(newTab);

                            //人气
                            ComicTabList popTab = getComicTabList(doc, POP_COMIC, POP_COMIC_TITLE);
                            tabLists.add(popTab);

                            //必看
                            ComicTabList mustTab = getComicTabList(doc, MUST_COMIC, MUST_COMIC_TITLE);
                            tabLists.add(mustTab);

                            //推荐
                            ComicTabList recommendTab = getComicTabList(doc, RECOMMEND_COMIC, RECOMMEND_COMIC_TITLE);
                            tabLists.add(recommendTab);
                            if (mFragment != null)
                                mFragment.onSuccess(tabLists);
                            isConnecting = false;
                        } catch (UnsupportedEncodingException e) {
                            if (mFragment != null)
                                mFragment.onException(e);
                            isConnecting = false;
                        }
                    }

                    @Override
                    public void fail(int errorCode, String errorMsg) {
                        if (mFragment != null)
                            mFragment.onFailure(errorCode, errorMsg);
                        isConnecting = false;
                    }
                });
    }

    private ComicTabList getComicTabList(Document doc, String divId, String title) {
        Element hotDoc = doc.select("div[id=" + divId + "]").first();
        Elements links = hotDoc.select("a[class=image_link]");
        Elements tumbs = hotDoc.select("img");
        Elements infos = hotDoc.select("li");
        List<Comic> hotComics = new ArrayList<>();
        for (int i = 0; i < links.size(); i++) {
            Comic comic = new Comic();
            comic.setTitle(links.get(i).attr("title"));
            String url = links.get(i).attr("href");
            String end = url.substring(HHApplication.getInstance()
                    .getHHWebVariable().getPre().length());
            comic.setCid(Integer.parseInt(end.split("\\.")[0]));
            comic.setThumbnailUrl(tumbs.get(i).attr("src"));
            String authorDoc = tumbs.get(i).attr("alt");
            comic.setAuthor(authorDoc.split(" - ")[1].split("20")[0]);
            comic.setComicStatus("[" + infos.get(i).text().split("\\[")[1]);
            hotComics.add(comic);
        }
        return new ComicTabList(hotComics, title);
    }

    @Override
    public boolean isConnecting() {
        return isConnecting;
    }

    @Override
    public void removeListener() {
        mFragment = null;
    }
}
