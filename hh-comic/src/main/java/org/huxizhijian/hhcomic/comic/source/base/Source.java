package org.huxizhijian.hhcomic.comic.source.base;

import org.huxizhijian.annotations.SourceInterface;
import org.huxizhijian.hhcomic.comic.bean.Category;
import org.huxizhijian.hhcomic.comic.bean.ComicListBean;
import org.huxizhijian.hhcomic.comic.source.base.parser.CategoryParser;
import org.huxizhijian.hhcomic.comic.source.base.parser.ChapterImageParser;
import org.huxizhijian.hhcomic.comic.source.base.parser.ComicInfoParser;
import org.huxizhijian.hhcomic.comic.source.base.parser.RankAndRecommendParser;
import org.huxizhijian.hhcomic.comic.source.base.parser.SearchComicParser;

import java.util.List;

/**
 * @author huxizhijian
 * @date 2018/9/27
 */
@SourceInterface
public abstract class Source implements CategoryParser, ChapterImageParser, ComicInfoParser
        , RankAndRecommendParser, SearchComicParser {

    /**
     * 获取源的名称
     *
     * @return source name
     */
    public abstract String getSourceName();

    /**
     * 获取源的唯一id
     *
     * @return source key
     */
    public abstract String getSourceKey();

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
    public abstract List<ComicListBean> getRank();

    /**
     * 获取源支持查询的推荐列表
     *
     * @return recommend list
     */
    public abstract List<ComicListBean> getRecommend();
}
