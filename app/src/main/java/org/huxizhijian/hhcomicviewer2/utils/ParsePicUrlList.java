package org.huxizhijian.hhcomicviewer2.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 分析单卷里的图片列表
 * Created by wei on 2016/8/26.
 */
public class ParsePicUrlList {

    public static ArrayList<String> scanPicInPage(String url, String contents) {                        //获取图片网址列表
        String picListUrl = scanPicListUrl(contents);
        ArrayList<String> picList = new ArrayList<>();
        if (picListUrl == "") {
            return picList;
        }
        picList = parsePicUrl(picListUrl, Constants.ENCODE_KEY, url);
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

    private static ArrayList<String> parsePicUrl(String picListUrl, String keyWord, String url) {           //分析隐藏的图片网址,根据网站的js代码获取网址
        String[] num = url.split("s=");
        if (num.length < 2) {
            ArrayList<String> fail = new ArrayList<>();
            fail.add("");
            return fail;
        }
        String number;
        if (num[1].length() == 1) {
            number = "0" + num[1];
        } else {
            number = num[1];
        }
        String serverUrl = Constants.PIC_SERVICE_URL + number + "/";
        String k = keyWord.substring(0, keyWord.length() - 1);
        String f = keyWord.substring(keyWord.length() - 1);
        char charNum = '0';
        for (int i = 0; i < k.length(); i++) {
            picListUrl = picListUrl.replaceAll(k.substring(i, i + 1), String.valueOf(charNum));
            charNum++;
        }
        String[] result = picListUrl.split(f);
        String fi = "";
        for (int i = 0; i < result.length; i++) {
            fi += String.valueOf((char) (Integer.valueOf(result[i]).intValue()));
        }
        String[] jpgUrl = fi.split("\\|");
        ArrayList<String> picList = new ArrayList<>();
        for (int i = 0; i < jpgUrl.length; i++) {
            picList.add(serverUrl + jpgUrl[i]);
        }
        return picList;
    }
}
