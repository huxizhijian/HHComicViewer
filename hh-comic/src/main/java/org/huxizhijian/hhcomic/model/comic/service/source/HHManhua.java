package org.huxizhijian.hhcomic.model.comic.service.source;

import android.text.TextUtils;

import org.huxizhijian.annotations.SourceImpl;
import org.huxizhijian.hhcomic.model.comic.service.bean.Category;
import org.huxizhijian.hhcomic.model.comic.service.bean.ComicListBean;
import org.huxizhijian.hhcomic.model.comic.service.bean.FilterList;
import org.huxizhijian.hhcomic.model.comic.service.bean.Sort;
import org.huxizhijian.hhcomic.model.comic.service.bean.result.ChapterImage;
import org.huxizhijian.hhcomic.model.comic.service.bean.result.ComicResultList;
import org.huxizhijian.hhcomic.model.comic.db.entity.Chapter;
import org.huxizhijian.hhcomic.model.comic.db.entity.Comic;
import org.huxizhijian.hhcomic.model.comic.service.source.base.Source;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

/**
 * 该源的页码从1开始
 *
 * @author huxizhijian
 * @date 2018/10/1
 */
@SourceImpl(id = HHManhua.SOURCE_KEY, name = HHManhua.SOURCE_NAME)
public class HHManhua extends Source {

    static final String SOURCE_KEY = "HHManhua";

    static final String SOURCE_NAME = "汗汗漫画";

    private static final String BASE_URL = "http://www.hheehh.com";

    /**
     * comic id
     */
    private static final String COMIC_LIST_URL = BASE_URL + "/comic/%s.html";
    /**
     * chapter id, page, service id
     */
    private static final String CHAPTER_URL = BASE_URL + "/page%s/%d.html?s=%s";
    /**
     * comic id
     */
    private static final String COMIC_INFO_URL = BASE_URL + "/manhua%s.html";
    /**
     * search keyword
     */
    private static final String SEARCH_URL = BASE_URL + "/comic/?act=search&st=%s";

    public HHManhua() throws IOException {
        super();
    }

    @Override
    public String getSourceName() {
        return SOURCE_NAME;
    }

    @Override
    public String getSourceKey() {
        return SOURCE_KEY;
    }

    @Override
    protected void initCategoryList(Category.ListBuilder listBuilder) {
        listBuilder
                .addCategory("漫画目录", "")
                .addCategory("萌系", "class_1")
                .addCategory("搞笑", "class_2")
                .addCategory("格斗", "class_3")
                .addCategory("科幻", "class_4")
                .addCategory("剧情", "class_5")
                .addCategory("侦探", "class_6")
                .addCategory("竞技", "class_7")
                .addCategory("魔法", "class_8")
                .addCategory("神鬼", "class_9")
                .addCategory("校园", "class_10")
                .addCategory("惊栗", "class_12")
                .addCategory("厨艺", "class_13")
                .addCategory("伪娘", "class_14")
                .addCategory("冒险", "class_15")
                .addCategory("小说", "class_19")
                .addCategory("港漫", "class_20")
                .addCategory("耽美", "class_21")
                .addCategory("经典", "class_22")
                .addCategory("欧美", "class_23")
                .addCategory("日文", "class_24")
                .addCategory("亲情", "class_25")
                .addCategory("汗妹推荐的绅士漫画", "best_1");
    }

    @Override
    protected void initRankBeanList(ComicListBean.ListBuilder listBuilder) {
        listBuilder
                .addListBean("最近刷新的漫画 TOP100", "newrating")
                .addListBean("最多人看的漫画 TOP100", "hotrating")
                .addListBean("评分最高的漫画 TOP100", "toprating")
                .addListBean("最多人评论的画 TOP100", "hoorating");
    }

    @Override
    protected void initRecommendList(ComicListBean.ListBuilder listBuilder) {
        listBuilder
                .addListBean("新加漫画", "iTabHotHtm0")
                .addListBean("人气榜漫", "iTabHotHtm1")
                .addListBean("热点漫画", "iTabHotHtm2")
                .addListBean("必看漫画", "iTabHotHtm3")
                .addListBean("漫迷推荐", "iTabHotHtm4");
    }

    @Override
    public Request buildCategoryRequest(Category category, int page, FilterList.FilterPicker picker, Sort sort) {
        return TextUtils.isEmpty(category.getCategoryId()) ? newGetRequest(BASE_URL + "/comic")
                : newGetRequest(String.format(COMIC_LIST_URL, category.getCategoryId()));
    }

    @Override
    public ComicResultList parseCategoryList(byte[] html, Category category, int page)
            throws UnsupportedEncodingException {
        Document doc = Jsoup.parse(new String(html, "utf-8"));
        // 解析类别结果的总页数
        Elements lastPageElements = doc.getElementsByAttributeValue("title", "最后一页");
        Element lastPage = lastPageElements.first();
        String[] pageCountString = lastPage.attr("href").split("/");
        int pageCount = Integer.valueOf(pageCountString[pageCountString.length - 1].split("\\.")[0]);
        List<Comic> comicList = parseComicList(doc);
        return new ComicResultList(comicList, category.getCategoryName(), page, pageCount);
    }

