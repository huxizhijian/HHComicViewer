package org.huxizhijian.hhcomic.service.source;

import android.text.TextUtils;
import android.util.SparseArray;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.huxizhijian.annotations.SourceImpl;
import org.huxizhijian.hhcomic.service.bean.Category;
import org.huxizhijian.hhcomic.service.bean.ComicListBean;
import org.huxizhijian.hhcomic.service.bean.Filter;
import org.huxizhijian.hhcomic.service.bean.FilterList;
import org.huxizhijian.hhcomic.service.bean.Sort;
import org.huxizhijian.hhcomic.service.bean.result.ChapterImage;
import org.huxizhijian.hhcomic.service.bean.result.ComicResultList;
import org.huxizhijian.hhcomic.db.entity.Chapter;
import org.huxizhijian.hhcomic.db.entity.Comic;
import org.huxizhijian.hhcomic.service.source.base.ComicSource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

/**
 * 该源的页码从0开始，所以请求的page应-1，返回的页码（如果该页码是解析出来的）应+1
 *
 * @author huxizhijian
 * @date 2018/10/8
 */
@SourceImpl(id = Dmzj.SOURCE_KEY)
public class Dmzj extends ComicSource {

    /**
     * 源id
     */
    public static final String SOURCE_KEY = "DMZJ";
    /**
     * 源名称
     */
    private static final String SOURCE_NAME = "动漫之家";
    /**
     * API V3
     */
    private static final String BASE_URL = "http://v3api.dmzj.com";
    /**
     * {comic id}
     */
    private static final String COMIC_URL = BASE_URL + "/comic/%s.json";
    /**
     * {comic id}{chapter id}
     */
    private static final String CHAPTER_URL = BASE_URL + "/chapter/%s/%s.json";
    /**
     * 返回category id(tag id),cover和名称的列表, 0表示漫画分类，1则是小说分类
     */
    private static final String CATEGORY_URL = BASE_URL + "/0/category.json";
    /**
     * category type filter
     */
    private static final String CATEGORY_FILTER = BASE_URL + "/classify/filter.json";
    /**
     * {category/filter id(tag id)}{sort id}{page}
     * 值得注意的是，多个tag id之间用-分隔
     */
    private static final String CATEGORY_RESULT_URL = BASE_URL + "/classify/%s/%s/%d.json";
    /**
     * rank type filter
     */
    private static final String RANK_FILTER = BASE_URL + "/rank/type_filter.json";
    /**
     * {filter id}{time limit}{rank id}{page}
     */
    private static final String RANK_URL = BASE_URL + "/rank/%s/%s/%s/%d.json";
    /**
     * {keyword}{page}, 0表示搜索漫画, 1是搜索小说
     */
    private static final String SEARCH_URL = BASE_URL + "/search/show/0/%s/%d.json";
    /**
     * 主页推荐界面
     */
    private static final String RECOMMEND_URL = BASE_URL + "/v3/recommend.json";

    public Dmzj() throws IOException {
        super();
    }

