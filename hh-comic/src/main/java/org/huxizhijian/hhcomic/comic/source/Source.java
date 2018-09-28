package org.huxizhijian.hhcomic.comic.source;

import org.huxizhijian.hhcomic.comic.bean.Category;
import org.huxizhijian.hhcomic.comic.bean.ComicRank;
import org.huxizhijian.hhcomic.comic.source.parser.CategoryParser;
import org.huxizhijian.hhcomic.comic.source.parser.ChapterImageParser;
import org.huxizhijian.hhcomic.comic.source.parser.ComicInfoParser;
import org.huxizhijian.hhcomic.comic.source.parser.RankComicParser;
import org.huxizhijian.hhcomic.comic.source.parser.SearchComicParser;

import java.util.List;

/**
 * @author huxizhijian
 * @date 2018/9/27
 */
public abstract class Source implements CategoryParser, ChapterImageParser, ComicInfoParser
        , RankComicParser, SearchComicParser {

    /**
     * 获取源的名称
     *
     * @return source name
     */
    public abstract String getSourceName();

    /**
     * 获取类的分类
     *
     * @return 分类列表
     */
    public abstract List<Category> getCategory();

    /**
     * 获取源支持查询的排行榜
     *
     * @return rank list
     */
    public abstract List<ComicRank> getRank();

    /**
     * 获取源支持查询的推荐列表
     *
     * @return recommend list
     */
    public abstract List<ComicRank> getRecommend();
}
