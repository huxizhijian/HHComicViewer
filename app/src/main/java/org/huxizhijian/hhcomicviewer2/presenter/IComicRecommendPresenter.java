package org.huxizhijian.hhcomicviewer2.presenter;

/**
 * Created by wei on 2017/1/4.
 */

public interface IComicRecommendPresenter {

    void getRecommendList();

    boolean isConnecting();

    void removeListener();

}