    @Override
    protected Category.ListBuilder initCategoryList(Category.ListBuilder listBuilder) throws IOException {
        // 请求category筛选
        Request filterRequest = newGetRequest(CATEGORY_FILTER);
        Response filterResponse = mOkHttpClient.newCall(filterRequest).execute();
        JSONArray filterArray = JSONArray.parseArray(filterResponse.body().string());

        FilterList.Builder filterListBuilder = FilterList.newFilterList(SOURCE_KEY);

        // 开启第一个for循环，获取subtitle
        int arraySize = filterArray.size();
        for (int i = 0; i < arraySize; i++) {
            JSONObject jsonObject = filterArray.getJSONObject(i);
            FilterList.Builder.FilterItemListBuilder filterItemListBuilder = filterListBuilder.beginSubtitle(jsonObject.getString("title"));
            JSONArray itemArray = jsonObject.getJSONArray("items");
            // 开启第二个for循环，获得subtitle下具体的筛选item
            int itemArraySize = itemArray.size();
            for (int j = 0; j < itemArraySize; j++) {
                JSONObject item = itemArray.getJSONObject(j);
                filterItemListBuilder.add(item.getString("tag_id"), item.getString("tag_name"));
            }
            filterItemListBuilder.endSubtitle();
        }

        FilterList filterList = filterListBuilder.build();

        // category排序
        List<Sort> sortList = new Sort.ListBuilder(SOURCE_KEY)
                .add("0", "热度倒序")
                .add("1", "更新倒序")
                .build();

        // 请求category分类
        Request categoryRequest = newGetRequest(CATEGORY_URL);
        Response categoryResponse = mOkHttpClient.newCall(categoryRequest).execute();
        JSONArray array = JSONArray.parseArray(categoryResponse.body().string());
        int size = array.size();
        for (int i = 0; i < size; i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            // 将筛选列表和排序列表加入
            listBuilder.addCategory(jsonObject.getString("title"), jsonObject.getString("tag_id"),
                    jsonObject.getString("cover"), filterList, sortList);
        }
        return listBuilder;
    }

    @Override
    protected ComicListBean.ListBuilder initRankBeanList(ComicListBean.ListBuilder listBuilder) throws IOException {
        // 请求rank的类别筛选列表
        Request filterRequest = newGetRequest(RANK_FILTER);
        Response filterResponse = mOkHttpClient.newCall(filterRequest).execute();
        JSONArray filterArray = JSONArray.parseArray(filterResponse.body().string());

        FilterList.Builder filterListBuilder = FilterList.newFilterList(SOURCE_KEY);

        FilterList.Builder.FilterItemListBuilder filterItemListBuilder = filterListBuilder.beginSubtitle("类别");
        // 请求这里只有一个subtitle，即全部分类
        int filterSize = filterArray.size();
        for (int i = 0; i < filterSize; i++) {
            JSONObject jsonObject = filterArray.getJSONObject(i);
            filterItemListBuilder.add(jsonObject.getString("tag_id"), jsonObject.getString("tag_name"));
        }
        filterItemListBuilder.endSubtitle();

        // 加入另一个无法网络请求的筛选
        filterListBuilder.beginSubtitle("时间")
                .add("0", "日")
                .add("1", "周")
                .add("2", "月")
                .add("3", "年")
                .endSubtitle();

        FilterList filterList = filterListBuilder.build();

        // 排行榜顺序即是排行榜类别
        listBuilder
                .addListBean("人气排行", "0", filterList)
                .addListBean("吐槽排行", "1", filterList)
                .addListBean("订阅排行", "2", filterList);

        return listBuilder;
    }

