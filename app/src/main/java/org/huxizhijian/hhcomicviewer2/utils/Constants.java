package org.huxizhijian.hhcomicviewer2.utils;

/**
 * 常量
 * Created by wei on 2016/8/20.
 */

public class Constants {
    public static final int THEME_COLOR = 0xffff8c00; //主题色调
    public static final String HHCOMIC_URL = "http://www.hhcomic.cc/"; //主站网址
    public static final String ENCODE_KEY = "tahfcioewrm"; //解码关键字
    public static final String PIC_SERVICE_URL = "http://64.140.165.115:9393/dm"; //图片服务器
    public static final String SEARCH_URL = "http://ssooff.com/"; //搜索网站
    public static final String ACTION_SEARCH = "action_search"; //标记为搜索行动
    public static final String ACTION_CLASSIFIES = "action_classifies"; //标记为获取分类列表行动

    public static final int DOWNLOAD_START = 0x0;
    public static final int DOWNLOAD_DOWNLOADING = 0x1;
    public static final int DOWNLOAD_FINISHED = 0x2;
    public static final int DOWNLOAD_PAUSE = 0x3;
    public static final int DOWNLOAD_ERROR = 0x4;
}
