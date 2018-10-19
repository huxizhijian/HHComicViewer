package org.huxizhijian.hhcomic.comic.source;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.huxizhijian.annotations.SourceImpl;
import org.huxizhijian.hhcomic.comic.bean.Category;
import org.huxizhijian.hhcomic.comic.bean.ComicListBean;
import org.huxizhijian.hhcomic.comic.bean.FilterList;
import org.huxizhijian.hhcomic.comic.bean.Sort;
import org.huxizhijian.hhcomic.comic.bean.result.ChapterImage;
import org.huxizhijian.hhcomic.comic.bean.result.ComicResultList;
import org.huxizhijian.hhcomic.comic.entity.Chapter;
import org.huxizhijian.hhcomic.comic.entity.Comic;
import org.huxizhijian.hhcomic.comic.source.base.ComicSource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

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
        return null;
    }

    @Override
    public ComicResultList parseCategoryList(byte[] html, Category category, int page)
            throws UnsupportedEncodingException {
        return null;
    }

    @Override
    public Request buildChapterRequest(String comicId, String chapterId, String extra) {
        return null;
    }

    @Override
    public ChapterImage getChapterImage(byte[] html, String comicId, String chapterId, String extra)
            throws UnsupportedEncodingException {
        return null;
    }

    @Override
    public Request buildComicInfoRequest(String comicId) {
        return null;
    }

    @Override
    public Comic getComicInfo(byte[] html, String comicId) throws UnsupportedEncodingException {
        return null;
    }

    @Override
    public List<Chapter> getChaptersInfo(byte[] html, String comicId) throws UnsupportedEncodingException {
        return null;
    }

    @Override
    public Request buildRankRequest(ComicListBean listBean, int page, FilterList.FilterPicker picker) {
        return null;
    }

    @Override
    public ComicResultList parseRankList(byte[] html, ComicListBean listBean, int page) throws UnsupportedEncodingException {
        return null;
    }

    @Override
    public Request buildRecommendRequest() {
        return null;
    }

    @Override
    public ComicResultList parseRecommendList(byte[] html, ComicListBean listBean) throws UnsupportedEncodingException {
        return null;
    }

    @Override
    public Request buildSearchRequest(String searchKey, int page) {
        return null;
    }

    @Override
    public ComicResultList parseSearchList(byte[] html, String searchKey, int page) throws UnsupportedEncodingException {
        return null;
    }
}
