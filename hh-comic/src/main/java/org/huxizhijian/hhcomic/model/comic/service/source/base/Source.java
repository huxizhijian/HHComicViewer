package org.huxizhijian.hhcomic.model.comic.service.source.base;

import org.huxizhijian.annotations.SourceInterface;
import org.huxizhijian.core.app.ConfigKeys;
import org.huxizhijian.core.app.HHGlobalVariable;
import org.huxizhijian.hhcomic.model.comic.service.bean.Category;
import org.huxizhijian.hhcomic.model.comic.service.bean.ComicListBean;
import org.huxizhijian.hhcomic.model.comic.service.source.base.parser.CategoryParser;
import org.huxizhijian.hhcomic.model.comic.service.source.base.parser.ChapterImageParser;
import org.huxizhijian.hhcomic.model.comic.service.source.base.parser.ComicInfoParser;
import org.huxizhijian.hhcomic.model.comic.service.source.base.parser.RankAndRecommendParser;
import org.huxizhijian.hhcomic.model.comic.service.source.base.parser.SearchComicParser;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author huxizhijian
 * @date 2018/9/27
 */
@SourceInterface
public abstract class Source implements CategoryParser, ChapterImageParser, ComicInfoParser
        , RankAndRecommendParser, SearchComicParser, SourceInfo {

    /**
     * 分类列表的构建者类
     */
    private Category.ListBuilder mCategoryListBuilder;

    /**
     * 源的分类列表
     */
    private List<Category> mCategoryList;

    /**
     * 排行列表的构建者类
     */
    private ComicListBean.ListBuilder mRankBeanListBuilder;

    /**
     * 源的排行列表
     */
    private List<ComicListBean> mRankBeanList;

    /**
     * 推荐列表的构建者类
     */
    private ComicListBean.ListBuilder mRecommendBeanListBuilder;

    /**
     * 源的推荐列表
     */
    private List<ComicListBean> mRecommendBeanList;

    /**
     * okhttp的client类，可以解析时或者添加分类实体、添加分类筛选时访问网络
     */
    protected final OkHttpClient mOkHttpClient;

    /**
     * 初始化方法
     */
    private void init() throws IOException {
        mCategoryListBuilder = new Category.ListBuilder(getSourceKey());
        initCategoryList(mCategoryListBuilder);
        mRankBeanListBuilder = new ComicListBean.ListBuilder(getSourceKey(), false);
        initRankBeanList(mRankBeanListBuilder);
        mRecommendBeanListBuilder = new ComicListBean.ListBuilder(getSourceKey(), true);
        initRecommendList(mRecommendBeanListBuilder);
    }

    /**
     * 由于init()方法可能会调用网路请求，所以该方法会抛出IO异常，子类的构造方法需要调用父类构造方法即需调用super()
     *
     * @throws IOException 调用网络请求可能会出现的错误
     */
    public Source() throws IOException {
        mOkHttpClient = HHGlobalVariable.getConfiguration(ConfigKeys.OKHTTP_CLIENT);
        init();
        mRankBeanList = mRankBeanListBuilder.build();
        mCategoryList = mCategoryListBuilder.build();
        mRecommendBeanList = mRecommendBeanListBuilder.build();
        if (mRankBeanList.size() == 0 || mCategoryList.size() == 0 || mRecommendBeanList.size() == 0) {
            throw new NullPointerException("Should add some list bean!");
        }
    }

    @Override
    public List<Category> getCategory() {
        return mCategoryList;
    }

    @Override
    public List<ComicListBean> getRank() {
        return mRankBeanList;
    }

    @Override
    public List<ComicListBean> getRecommend() {
        return mRecommendBeanList;
    }

    /**
     * 初始化分类的实体类列表
     *
     * @param listBuilder 帮助构造list的建造者类
     * @return 建造者类
     * @throws IOException 进行网络请求时可能发生的异常
     */
    protected abstract Category.ListBuilder initCategoryList(Category.ListBuilder listBuilder) throws IOException;

    /**
     * 初始化排行的实体类列表
     *
     * @param listBuilder 帮助构造list的建造者类
     * @return 建造者类
     * @throws IOException 进行网络请求时可能发生的异常
     */
    protected abstract ComicListBean.ListBuilder initRankBeanList(ComicListBean.ListBuilder listBuilder) throws IOException;

    /**
     * 初始化推荐的实体类列表
     *
     * @param listBuilder 帮助构造list的建造者类
     * @return 建造者类
     * @throws IOException 进行网络请求时可能发生的异常
     */
    protected abstract ComicListBean.ListBuilder initRecommendList(ComicListBean.ListBuilder listBuilder) throws IOException;

    /**
     * 帮助构造request
     */
    protected Request newGetRequest(String url) {
        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }
}
