package org.huxizhijian.hhcomic.comic.source;

import org.huxizhijian.hhcomic.comic.bean.Chapter;
import org.huxizhijian.hhcomic.comic.bean.Comic;
import org.huxizhijian.hhcomic.comic.parser.detail.DetailStrategy;
import org.huxizhijian.hhcomic.comic.parser.search.SearchGetStrategy;

import java.io.UnsupportedEncodingException;
import java.util.List;

import okhttp3.Request;

/**
 * 汗汗漫画网站解析类
 *
 * @Author huxizhijian on 2017/10/9.
 */
public class HHManHua extends ComicSource {

    private static final String HH_NAME = "汗汗漫画";
    private static final String HH_BASE_URL = "http://www.hhmmoo.com/";
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

    public ComicSource newDefaltConfig() {
        return new HHManHua()
                .addStragegy(0, new HHDetailStrategy())
                .addStragegy(1, new HHSearchStrategy());
    }

    public class HHDetailStrategy extends DetailStrategy {

        @Override
        protected String getDetailUrl(String comicId) {
            return null;
        }

        @Override
        protected Comic parseComic(byte[] data, String comicId) throws UnsupportedEncodingException {
            //转换成UTF-8
            String content = new String(data, "utf-8");
            Comic comic = new Comic();
            return null;
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
        protected List<Chapter> parseChapter(byte[] data) {
            return null;
        }
    }

    public class HHSearchStrategy extends SearchGetStrategy {

        @Override
        protected String getSearchUrl(String key, int page, int size) {
            return null;
        }

        @Override
        protected List<Comic> parseSearchResult(byte[] data) {
            return null;
        }
    }

}