    @Override
    protected ComicListBean.ListBuilder initRecommendList(ComicListBean.ListBuilder listBuilder) throws IOException {
        // 主页推荐
        return listBuilder
                .addListBean("近期必看", "47")
                .addListBean("国漫也精彩", "52")
                .addListBean("美漫大事件", "53")
                .addListBean("热门连载", "54")
                .addListBean("条漫专区", "55")
                .addListBean("最新上架", "56");
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
    public Request buildCategoryRequest(Category category, int page, FilterList.FilterPicker picker, Sort sort) {
        // 分析过滤
        StringBuilder filterString = new StringBuilder();
        filterString.append(category.getCategoryId());
        int size = picker.getSubtitleList().size();
        for (int i = 0; i < size; i++) {
            String subtitle = picker.getSubtitleList().get(i);
            Filter filter = picker.getPick(subtitle);
            if (filter != null) {
                if ("题材".equals(subtitle)) {
                    // 如果选择的是题材，就清除分类
                    filterString.delete(0, filterString.length());
                    filterString.append(filter.getId());
                } else {
                    filterString.append("-");
                    filterString.append(filter.getId());
                }
            }
        }
        String sortString = sort == null ? "0" : sort.getId();
        return newGetRequest(String.format(Locale.CHINESE, CATEGORY_RESULT_URL,
                filterString.toString(), sortString, page - 1));
    }

    @Override
    public ComicResultList parseCategoryList(byte[] html, Category category, int page)
            throws UnsupportedEncodingException {
        String jsonString = new String(html, "utf-8");
        return new ComicResultList(parseComicListFromJsonString(jsonString), "分类结果", page, -1);
    }

    @Override
    public Request buildChapterRequest(String comicId, String chapterId, String extra) {
        return newGetRequest(String.format(CHAPTER_URL, comicId, chapterId));
    }

    @Override
    public ChapterImage getChapterImage(byte[] html, String comicId, String chapterId, String extra)
            throws UnsupportedEncodingException {
        String jsonString = new String(html, "utf-8");
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        JSONArray imgArray = jsonObject.getJSONArray("page_url");
        int imgSize = imgArray.size();
        SparseArray<String> imgList = new SparseArray<>(imgSize);
        for (int i = 0; i < imgSize; i++) {
            imgList.put(i, (String) imgArray.get(i));
        }
        return new ChapterImage.ChapterImageBuilder()
                .fullImageUrl(imgList)
                .build();
    }

    @Override
    public Request buildComicInfoRequest(String comicId) {
        return newGetRequest(String.format(Locale.CHINESE, COMIC_URL, comicId));
    }

    @Override
    public Comic getComicInfo(byte[] html, String comicId) throws UnsupportedEncodingException {
        String jsonString = new String(html, "utf-8");
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        Comic comic = new Comic();
        comic.setSourceKey(SOURCE_KEY);
        comic.setComicId(comicId);
        comic.setTitle(jsonObject.getString("title"));
        if (jsonObject.getString("last_updatetime") != null) {
            String updateTime = jsonObject.getString("last_updatetime");
            comic.setComicLastUpdateTime(stringToDate(updateTime));
        }
        comic.setCover(jsonObject.getString("cover"));
        JSONArray statusArray = jsonObject.getJSONArray("status");
        String status = statusArray.getJSONObject(0).getString("tag_name");
        if (status != null) {
            if ("已完结".equals(status)) {
                comic.setEnd(true);
            } else if ("连载中".equals(status)) {
                comic.setEnd(false);
            }
        }
        JSONArray authors = jsonObject.getJSONArray("authors");
        StringBuilder author = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            if (i != 0) {
                author.append(" ");
            }
            author.append(authors.getJSONObject(i).getString("tag_name"));
        }
        comic.setAuthor(author.toString());
        comic.setDescription(jsonObject.getString("description"));
        return comic;
    }

    @Override
    public Map<String, List<Chapter>> getChaptersInfo(byte[] html, String comicId) throws UnsupportedEncodingException {
        // 解析章节列表
        String jsonString = new String(html, "utf-8");
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        JSONArray chapterSubtitleArray = jsonObject.getJSONArray("chapters");
        int typeSize = chapterSubtitleArray.size();
        Map<String, List<Chapter>> chapterMap = new HashMap<>(typeSize);
        for (int i = 0; i < typeSize; i++) {
            JSONObject chapterListObject = chapterSubtitleArray.getJSONObject(i);
            String subtitle = chapterListObject.getString("title");
            JSONArray chapterArray = chapterListObject.getJSONArray("data");
            int chapterSize = chapterArray.size();
            List<Chapter> chapterList = new ArrayList<>(chapterSize);
            for (int j = 0; j < chapterSize; j++) {
                JSONObject chapterObject = chapterArray.getJSONObject(j);
                Chapter chapter = new Chapter();
                chapter.setSourceKey(SOURCE_KEY);
                chapter.setComicId(comicId);
                chapter.setChapterId(chapterObject.getString("chapter_id"));
                chapter.setChapterName(chapterObject.getString("chapter_title"));
                chapter.setType(subtitle);
                chapterList.add(chapter);
            }
            chapterMap.put(subtitle, chapterList);
        }
        return chapterMap;
    }

