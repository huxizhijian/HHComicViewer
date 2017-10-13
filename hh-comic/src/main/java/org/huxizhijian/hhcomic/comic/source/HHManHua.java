package org.huxizhijian.hhcomic.comic.source;

import org.huxizhijian.core.util.misc.Pair;
import org.huxizhijian.hhcomic.comic.bean.Chapter;
import org.huxizhijian.hhcomic.comic.bean.Comic;
import org.huxizhijian.hhcomic.comic.parser.comic.category.CategoryStrategy;
import org.huxizhijian.hhcomic.comic.parser.comic.detail.DetailStrategy;
import org.huxizhijian.hhcomic.comic.parser.comic.rank.RankStrategy;
import org.huxizhijian.hhcomic.comic.parser.comic.recommend.RecommendStrategy;
import org.huxizhijian.hhcomic.comic.parser.comic.search.SearchGetStrategy;
import org.huxizhijian.hhcomic.comic.source.base.ComicSource;
import org.huxizhijian.hhcomic.comic.source.base.Source;
import org.huxizhijian.hhcomic.comic.type.ComicDataSourceType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;

/**
 * 汗汗漫画网站解析类
 *
 * @Author huxizhijian on 2017/10/9.
 */
public class HHManHua extends ComicSource {

    private static final String HH_NAME = "汗汗漫画";
    private static final String HH_BASE_URL = "http://www.hhmmoo.com";  //漫画主站点
    private static final String HH_COMIC_PRE = "/manhua";
    private static final String HH_SEARCH_URL = "http://ssooff.com/";   //漫画搜索网站

    private static final int SOURCE_TYPE = Source.HHManHua;

    @Override
    public String setSourceName() {
        return HH_NAME;
    }

    @Override
    public String setBaseUrl() {
        return HH_BASE_URL;
    }

    @Override
    public int getSourceType() {
        return SOURCE_TYPE;
    }

    public HHManHua() {
        // 支持的排行类别
        RANK_TYPE_MAP.put("最近更新", "/top/newrating.aspx");
        RANK_TYPE_MAP.put("最多人看", "/top/hotrating.aspx");
        RANK_TYPE_MAP.put("评分最高", "/top/toprating.aspx");
        RANK_TYPE_MAP.put("最多人评论", "/top/hoorating.aspx");
        // 支持的分类类别
        CATEGORY_TYPE_MAP.put("1", Pair.create("萌系", "http://pic.huo80.com/comicui/21301.JPG"));
        CATEGORY_TYPE_MAP.put("2", Pair.create("搞笑", "http://pic.huo80.com/comicui/28545.JPG"));
        CATEGORY_TYPE_MAP.put("3", Pair.create("格斗", "http://pic.huo80.com/comicui/28906.JPG"));
        CATEGORY_TYPE_MAP.put("4", Pair.create("科幻", "http://pic.huo80.com/comicui/30298.JPG"));
        CATEGORY_TYPE_MAP.put("5", Pair.create("剧情", "http://pic.huo80.com/comicui/30589.JPG"));
        CATEGORY_TYPE_MAP.put("6", Pair.create("侦探", "http://pic.huo80.com/upload/up200802/a104.jpg"));
        CATEGORY_TYPE_MAP.put("7", Pair.create("竞技", "http://pic.huo80.com/upload/up200806/a198.jpg"));
        CATEGORY_TYPE_MAP.put("8", Pair.create("魔法", "http://pic.huo80.com/upload/up200912/a130.jpg"));
        CATEGORY_TYPE_MAP.put("9", Pair.create("神鬼", "http://pic.huo80.com/upload/up201003/a164.jpg"));
        CATEGORY_TYPE_MAP.put("10", Pair.create("校园", "http://pic.huo80.com/comicui2/7688a.JPG"));
        CATEGORY_TYPE_MAP.put("11", Pair.create("惊栗", "http://pic.huo80.com/comicui/7556.JPG"));
        CATEGORY_TYPE_MAP.put("12", Pair.create("厨艺", "http://pic.huo80.com/comicui/27782.JPG"));
        CATEGORY_TYPE_MAP.put("13", Pair.create("伪娘", "http://pic.huo80.com/comicui/31342.JPG"));
        CATEGORY_TYPE_MAP.put("15", Pair.create("冒险", "http://pic.huo80.com/comicui/20512.JPG"));
        CATEGORY_TYPE_MAP.put("19", Pair.create("小说", "http://pic.huo80.com/comicui/31149.JPG"));
        CATEGORY_TYPE_MAP.put("20", Pair.create("港漫", "http://pic.huo80.com/upload/up200804/a195.jpg"));
        CATEGORY_TYPE_MAP.put("21", Pair.create("耽美", "http://pic.huo80.com/comicui/11616.JPG"));
        CATEGORY_TYPE_MAP.put("22", Pair.create("经典", "http://pic.huo80.com/comicui/15553.JPG"));
        CATEGORY_TYPE_MAP.put("23", Pair.create("欧美", "http://pic.huo80.com/comicui/30267.JPG"));
        CATEGORY_TYPE_MAP.put("24", Pair.create("日文", "http://pic.huo80.com/comicui/24318.JPG"));
        CATEGORY_TYPE_MAP.put("25", Pair.create("亲情", "http://pic.huo80.com/comicui/29176.JPG"));
        // 支持的推荐类别
        RECOMMEND_TYPE_MAP.put("新加漫画", "iTabHotHtm0");
        RECOMMEND_TYPE_MAP.put("热点漫画", "iTabHotHtm2");
        RECOMMEND_TYPE_MAP.put("人气榜漫", "iTabHotHtm1");
        RECOMMEND_TYPE_MAP.put("必看漫画", "iTabHotHtm3");
        RECOMMEND_TYPE_MAP.put("漫迷推荐", "iTabHotHtm4");
    }

