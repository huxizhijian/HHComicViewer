package org.huxizhijian.hhcomicviewer2.utils;

/**
 * 常量
 * Created by wei on 2016/8/20.
 */

public class Constants {
    public static final int THEME_COLOR = 0xffff8c00; //主题色调
    public static final int COLOR_BLACK = 0xaa000000; //黑色
    public static final String HHCOMIC_URL = "http://www.hhcomic.cc/"; //主站网址
    public static final String ENCODE_KEY = "tahfcioewrm"; //解码关键字
    public static final String PIC_SERVICE_URL = "http://64.140.165.115:9393/dm"; //图片服务器
    public static final String SEARCH_URL = "http://ssooff.com/"; //搜索网站
    public static final String ACTION_CLASSIFIES = "action_classifies"; //标记为获取分类列表行
    public static final String NO_NETWORK = "没有网络!";

    public static final String CLASSIFIES_CONTENT = "<div id=\"menu\">\n" +
            "        <a href=\"/hhlist/1/\" class=\"linkb\">萌萌</a> | <a href=\"/hhlist/2/\" class=\"linkb\">搞笑</a> | <a href=\"/hhlist/3/\" class=\"linkb\">格斗</a> | <a href=\"/hhlist/4/\" class=\"linkb\">科幻</a> | <a href=\"/hhlist/5/\" class=\"linkb\">剧情</a> | <a href=\"/hhlist/6/\" " +
            "class=\"linkb\">侦探</a> | <a href=\"/hhlist/7/\" class=\"linkb\">竞技</a> | <a href=\"/hhlist/8/\" class=\"linkb\">魔法</a> | <a href=\"/hhlist/9/\" class=\"linkb\">神鬼</a> | <a href=\"/hhlist/10/\" class=\"linkb\">校园</a> | <a href=\"/hhlist/11/\" class=\"linkb\">惊栗</a> " +
            "| <a href=\"/hhlist/12/\" class=\"linkb\">厨艺</a> | <a href=\"/hhlist/13/\" class=\"linkb\">百合</a> | <a href=\"/hhlist/14/\" class=\"linkb\">图片</a> | <a href=\"/hhlist/15/\" class=\"linkb\">冒险</a> | <a href=\"/hhlist/19/\" class=\"linkb\">小说</a> | <a href=\"/hhlist/20/\" class=\"linkb\">港漫</a> "
            + "| <a href=\"/hhlist/21/\" class=\"linkb\"><strong>耽美</strong></a> | <a href=\"/hhlist/22/\" class=\"linkb\">经典</a> | <a href=\"/hhlist/23/\" class=\"linkb\">欧美</a> | <a href=\"/hhlist/24/\" class=\"linkb\">日文</a>  </div>\n";

    public static final int DOWNLOAD_START = 0x0;
    public static final int DOWNLOAD_DOWNLOADING = 0x1;
    public static final int DOWNLOAD_FINISHED = 0x2;
    public static final int DOWNLOAD_PAUSE = 0x3;
    public static final int DOWNLOAD_ERROR = 0x4;
}
