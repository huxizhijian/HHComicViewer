package org.huxizhijian.hhcomicviewer2.persenter;

/**
 * Created by wei on 2017/1/20.
 */

public interface IClassifiesShowPresenter {
    void getComicList(String url, int page);
    void removeListener();
}
