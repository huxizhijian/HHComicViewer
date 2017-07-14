package org.huxizhijian.hhcomicviewer2.presenter.viewinterface;

import org.huxizhijian.hhcomicviewer2.model.ComicChapter;

/**
 * Created by wei on 2017/1/7.
 */

public interface IComicChapterListener {

    void onSuccess(ComicChapter comicChapter);

    void onException(Throwable e, ComicChapter comicChapter);

    void onFail(int errorCode, String errorMsg, ComicChapter comicChapter);

}
