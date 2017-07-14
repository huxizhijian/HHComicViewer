package org.huxizhijian.hhcomicviewer2.presenter.viewinterface;

import org.huxizhijian.hhcomicviewer2.adapter.entity.ComicTabList;

import java.util.List;

/**
 * Created by wei on 2017/1/4.
 */

public interface IComicRecommendFragment {

    void onSuccess(List<ComicTabList> comicTabLists);

    void onException(Throwable e);

    void onFailure(int errorCode, String errorMsg);

}
