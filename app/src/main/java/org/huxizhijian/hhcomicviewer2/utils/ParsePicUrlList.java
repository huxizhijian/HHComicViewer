/*
 * Copyright 2016 huxizhijian
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

package org.huxizhijian.hhcomicviewer2.utils;

import org.huxizhijian.hhcomicviewer2.app.HHApplication;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 分析单卷里的图片列表
 * Created by wei on 2016/8/26.
 */
public class ParsePicUrlList {

    public static ArrayList<String> scanPicInPage(int serverId, String contents) {                        //获取图片网址列表
        String picListUrl = scanPicListUrl(contents);
        ArrayList<String> picList = new ArrayList<>();
        if (picListUrl == "") {
            return picList;
        }
        picList = parsePicUrl(picListUrl, HHApplication.getInstance().getHHWebVariable().getEncodeKey(), serverId);
        return picList;
    }

    private static String scanPicListUrl(String contents) {                                        //分析获取的网址，把密文PicListUrl提取出来
        String patternString
                = "var PicListUrl = \"([^\"]*)\"";
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(contents);
        String picList = "";
        if (matcher.find()) {
            picList = matcher.group(1);
        }
        return picList;
    }

    private static ArrayList<String> parsePicUrl(String picListUrl, String keyWord, int serverId) {           //分析隐藏的图片网址,根据网站的js代码获取网址
        String serverUrl = null;
        if (serverId < 10) {
            serverUrl = HHApplication.getInstance().getHHWebVariable().getPicServer() + "0" + serverId + "/";
        } else {
            serverUrl = HHApplication.getInstance().getHHWebVariable().getPicServer() + serverId + "/";
        }
        String k = keyWord.substring(0, keyWord.length() - 1);
        String f = keyWord.substring(keyWord.length() - 1);
        char charNum = '0';
        for (int i = 0; i < k.length(); i++) {
            picListUrl = picListUrl.replaceAll(k.substring(i, i + 1), String.valueOf(charNum));
            charNum++;
        }
        String[] result = picListUrl.split(f);
        StringBuilder fi = new StringBuilder();
        for (int i = 0; i < result.length; i++) {
            fi.append(String.valueOf((char) (Integer.valueOf(result[i]).intValue())));
        }
        String[] jpgUrl = fi.toString().split("\\|");
        ArrayList<String> picList = new ArrayList<>();
        for (int i = 0; i < jpgUrl.length; i++) {
            picList.add(serverUrl + jpgUrl[i]);
        }
        return picList;
    }
}
