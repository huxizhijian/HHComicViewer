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
import org.huxizhijian.hhcomicviewer.presenter.IClassifiesShowPresenter;
import org.huxizhijian.hhcomicviewer.presenter.viewinterface.IClassifiesShowActivity;
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
 * @author wei
 * @date 2017/1/20
 */

public class ClassifiesShowPresenterImpl implements IClassifiesShowPresenter {

    private IClassifiesShowActivity mActivity;

    public ClassifiesShowPresenterImpl(IClassifiesShowActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public void getComicList(String url, int page) {
        HHApiProvider.getInstance()
                .getWebContentAsyn(HHApplication.getInstance().getHHWebVariable().getCsite() + url + page + ".html"
                        , new NormalResponse<byte[]>() {
                            @Override
                            public void success(NormalRequest request, byte[] data) {
                                String content = null;
                                try {
                                    content = new String(data, "utf-8");
                                    Document doc = Jsoup.parse(content);

                                    int maxPage = 1;
                                    Element pageInfo = doc.select("div[class=cComicPageChange]").first();
                                    Elements pages = pageInfo.select("a");
                                    for (Element page : pages) {
                                        if (page.text().equals("尾页")) {
                                            String pageSize = page.attr("href").split("\\.")[0];
                                            if (pageSize.matches("/[^']*")) {
                                                pageSize = pageSize.split("/")[3];
                                            }
                                            maxPage = Integer.valueOf(pageSize);
                                        }
                                    }

                                    Element comicsSrc = doc.select("div[class=cComicList]").first();
                                    Elements urlsSrc = comicsSrc.select("a");
                                    Elements imgsSrc = comicsSrc.select("img");
                                    List<Comic> comics = new ArrayList<>();
                                    for (int i = 0; i < urlsSrc.size(); i++) {
                                        Comic comic = new Comic();
                                        comic.setTitle(urlsSrc.get(i).attr("title"));
                                        String url = urlsSrc.get(i).attr("href");
                                        String end = url.substring(HHApplication.getInstance()
                                                .getHHWebVariable().getPre().length());
                                        comic.setCid(Integer.parseInt(end.split("\\.")[0]));
                                        comic.setThumbnailUrl(imgsSrc.get(i).attr("src"));
                                        comics.add(comic);
                                    }
                                    if (mActivity != null)
                                        mActivity.onSuccess(maxPage, comics);
                                } catch (UnsupportedEncodingException e) {
                                    if (mActivity != null)
                                        mActivity.onException(e);
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void fail(int errorCode, String errorMsg) {
                                if (mActivity != null)
                                    mActivity.onFailure(errorCode, errorMsg);
                            }
                        });
    }

    @Override
    public void removeListener() {
        mActivity = null;
    }

}