    @Override
    public Request buildRankRequest(ComicListBean listBean, int page, FilterList.FilterPicker picker) {
        Filter classifyFilter = picker.getPick("类别");
        Filter timeFilter = picker.getPick("时间");
        return newGetRequest(String.format(Locale.CHINESE, RANK_URL,
                classifyFilter.getId(), timeFilter.getId(), listBean.getListId(), page - 1));
    }

    @Override
    public ComicResultList parseRankList(byte[] html, ComicListBean listBean, int page)
            throws UnsupportedEncodingException {
        String jsonString = new String(html, "utf-8");
        return new ComicResultList(parseComicListFromJsonString(jsonString), "分类结果", page, -1);
    }

    @Override
    public Request buildRecommendRequest() {
        return newGetRequest(RECOMMEND_URL);
    }

    @Override
    public ComicResultList parseRecommendList(byte[] html, ComicListBean listBean) throws UnsupportedEncodingException {
        String jsonString = new String(html, "utf-8");
        JSONArray jsonArray = JSONArray.parseArray(jsonString);
        int recommendSize = jsonArray.size();
        List<Comic> comicList = null;
        for (int i = 0; i < recommendSize; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (listBean.getListId().equals(jsonObject.getString("category_id"))) {
                JSONArray recommendArray = jsonObject.getJSONArray("data");
                int size = recommendArray.size();
                comicList = new ArrayList<>(size);
                Comic comic;
                for (int j = 0; j < size; j++) {
                    comic = new Comic();
                    comic.setSourceKey(SOURCE_KEY);
                    String comicId = jsonObject.getString("id");
                    if (TextUtils.isEmpty(comicId)) {
                        comicId = jsonObject.getString("obj_id");
                    }
                    comic.setComicId(comicId);
                    comic.setTitle(jsonObject.getString("title"));
                    comic.setCover(jsonObject.getString("cover"));
                    String status = jsonObject.getString("status");
                    if (status != null) {
                        if ("已完结".equals(status)) {
                            comic.setEnd(true);
                        } else if ("连载中".equals(status)) {
                            comic.setEnd(false);
                        }
                    }
                    comicList.add(comic);
                }
            }
        }
        return new ComicResultList(comicList, listBean.getListName(), 1, 1);
    }

    @Override
    public Request buildSearchRequest(String searchKey, int page) {
        return newGetRequest(String.format(Locale.CHINESE, SEARCH_URL, searchKey, page - 1));
    }

    @Override
    public ComicResultList parseSearchList(byte[] html, String searchKey, int page) throws UnsupportedEncodingException {
        String jsonString = new String(html, "utf-8");
        return new ComicResultList(parseComicListFromJsonString(jsonString), "分类结果", page, -1);
    }

    private List<Comic> parseComicListFromJsonString(String jsonString) {
        JSONArray array = JSONArray.parseArray(jsonString);
        int size = array.size();
        if (size != 0) {
            List<Comic> comicList = new ArrayList<>();
            Comic comic;
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                comic = new Comic();
                comic.setSourceKey(SOURCE_KEY);
                comic.setComicId(jsonObject.getString("id"));
                comic.setTitle(jsonObject.getString("title"));
                comic.setAuthor(jsonObject.getString("authors"));
                if (jsonObject.getString("last_updatetime") != null) {
                    String updateTime = jsonObject.getString("last_updatetime");
                    comic.setComicLastUpdateTime(stringToDate(updateTime));
                }
                comic.setCover(jsonObject.getString("cover"));
                String status = jsonObject.getString("status");
                if (status != null) {
                    if ("已完结".equals(status)) {
                        comic.setEnd(true);
                    } else if ("连载中".equals(status)) {
                        comic.setEnd(false);
                    }
                }
                comicList.add(comic);
            }
            return comicList;
        }
        return null;
    }

    /**
     * String类型毫秒数转换成日期
     *
     * @return String yyyy-MM-dd HH:mm:ss
     */
    private static String stringToDate(String lo) {
        long time = Long.parseLong(lo);
        Date date = new Date(time);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return sd.format(date);
    }
}
