package org.huxizhijian.hhcomicviewer.ui.debug;

import org.huxizhijian.hhcomic.comic.bean.Comic;
import org.huxizhijian.hhcomicviewer.base.BasePresenter;
import org.huxizhijian.hhcomicviewer.base.BaseView;

import java.util.List;

/**
 * @author huxizhijian
 * @date 2017/11/13
 */
public class GetRecommendsContract {

    interface View extends BaseView<Presenter> {

        /**
         * 获取第一页的comicList
         *
         * @param comicList 漫画list
         * @param hasMore   是否有下一页，根据这个控制是否有加载下一页的时间监听
         */
        void onGetComicList(List<Comic> comicList, boolean hasMore);

        /**
         * 获取ComicList出现错误
         */
        void onError();

        /**
         * 获取到空list
         */
        void onEmptyList();

        /**
         * 获取下一页成功
         *
         * @param comicList comicList
         * @param hasMore   是否有下一页
         */
        void onGetNextPageSuccess(List<Comic> comicList, boolean hasMore);

        /**
         * 获取下一页失败，没有更多了（主要用于不知道pageCount的情况）
         */
        void onNoMore();
    }

    interface Presenter extends BasePresenter {

        /**
         * 开始获取下一页
         */
        void getNextPage();

        /**
         * 获取指定页面的内容，完成后将会调用onGetComicList()刷新页面
         *
         * @param page 指定index
         */
        void getSpecifyPage(int page);

        /**
         * 开始刷新页面
         */
        void refresh();

        /**
         * 获取当前页数
         *
         * @return page
         */
        int getPage();

        /**
         * 获取总页数，-1为不清楚
         *
         * @return pageCount
         */
        int getPageCount();
    }
}