    @Override
    public Request buildChapterRequest(String comicId, String chapterId, String extra) {
        return newGetRequest(String.format(Locale.CHINESE, CHAPTER_URL, chapterId, 1, extra));
    }

    @Override
    public ChapterImage getChapterImage(byte[] html, String comicId, String chapterId, String extra)
            throws UnsupportedEncodingException {
        Document doc = Jsoup.parse(new String(html, "utf-8"));
        String firstImgUrl = getImg(doc);
        int pageCount = Integer.parseInt(doc.getElementsByAttributeValue("id", "hdPageCount")
                .first().attr("value"));
        return new ChapterImage.ChapterImageBuilder()
                .prepareLazyLoad(1, firstImgUrl, pageCount)
                .imageGet(page -> {
                    // 获取图片地址回调
                    Request request = newGetRequest(String.format(Locale.CHINESE, CHAPTER_URL, chapterId, page, extra));
                    Response response = mOkHttpClient.newCall(request).execute();
                    return getImg(Jsoup.parse(new String(response.body().bytes(), "utf-8")));
                })
                .build();
    }

    /**
     * 根据网页结果得到图片地址
     *
     * @param doc 返回结果
     * @return 图片地址
     */
    private static String getImg(Document doc) {
        // 找到图片服务器地址
        Element serviceListElement = doc.getElementsByAttributeValue("id", "hdDomain").first();
        String serviceListString = serviceListElement.attr("value");
        String[] service = serviceListString.split("\\|");
        // 找到密文
        Element imgElement = doc.getElementsByTag("img").first();
        String cipherText = imgElement.attr("name");
        // 组合成图片地址，使用第一个源
        return service[0] + encode(cipherText);
    }