    /**
     * 默认添加所有策略，也可以自行添加
     */
    public ComicSource defaultConfig() {
        return this
                .addStrategy(ComicDataSourceType.WEB_DETAIL, new HHDetailStrategy())
                .addStrategy(ComicDataSourceType.WEB_SEARCH, new HHSearchStrategy())
                .addStrategy(ComicDataSourceType.WEB_RANK, new HHRankStrategy())
                .addStrategy(ComicDataSourceType.WEB_RECOMMENDED, new HHRecommendStrategy())
                .addStrategy(ComicDataSourceType.WEB_CATEGORY, new HHCategoryStrategy());
    }

    /**
     * 获取Comic详情信息策略
     */
    public class HHDetailStrategy extends DetailStrategy {

        @Override
        protected String getDetailUrl(String comicId) {
            return HH_BASE_URL + HH_COMIC_PRE + comicId + ".html";
        }

        @Override
        protected Comic parseComic(byte[] data, String comicId) throws UnsupportedEncodingException {
            //转换成UTF-8
            String content = new String(data, "utf-8");
            //初始化comic
            Comic comic = new Comic();
            //添加来源
            comic.setSource(SOURCE_TYPE);
            //添加ComicID
            comic.setCid(comicId);
            //解析网页内容，将解析到的数据添加到Comic实例中
            Document doc = Jsoup.parse(content);
            Element comicInfoDiv = doc.select("div[class=product]").first();

            comic.setTitle(comicInfoDiv.getElementsByTag("h1").first().text());
            comic.setCover(comicInfoDiv.select("div[id=about_style]").first()
                    .getElementsByTag("img").first().attr("src"));

            Element about_kit = comicInfoDiv.select("div[id=about_kit]").first();
            Elements comicInfoList = about_kit.select("li");
            comicInfoList.remove(0);
            for (Element comicInfo : comicInfoList) {
                switch (comicInfo.getElementsByTag("b").first().text()) {
                    case "作者:":
                        comic.setAuthor(comicInfo.text().split(":")[1]);
                        break;
                    case "状态:":
                        String state = comicInfo.text();
                        if (state.contains("完结")) {
                            comic.setFinish(true);
                        } else if (state.contains("连载")) {
                            comic.setFinish(false);
                        }
                        break;
                    case "更新:":
                        comic.setUpdate(comicInfo.text());
                        break;
                    case "收藏:":
                        comic.setFavoriteCount(Float.valueOf(comicInfo.text()));
                        break;
                    case "评价:":
                        comic.setRate(Float.valueOf(comicInfo.getElementsByTag("span").first().text()));
                        comic.setRatePeopleCount(Integer.valueOf(comicInfo.text().split("\\(")[1].split("人")[0]));
                        break;
                    case "简介":
                        comic.setIntro(comicInfo.text());
                        break;
                }
            }
            return comic;
        }

