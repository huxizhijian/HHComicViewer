/*
 * Copyright 2016-2018 huxizhijian
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

package org.huxizhijian.hhcomic;

import org.huxizhijian.hhcomic.db.entity.Comic;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void test() throws IOException {
        Request request = new Request.Builder()
                .url("http://www.hheehh.com/comic/?act=search&st=")
                .get()
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String html = new String(response.body().bytes(), "utf-8");
        Document doc = Jsoup.parse(html);
        System.out.println(parseComicList(doc));
    }

    private List<Comic> parseComicList(Document doc) {
        // 解析本页面comic列表
        if (!doc.hasClass("cComicList")) {
            return null;
        }
        Elements comicListElements = doc.getElementsByAttributeValue("class", "cComicList")
                .first().getElementsByTag("li");
        List<Comic> comicList = new ArrayList<>();
        Comic comic;
        for (Element comicElement : comicListElements) {
            comic = new Comic();
            comic.setTitle(comicElement.getElementsByTag("a").first().attr("title"));
            comic.setComicId(getComicIdFromHref(comicElement.getElementsByTag("a")
                    .first().attr("href")));
            comic.setCover(comicElement.getElementsByTag("img").first().attr("src"));
            comicList.add(comic);
        }
        return comicList;
    }

    private String getComicIdFromHref(String href) {
        return href.split("\\.")[0].substring(7);
    }

    @Test
    public void encodeTest() throws IOException {
        Request request = new Request.Builder()
                .url("http://www.hheehh.com/page201537/1.html?s=6&d=0")
                .get()
                .build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String html = new String(response.body().bytes(), "utf-8");
        Document doc = Jsoup.parse(html);
        Element imgElement = doc.getElementsByTag("img").first();
        String cipherText = imgElement.attr("name");
        System.out.println(getImg(doc, cipherText));
    }

    private String getImg(Document doc, String cipherText) {
        Element serviceListElement = doc.getElementsByAttributeValue("id", "hdDomain").first();
        String serviceListString = serviceListElement.attr("value");
        String[] service = serviceListString.split("\\|");
        return service[0] + encode(cipherText);
    }

    private String encode(String cipherText) {
        String x = cipherText.substring(cipherText.length() - 1);
        String key = "abcdefghijklmnopqrstuvwxyz";
        int xi = key.indexOf(x) + 1;
        String sk = cipherText.substring(cipherText.length() - xi - 12, cipherText.length() - xi - 1);
        cipherText = cipherText.substring(0, cipherText.length() - xi - 12);
        String k = sk.substring(0, sk.length() - 1);
        String f = sk.substring(sk.length() - 1);
        for (int i = 0; i < k.length(); i++) {
            cipherText = cipherText.replace(k.substring(i, i + 1), String.valueOf(i));
        }
        String[] ss = cipherText.split(f);
        StringBuilder string = new StringBuilder();
        for (String unicode : ss) {
            int data = Integer.parseInt(unicode);
            string.append((char) data);
        }
        return string.toString();
    }

    @Test
    public void testRxJava() throws InterruptedException {
        // 这证明了我们不需要再RxJava的事件产生处进行try-catch异常
        Flowable.create(emitter -> {
            Request request = new Request.Builder()
                    .url("http://www.baidu.com/")
                    .get()
                    .build();
            OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                throw new NullPointerException("null poi");
            }
            emitter.onNext(response.body().string());
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .subscribe(System.out::println, throwable -> {
                    System.out.println("onError");
                    throwable.printStackTrace();
                });
        Thread.sleep(2500);
    }
}