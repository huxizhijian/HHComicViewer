package org.huxizhijian.hhcomicviewer2.persenter.implpersenter;

import org.huxizhijian.hhcomicviewer2.HHApplication;
import org.huxizhijian.hhcomicviewer2.model.Comic;
import org.huxizhijian.hhcomicviewer2.persenter.IClassifiesShowPresenter;
import org.huxizhijian.hhcomicviewer2.persenter.viewinterface.IClassifiesShowActivity;
import org.huxizhijian.hhcomicviewer2.utils.HHApiProvider;
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
 * Created by wei on 2017/1/20.
 */

public class ClassifiesShowPresenterImpl implements IClassifiesShowPresenter {

    private IClassifiesShowActivity mActivity;

    public ClassifiesShowPresenterImpl(IClassifiesShowActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public void getComicList(String url, int page) {
        /*Request request = new Request.Builder().get()
                .url(HHApplication.getInstance().getHHWebVariable().getCsite() + url + page + ".html")
                .build();
        HHApplication.getInstance().getClient().newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mActivity.onException(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String content = new String(response.body().bytes(), "utf-8");
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
//                                System.out.println(mPageSize);
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

                        mActivity.onSuccess(maxPage, comics);
                    }
                });*/

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
//                                System.out.println(mPageSize);
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

                                    mActivity.onSuccess(maxPage, comics);
                                } catch (UnsupportedEncodingException e) {
                                    mActivity.onException(e);
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void fail(int errorCode, String errorMsg) {
                                mActivity.onFailure(errorCode, errorMsg);
                            }
                        });
    }

}
