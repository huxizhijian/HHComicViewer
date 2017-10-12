package org.huxizhijian.hhcomic.comic.source;

import org.huxizhijian.hhcomic.comic.bean.Chapter;
import org.huxizhijian.hhcomic.comic.bean.Comic;
import org.huxizhijian.hhcomic.comic.parser.comic.category.CategoryStrategy;
import org.huxizhijian.hhcomic.comic.parser.comic.detail.DetailStrategy;
import org.huxizhijian.hhcomic.comic.parser.comic.rank.RankStrategy;
import org.huxizhijian.hhcomic.comic.parser.comic.recommend.RecommendStrategy;
import org.huxizhijian.hhcomic.comic.parser.comic.search.SearchGetStrategy;
import org.huxizhijian.hhcomic.comic.source.base.ComicSource;
import org.huxizhijian.hhcomic.comic.source.base.Source;
import org.huxizhijian.hhcomic.comic.type.CategoryType;
import org.huxizhijian.hhcomic.comic.type.ComicDataSourceType;
import org.huxizhijian.hhcomic.comic.type.RankType;
import org.huxizhijian.hhcomic.comic.type.RecommendType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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
        RANK_TYPE_MAP.put(RankType.HH_NEW_RATING, new TypeContent("最近更新", "/top/newrating.aspx"));
        RANK_TYPE_MAP.put(RankType.HH_TOP_READER, new TypeContent("最多人看", "/top/hotrating.aspx"));
        RANK_TYPE_MAP.put(RankType.HH_TOP_RATING, new TypeContent("评分最高", "/top/toprating.aspx"));
        RANK_TYPE_MAP.put(RankType.HH_HOT_COMMIT, new TypeContent("最多人评论", "/top/hoorating.aspx"));
        // 支持的分类类别
        CATEGORY_TYPE_MAP.put(CategoryType.HH_MENGXI, new TypeContent("萌系", "1"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_GAOXIAO, new TypeContent("搞笑", "2"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_GEDOU, new TypeContent("格斗", "3"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_KEHUAN, new TypeContent("科幻", "4"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_JUQING, new TypeContent("剧情", "5"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_ZHENTAN, new TypeContent("侦探", "6"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_JINGJI, new TypeContent("竞技", "7"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_MOFA, new TypeContent("魔法", "8"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_SHENGUI, new TypeContent("神鬼", "9"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_XIAOYUAN, new TypeContent("校园", "10"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_JINGSONG, new TypeContent("惊栗", "11"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_CHUYI, new TypeContent("厨艺", "12"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_WEINIANG, new TypeContent("伪娘", "13"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_MAOXIAN, new TypeContent("冒险", "15"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_XIAOSHUO, new TypeContent("小说", "19"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_GANGMAN, new TypeContent("港漫", "20"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_DANMEI, new TypeContent("耽美", "21"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_JINGDIAN, new TypeContent("经典", "22"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_OUMEI, new TypeContent("欧美", "23"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_RIWEN, new TypeContent("日文", "24"));
        CATEGORY_TYPE_MAP.put(CategoryType.HH_QINQING, new TypeContent("亲情", "25"));
        // 支持的推荐类别
        RECOMMEND_TYPE_MAP.put(RecommendType.HH_NEW_COMIC, new TypeContent("新加漫画", "iTabHotHtm0"));
        RECOMMEND_TYPE_MAP.put(RecommendType.HH_HOT_COMIC, new TypeContent("热点漫画", "iTabHotHtm2"));
        RECOMMEND_TYPE_MAP.put(RecommendType.HH_POP_COMIC, new TypeContent("人气榜漫", "iTabHotHtm1"));
        RECOMMEND_TYPE_MAP.put(RecommendType.HH_MUST_COMIC, new TypeContent("必看漫画", "iTabHotHtm3"));
        RECOMMEND_TYPE_MAP.put(RecommendType.HH_RECOMMEND_COMIC, new TypeContent("漫迷推荐", "iTabHotHtm4"));
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
        protected String getRankUrl(Enum<RankType> rankType, int page, int size) {
            TypeContent content = RANK_TYPE_MAP.get(rankType);
            return HH_BASE_URL + content.urlKey;
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
        protected String getRecommendUrl(Enum<RecommendType> recommendType, int page, int size) {
            // 首页推荐只需要首页
            return HH_BASE_URL;
        }

        @Override
        protected int getPageCount(byte[] data) {
            // 首页推荐不能翻页
            return 1;
        }

        @Override
        protected List<Comic> parseRecommendComics(byte[] data, Enum<RecommendType> recommendType) throws UnsupportedEncodingException {
            String content = new String(data, "utf-8");
            Document doc = Jsoup.parse(content);
            TypeContent typeContent = RECOMMEND_TYPE_MAP.get(recommendType);
            return getComicList(doc, typeContent.urlKey);
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
     * 分页策略
     */
    public class HHCategoryStrategy extends CategoryStrategy {

        @Override
        protected String getCategoryUrl(Enum<CategoryType> categoryType, int page, int size) {
            TypeContent typeContent = CATEGORY_TYPE_MAP.get(categoryType);
            return HH_BASE_URL + "/comic/class_" + typeContent.urlKey + "/" + page + ".html";
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