        @Override
        protected boolean shouldNotRequestToParseChapter() {
            return false;
        }

        @Override
        protected Request buildChapterRequest(String comicId) {
            return null;
        }

        @Override
        protected List<Chapter> parseChapter(byte[] data) throws UnsupportedEncodingException {
            // 转换成UTF-8
            String content = new String(data, "utf-8");
            // 初始化Chapter
            List<Chapter> chapters = new ArrayList<>();
            Chapter chapter = null;
            // 获取到网页内容时自动完善内容
            Document doc = Jsoup.parse(content);
            // 章节目录解析
            Element volListSrc = doc.select("div[class=cVolList]").first();
            Elements tagsSrc = volListSrc.select("div[class=cVolTag]");
            Elements tagChapterSrc = volListSrc.select("ul[class=cVolUl]");

            for (int i = 0; i < tagsSrc.size(); i++) {
                Elements chaptersSrc = tagChapterSrc.get(i).select("a[class=l_s]");
                for (int j = chaptersSrc.size() - 1; j > -1; j--) {
                    //这个倒数循环把原本的倒序的章节顺序变为正序
                    String title = chaptersSrc.get(j).attr("title");
                    //地址
                    String url = chaptersSrc.get(j).attr("href");
                    chapter = new Chapter(title, url);
                    chapters.add(chapter);
                }
            }
            return chapters;
        }
    }

    /**
     * 搜索策略
     */
    public class HHSearchStrategy extends SearchGetStrategy {

        @Override
        protected String getSearchUrl(String key, int page, int size) throws UnsupportedEncodingException {
            String getKey = null;
            getKey = "?key=" + URLEncoder.encode(key, "GB2312");
            getKey += "&button=%CB%D1%CB%F7%C2%FE%BB%AD";
            return HH_SEARCH_URL + getKey;
        }

        @Override
        protected List<Comic> parseSearchResult(byte[] data) throws UnsupportedEncodingException {
            String content = null;
            content = new String(data, "gb2312");
            Document doc = Jsoup.parse(content);
            Element comicSrcs = doc.select("div[class=dSHtm]").first();
            Elements comicUrls = comicSrcs.select("div");
            comicUrls.remove(0);

            List<Comic> comics = new ArrayList<>();
            Comic comic = null;

            for (int i = 0; i < comicUrls.size(); i++) {
                comic = new Comic();
                comic.setSource(SOURCE_TYPE);
                Element comicSrc = comicUrls.get(i).select("a").first();
                String url = comicSrc.attr("href");
                String[] urlSplit = url.split("/");
                String end = urlSplit[urlSplit.length - 1];
                comic.setCid(end.split("\\.")[0]);
                comic.setTitle(comicSrc.text());
                Element imgUrl = comicUrls.get(i).select("img").first();
                comic.setCover(imgUrl.attr("src"));
                Elements desc = comicUrls.get(i).getElementsByTag("br");
                comic.setIntro(desc.get(2).text());
                comics.add(comic);
            }
            return comics;
        }

        @Override
        protected int getPageCount(byte[] data) {
            // 由于搜索网站的限制，永远只有一页
            return 1;
        }
    }

    /**
     * 排行策略
     */
    public class HHRankStrategy extends RankStrategy {

        @Override
        protected String getRankUrl(String rankType, int page, int size) {
            String urlKey = RANK_TYPE_MAP.get(rankType);
            return HH_BASE_URL + urlKey;
        }

        @Override
        protected int getPageCount(byte[] data) {
            // 排行会在一页中展示
            return 1;
        }

