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

package org.huxizhijian.hhcomicviewer.utils;

import android.support.annotation.WorkerThread;

import org.huxizhijian.hhcomicviewer.app.HHApplication;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 分析单卷里的图片列表
 *
 * @author huxizhijian
 * @date 2016/8/26.
 */
public class ParsePicUrlList {

    /**
     * 获取图片网址列表
     *
     * @param serverId 服务器id
     * @param content  第一页返回content，UTF-8编码
     * @return pic url list
     */
    @WorkerThread
    public static ArrayList<String> scanPicInPage(int cid, long chid, int serverId, String content) throws IOException {
        // 使用Jsoup获取DOM的各种元素
        Document doc = Jsoup.parse(content);
        // 首先解析有多少页
        int picCount = getPicCount(doc);

        // 进行每页的图片网址获取
        OkHttpClient okHttpClient = HHApplication.getInstance().getClient();
        Request.Builder builder = new Request.Builder().get();
        Request request = null;
        Response response = null;
        Document picDoc = null;
        ArrayList<String> picUrlList = new ArrayList<>(picCount);
        String encodeKey = HHApplication.getInstance().getHHWebVariable().getEncodeKey();

        // 循环访问每页，提取图片地址
        for (int i = 0; i < picCount; i++) {
            if (i + 1 == 1) {
                // 不需要获取第一页内容
                picDoc = doc;
            } else {
                request = builder
                        .url(CommonUtils.getChapterUrl(cid, chid, serverId, i + 1))
                        .build();
                response = okHttpClient.newCall(request).execute();
                String picPageContent = new String(response.body().bytes(), StandardCharsets.UTF_8);
                picDoc = Jsoup.parse(picPageContent);
            }
            // 解析并获取网址
            String picUrl = parsePicUrl(picDoc, serverId, encodeKey);
            picUrlList.add(picUrl);
        }
        return picUrlList;
    }

    private static int getPicCount(Document doc) {
        Element pageHtm = doc.getElementById("iPageHtm");
        Elements pageLink = pageHtm.getElementsByTag("a");
        // 注意，页码是以1开始的，所以最后一页的页码就是总页数
        return Integer.valueOf(pageLink.get(pageLink.size() - 1).text());
    }

    private static String parsePicUrl(Document picDoc, int serverId, String encodeKey) {
        Element body = picDoc.getElementById("iBodyQ");
        Element img = body.getElementsByTag("img").first();
        String cypherText = img.attr("name");
        String serverUrl = null;
        if (serverId < 10) {
            serverUrl = HHApplication.getInstance().getHHWebVariable().getPicServer() + "0" + serverId + "/";
        } else {
            serverUrl = HHApplication.getInstance().getHHWebVariable().getPicServer() + serverId + "/";
        }
        // 进行密文的解码
        String url = unsuan(cypherText, serverId, encodeKey);
        return serverUrl + url;
    }

    /**
     * 你要问我为啥是unsuan，网页的js就是这么写的
     *
     * @param cypherText
     * @param serverId
     * @param encodeKey
     * @return
     */
    private static String unsuan(String cypherText, int serverId, String encodeKey) {
        String x = cypherText.substring(cypherText.length() - 1);
        encodeKey = "abcdefghijklmnopqrstuvwxyz";
        int xi = encodeKey.indexOf(x) + 1;
        String sk = cypherText.substring(cypherText.length() - xi - 12, cypherText.length() - xi - 1);
        cypherText = cypherText.substring(0, cypherText.length() - xi - 12);
        String k = sk.substring(0, sk.length() - 1);
        String f = sk.substring(sk.length() - 1);
        for (int i = 0; i < k.length(); i++) {
            cypherText = cypherText.replaceAll(k.substring(i, i + 1), String.valueOf(i));
        }
        String[] ss = cypherText.split(f);
        cypherText = "";
        StringBuilder fineUrl = new StringBuilder();
        for (String s : ss) {
            fineUrl.append((char) (Integer.valueOf(s).intValue()));
        }
        return fineUrl.toString();
    }

//    private static String scanPicListUrl(String contents) {                                        //分析获取的网址，把密文PicListUrl提取出来
//        String patternString
//                = "var PicListUrl = \"([^\"]*)\"";
//        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(contents);
//        String picList = "";
//        if (matcher.find()) {
//            picList = matcher.group(1);
//        }
//        return picList;
//    }
//
//    private static ArrayList<String> parsePicUrl(String picListUrl, String keyWord, int serverId) {           //分析隐藏的图片网址,根据网站的js代码获取网址
//        String serverUrl = null;
//        if (serverId < 10) {
//            serverUrl = HHApplication.getInstance().getHHWebVariable().getPicServer() + "0" + serverId + "/";
//        } else {
//            serverUrl = HHApplication.getInstance().getHHWebVariable().getPicServer() + serverId + "/";
//        }
//        String k = keyWord.substring(0, keyWord.length() - 1);
//        String f = keyWord.substring(keyWord.length() - 1);
//        char charNum = '0';
//        for (int i = 0; i < k.length(); i++) {
//            picListUrl = picListUrl.replaceAll(k.substring(i, i + 1), String.valueOf(charNum));
//            charNum++;
//        }
//        String[] result = picListUrl.split(f);
//        StringBuilder fi = new StringBuilder();
//        for (int i = 0; i < result.length; i++) {
//            fi.append(String.valueOf((char) (Integer.valueOf(result[i]).intValue())));
//        }
//        String[] jpgUrl = fi.toString().split("\\|");
//        ArrayList<String> picList = new ArrayList<>();
//        for (int i = 0; i < jpgUrl.length; i++) {
//            picList.add(serverUrl + jpgUrl[i]);
//        }
//        return picList;
//    }
}