    /**
     * 对图片地址密文进行解密
     *
     * @param cipherText 密文
     * @return 解密后的图片地址（不包含服务器地址）
     */
    private static String encode(String cipherText) {
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

    @Override
    public Request buildComicInfoRequest(String comicId) {
        return newGetRequest(String.format(COMIC_INFO_URL, comicId));
    }

    @Override
    public Comic getComicInfo(byte[] html, String comicId) throws UnsupportedEncodingException {
        Document doc = Jsoup.parse(new String(html, "utf-8"));
        Comic comic = new Comic();
        comic.setSourceKey(SOURCE_KEY);
        comic.setComicId(comicId);
        Element infoElement = doc.getElementsByAttributeValue("id", "about_kit").first();
        updateComicInfo(comic, infoElement);
        // 更新cover
        Element coverElement = doc.getElementsByAttributeValue("id", "about_style").first();
        String cover = coverElement.getElementsByTag("img").first().attr("src");
        comic.setCover(cover);
        return comic;
    }

    /**
     * 将about_kit列表中的信息更新到comic实体类中
     *
     * @param comic       comic
     * @param infoElement about_kit列表信息
     */
    private void updateComicInfo(Comic comic, Element infoElement) {
        Elements infoListElements = infoElement.getElementsByTag("li");
        int size = infoListElements.size();
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                String title = infoListElements.get(i).getElementsByTag("h1").first().text();
                title = title.trim();
                comic.setTitle(title);
                continue;
            }
            Element info = infoListElements.get(i).getElementsByTag("b").first();
            switch (info.text()) {
                case "作者:":
                    comic.setAuthor(infoListElements.get(i).ownText());
                    break;
                case "状态:":
                    String state = infoListElements.get(i).ownText();
                    if ("完结".equals(state)) {
                        comic.setEnd(true);
                    } else {
                        comic.setEnd(false);
                    }
                    break;
                case "简介":
                    comic.setDescription(infoListElements.get(i).ownText().substring(1));
                    break;
                case "更新:":
                    comic.setComicLastUpdateTime(infoListElements.get(i).ownText());
                    break;
                default:
            }
        }
    }

    @Override
    public Map<String, List<Chapter>> getChaptersInfo(byte[] html, String comicId) throws UnsupportedEncodingException {
        Document doc = Jsoup.parse(new String(html, "utf-8"));
        Element volListElement = doc.getElementsByAttributeValue("class", "cVolList").first();
        Elements volTagElements = volListElement.getElementsByAttributeValue("class", "cVolTag");
        Elements volU1Elements = volListElement.getElementsByAttributeValue("class", "cVolUl");
        int size = volTagElements.size();
        Map<String, List<Chapter>> chapterMap = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            Elements volElements = volU1Elements.get(i).getElementsByTag("li");
            List<Chapter> chapterList = new ArrayList<>(volElements.size());
            String tag = volTagElements.get(i).ownText();
            for (int j = 0; j < volElements.size(); j++) {
                Chapter chapter = new Chapter();
                Element volElement = volElements.get(j).getElementsByTag("a").first();
                String chapterId = volElement.attr("href").split("/")[1].substring(4);
                String title = volElement.attr("title");
                chapter.setSourceKey(SOURCE_KEY);
                chapter.setComicId(comicId);
                chapter.setType(tag);
                chapter.setChapterId(chapterId);
                chapter.setChapterName(title);
                chapterList.add(chapter);
            }
            chapterMap.put(tag, chapterList);
        }
        return chapterMap;
    }

    @Override
    public Request buildRankRequest(ComicListBean listBean, int page, FilterList.FilterPicker picker) {
        return newGetRequest(BASE_URL + "/top/" + listBean.getListId() + ".aspx");
    }

    @Override
    public ComicResultList parseRankList(byte[] html, ComicListBean listBean, int page) throws UnsupportedEncodingException {
        Document doc = Jsoup.parse(new String(html, "utf-8"));
        List<Comic> comicList = new ArrayList<>();
        Comic comic;
        // 排行解析
        Element topComicListElement = doc.getElementsByAttributeValue("class", "cTopComicList").first();
        Elements topComicElements = topComicListElement.getElementsByAttributeValue("class", "cComicItem");
        for (Element topComicElement : topComicElements) {
            String comicId = getComicIdFromHref(topComicElement.getElementsByTag("a")
                    .first().attr("href"));
            String img = topComicElement.getElementsByTag("img").first().attr("src");
            String title = topComicElement.getElementsByAttributeValue("class", "cComicTitle").first().ownText();
            String author = topComicElement.getElementsByAttributeValue("class", "cComicAuthor").first().ownText();
            String desc = topComicElement.getElementsByAttributeValue("class", "cComicMemo").first().ownText();
            comic = new Comic();
            comic.setSourceKey(SOURCE_KEY);
            comic.setComicId(comicId);
            comic.setCover(img);
            comic.setTitle(title);
            comic.setAuthor(author);
            comic.setDescription(desc);
            comicList.add(comic);
        }
        return new ComicResultList(comicList, listBean.getListName(), 1, 1);
    }

    @Override
    public Request buildRecommendRequest() {
        return newGetRequest(BASE_URL);
    }

    @Override
    public ComicResultList parseRecommendList(byte[] html, ComicListBean listBean) throws UnsupportedEncodingException {
        Document doc = Jsoup.parse(new String(html, "utf-8"));
        List<Comic> comicList = new ArrayList<>();
        Comic comic;
        // 推荐解析
        Element recommendListElement = doc.getElementsByAttributeValue("id", listBean.getListId()).first();
        Elements recommendElements = recommendListElement.getElementsByTag("li");
        for (Element recommend : recommendElements) {
            Element aElement = recommend.getElementsByTag("a").first();
            String comicId = getComicIdFromHref(aElement.attr("href"));
            String title = aElement.attr("title");
            String img = aElement.getElementsByTag("img").first().attr("src");
            comic = new Comic();
            comic.setSourceKey(SOURCE_KEY);
            comic.setComicId(comicId);
            comic.setCover(img);
            comic.setTitle(title);
            comicList.add(comic);
        }
        return new ComicResultList(comicList, listBean.getListName(), 1, 1);
    }

    @Override
    public Request buildSearchRequest(String searchKey, int page) {
        // 搜索结果只有30部漫画，page不起效
        return newGetRequest(String.format(SEARCH_URL, searchKey));
    }

    @Override
    public ComicResultList parseSearchList(byte[] html, String searchKey, int page) throws UnsupportedEncodingException {
        Document doc = Jsoup.parse(new String(html, "utf-8"));
        List<Comic> comicList = parseComicList(doc);
        if (comicList == null) {
            // 当没有结果时会出现为null的情况，该情况视为没有结果
            // 抛出空指针异常则为解析错误
            return new ComicResultList(null, searchKey, 0, 0, true);
        }
        return new ComicResultList(comicList, searchKey, 1, 1);
    }

    /**
     * 从相对地址解析出comicId
     *
     * @param href 相对地址
     * @return comicId
     */
    private String getComicIdFromHref(String href) {
        return href.split("\\.")[0].substring(7);
    }

    /**
     * 从页面DOM元素中解析出comic列表
     *
     * @param doc dom元素
     * @return comicList
     */
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
            comic.setSourceKey(SOURCE_KEY);
            comic.setTitle(comicElement.getElementsByTag("a").first().attr("title"));
            comic.setComicId(getComicIdFromHref(comicElement.getElementsByTag("a")
                    .first().attr("href")));
            comic.setCover(comicElement.getElementsByTag("img").first().attr("src"));
            comicList.add(comic);
        }
        return comicList;
    }
}