        @Override
        protected List<Comic> parseRankComics(byte[] data) throws UnsupportedEncodingException {
            String content = new String(data, "utf-8");
            Document doc = Jsoup.parse(content);
            Elements comicSrcs = doc.select("div[class=cComicItem]");
            List<Comic> comics = new ArrayList<>();
            for (Element comicSrc : comicSrcs) {
                Comic comic = new Comic();
                String comicUrl = comicSrc.select("a").first().attr("href");
                String end = comicUrl.substring(HH_COMIC_PRE.length());
                comic.setSource(SOURCE_TYPE);
                comic.setCid(end.split("\\.")[0]);
                comic.setCover(comicSrc.select("img").first().attr("src"));
                comic.setTitle(comicSrc.select("span[class=cComicTitle]").first().text());
                comic.setAuthor(comicSrc.select("span[class=cComicAuthor").first().text());
                comic.setIntro(comicSrc.select("span[class=cComicRating").first().text());
                comics.add(comic);
            }
            return comics;
        }
    }

    /**
     * 首页推荐策略
     */
    public class HHRecommendStrategy extends RecommendStrategy {

        @Override
        protected String getRecommendUrl(String recommendType, int page, int size) {
            // 首页推荐只需要首页
            return HH_BASE_URL;
        }

        @Override
        protected int getPageCount(byte[] data) {
            // 首页推荐不能翻页
            return 1;
        }

        @Override
        protected List<Comic> parseRecommendComics(byte[] data, String recommendType) throws UnsupportedEncodingException {
            String content = new String(data, "utf-8");
            Document doc = Jsoup.parse(content);
            String urlKey = RECOMMEND_TYPE_MAP.get(recommendType);
            return getComicList(doc, urlKey);
        }

        private List<Comic> getComicList(Document doc, String divId) {
            Element hotDoc = doc.select("div[id=" + divId + "]").first();
            Elements links = hotDoc.select("a[class=image_link]");
            Elements tumbs = hotDoc.select("img");
            Elements infos = hotDoc.select("li");
            List<Comic> hotComics = new ArrayList<>();
            for (int i = 0; i < links.size(); i++) {
                Comic comic = new Comic();
                comic.setTitle(links.get(i).attr("title"));
                String url = links.get(i).attr("href");
                String end = url.substring(HH_COMIC_PRE.length());
                comic.setSource(SOURCE_TYPE);
                comic.setCid(end.split("\\.")[0]);
                comic.setCover(tumbs.get(i).attr("src"));
                String authorDoc = tumbs.get(i).attr("alt");
                comic.setAuthor(authorDoc.split(" - ")[1].split("20")[0]);
                comic.setIntro("[" + infos.get(i).text().split("\\[")[1]);
                hotComics.add(comic);
            }
            return hotComics;
        }
    }

    /**
     * 分类策略
     */
    public class HHCategoryStrategy extends CategoryStrategy {

        @Override
        protected String getCategoryUrl(String categoryType, int page, int size) {
            return HH_BASE_URL + String.format(Locale.CHINESE, "/comic/class_%s/%d.html", categoryType, page);
        }

        @Override
        protected int getPageCount(byte[] data) throws UnsupportedEncodingException {
            int pageCount = 1;
            String content = new String(data, "utf-8");
            Document doc = Jsoup.parse(content);
            Element pageInfo = doc.select("div[class=cComicPageChange]").first();
            Elements pages = pageInfo.select("a");
            for (Element page : pages) {
                if (page.text().equals("尾页")) {
                    String pageSize = page.attr("href").split("\\.")[0];
                    if (pageSize.matches("/[^']*")) {
                        pageSize = pageSize.split("/")[3];
                    }
                    pageCount = Integer.valueOf(pageSize);
                }
            }
            return pageCount;
        }

        @Override
        protected List<Comic> parseRecommend(byte[] data) throws UnsupportedEncodingException {
            String content = new String(data, "utf-8");
            Document doc = Jsoup.parse(content);
            Element comicsSrc = doc.select("div[class=cComicList]").first();
            Elements urlsSrc = comicsSrc.select("a");
            Elements imgsSrc = comicsSrc.select("img");
            List<Comic> comics = new ArrayList<>();
            for (int i = 0; i < urlsSrc.size(); i++) {
                Comic comic = new Comic();
                comic.setSource(SOURCE_TYPE);
                comic.setTitle(urlsSrc.get(i).attr("title"));
                String url = urlsSrc.get(i).attr("href");
                String end = url.substring(HH_COMIC_PRE.length());
                comic.setCid(end.split("\\.")[0]);
                comic.setCover(imgsSrc.get(i).attr("src"));
                comics.add(comic);
            }
            return comics;
        }
    }

}
