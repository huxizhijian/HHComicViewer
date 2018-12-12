/*
 * Copyright 2016-2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import androidx.annotation.Nullable;
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
        if (hasRank()) {
            mRankBeanList = mRankBeanListBuilder.build();
            if (mRankBeanList.size() == 0) {
                throw new NullPointerException("Should add some list bean!");
            }
        }
        if (hasRecommend()) {
            mRecommendBeanList = mRecommendBeanListBuilder.build();
            if (mRecommendBeanList.size() == 0) {
                throw new NullPointerException("Should add some list bean!");
            }
        }
        mCategoryList = mCategoryListBuilder.build();
        if (mCategoryList.size() == 0) {
            throw new NullPointerException("Should add some list bean!");
        }
    }

    @Override
    public List<Category> getCategory() {
        return mCategoryList;
    }

    @Override
    public boolean hasRank() {
        // 默认含有排行列表，没有的话override此方法
        return true;
    }

    @Override
    @Nullable
    public List<ComicListBean> getRank() {
        return mRankBeanList;
    }

    @Override
    public boolean hasRecommend() {
        // 默认含有推荐列表，没有的话override此方法
        return true;
    }

    @Override
    @Nullable
    public List<ComicListBean> getRecommend() {
        return mRecommendBeanList;
    }

    /**
     * 初始化分类的实体类列表
     *
     * @param listBuilder 帮助构造list的建造者类
     * @throws IOException 进行网络请求时可能发生的异常
     */
    protected abstract void initCategoryList(Category.ListBuilder listBuilder) throws IOException;

    /**
     * 初始化排行的实体类列表
     *
     * @param listBuilder 帮助构造list的建造者类
     * @throws IOException 进行网络请求时可能发生的异常
     */
    protected abstract void initRankBeanList(ComicListBean.ListBuilder listBuilder) throws IOException;

    /**
     * 初始化推荐的实体类列表
     *
     * @param listBuilder 帮助构造list的建造者类
     * @throws IOException 进行网络请求时可能发生的异常
     */
    protected abstract void initRecommendList(ComicListBean.ListBuilder listBuilder) throws IOException;

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
