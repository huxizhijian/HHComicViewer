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
import org.huxizhijian.hhcomicviewer.model.Comic;
import org.huxizhijian.hhcomicviewer.presenter.IRankDetailsPresenter;
import org.huxizhijian.hhcomicviewer.presenter.viewinterface.IRankDetailsFragment;
import org.huxizhijian.hhcomicviewer.utils.HHApiProvider;
import org.huxizhijian.sdk.network.service.NormalRequest;
import org.huxizhijian.sdk.network.service.NormalResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei on 2017/1/20.
 */

public class RankDetailsPresenterImpl implements IRankDetailsPresenter {

    private IRankDetailsFragment mFragment;

    public RankDetailsPresenterImpl(IRankDetailsFragment fragment) {
        this.mFragment = fragment;
    }

    @Override
    public void getRankList(String url) {
        HHApiProvider.getInstance().getWebContentAsyn(HHApplication.getInstance()
                        .getHHWebVariable().getCsite() + url,
                new NormalResponse<byte[]>() {
                    @Override
                    public void success(NormalRequest request, byte[] data) {
                        String content = null;
                        try {
                            content = new String(data, "utf-8");
                            Document doc = Jsoup.parse(content);
                            Elements comicSrcs = doc.select("div[class=cComicItem]");
                            List<Comic> comics = new ArrayList<>();
                            for (Element comicSrc : comicSrcs) {
                                Comic comic = new Comic();
                                String comicUrl = comicSrc.select("a").first().attr("href");
                                String end = comicUrl.substring(HHApplication.getInstance()
                                        .getHHWebVariable().getPre().length());
                                comic.setCid(Integer.parseInt(end.split("\\.")[0]));
                                comic.setThumbnailUrl(comicSrc.select("img").first().attr("src"));
                                comic.setTitle(comicSrc.select("span[class=cComicTitle]").first().text());
                                comic.setAuthor(comicSrc.select("span[class=cComicAuthor").first().text());
                                comic.setComicStatus(comicSrc.select("span[class=cComicRating").first().text());
                                comics.add(comic);
                            }
                            if (mFragment != null) {
                                mFragment.onSuccess(comics);
                            }
                        } catch (UnsupportedEncodingException | Selector.SelectorParseException e) {
                            e.printStackTrace();
                            if (mFragment != null) {
                                mFragment.onException(e);
                            }
                        }
                    }

                    @Override
                    public void fail(int errorCode, String errorMsg) {
                        if (mFragment != null) {
                            mFragment.onFailure(errorCode, errorMsg);
                        }
                    }
                });
    }

    @Override
    public void removeListener() {
        mFragment = null;
    }
}
