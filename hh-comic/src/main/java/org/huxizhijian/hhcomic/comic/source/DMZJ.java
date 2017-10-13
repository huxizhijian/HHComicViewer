package org.huxizhijian.hhcomic.comic.source;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.huxizhijian.core.app.HHEngine;
import org.huxizhijian.core.util.misc.Pair;
import org.huxizhijian.hhcomic.R;
import org.huxizhijian.hhcomic.comic.bean.Chapter;
import org.huxizhijian.hhcomic.comic.bean.Comic;
import org.huxizhijian.hhcomic.comic.parser.comic.detail.DetailStrategy;
import org.huxizhijian.hhcomic.comic.source.base.ComicSource;
import org.huxizhijian.hhcomic.comic.source.base.Source;
import org.huxizhijian.hhcomic.comic.type.ComicDataSourceType;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;

/**
 * TODO 动漫之家解析类
 * 图片的请求需要加入Header("Referer", "http://images.dmzj.com/")，否则会403
 *
 * @Author huxizhijian on 2017/10/12.
 */

public class DMZJ extends ComicSource {

    private static final String SOURCE_NAME = "动漫之家";
    private static final String DMZJ_BASE_URL = "v2.api.dmzj.com";
    private static final String DMZJ_COMIC_URL = "/comic/%s.json";
    private static final String DMZJ_CHAPTER_URL = "/chapter/%s/%s.json";       //chapter获取接口，第一个参数cid，第二个参数chapterId
    private static final String DMZJ_CATEGORY_URL = "/0/category.json";         //获取支持的分类id列表及cover
    private static final String DMZJ_SEARCH_URL = "/search/show/%d/%s/%d.json"; //Search接口参数为：搜索类别id，搜索关键字，结果页码
    private static final String DMZJ_CLASSIFY_URL = " /classify/%d/0/%d.json";  //获取类别的Comic列表，参数：类别id，结果页码
    private static final String DMZJ_RANK_URL = "/rank/0/0/0/%d.json";          //获取排行榜内容，参数：页码

    private static final int SOURCE_TYPE = Source.DMZJ;

    @Override
    public String setSourceName() {
        return SOURCE_NAME;
    }

    @Override
    public String setBaseUrl() {
        return DMZJ_BASE_URL;
    }

    @Override
    public int getSourceType() {
        return SOURCE_TYPE;
    }

    public DMZJ() {
        // 解析json文件，添加分类信息
        JSONArray array = JSON.parseArray(HHEngine.getApplicationContext().getResources().getString(R.string.dmzj_category));
        int size = array.size();
        JSONObject object;
        for (int i = 0; i < size; i++) {
            object = array.getJSONObject(i);
            CATEGORY_TYPE_MAP.put(String.valueOf(object.getInteger("tag_id")),
                    Pair.create(object.getString("title"), object.getString("cover")));
        }
    }

    /**
     * 默认添加所有策略，也可以自行添加
     */
    public ComicSource defaultConfig() {
        return new DMZJ()
                .addStrategy(ComicDataSourceType.WEB_DETAIL, new DMZJDetailStrategy());
    }

    /**
     * Comic详情策略
     */
    public class DMZJDetailStrategy extends DetailStrategy {

        @Override
        protected String getDetailUrl(String comicId) {
            return String.format(Locale.CHINESE, DMZJ_BASE_URL + DMZJ_COMIC_URL, comicId);
        }

        @Override
        protected Comic parseComic(byte[] data, String comicId) throws UnsupportedEncodingException {
            String html = new String(data, "utf-8");
            JSONObject object = JSON.parseObject(html);
            String title = object.getString("title");
            String cover = object.getString("cover");
            String update = String.valueOf(object.getLong("last_updatetime"));
            String intro = object.getString("description");
            StringBuilder sb = new StringBuilder();
            JSONArray array = object.getJSONArray("authors");
            for (int i = 0; i < array.size(); ++i) {
                sb.append(array.getJSONObject(i).getString("tag_name")).append(" ");
            }
            String author = sb.toString();
            boolean status = object.getJSONArray("status").getJSONObject(0).getInteger("tag_id") == 2310;
            //初始化Comic
            Comic comic = new Comic(SOURCE_TYPE, comicId, title, cover, update, author);
            comic.setIntro(intro);
            comic.setFinish(status);
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
            List<Chapter> chapters = new ArrayList<>();
            String html = new String(data, "utf-8");
            JSONObject object = JSON.parseObject(html);
            JSONArray array = object.getJSONArray("chapters");
            for (int i = 0; i != array.size(); ++i) {
                JSONArray chapterJson = array.getJSONObject(i).getJSONArray("data");
                for (int j = 0; j != chapterJson.size(); ++j) {
                    JSONObject chapter = chapterJson.getJSONObject(j);
                    String title = chapter.getString("chapter_title");
                    String path = chapter.getString("chapter_id");
                    long chapterId = chapter.getLong("chapter_id");
                    chapters.add(new Chapter(title, path, chapterId));
                }
            }
            return chapters;
        }
    }

}
