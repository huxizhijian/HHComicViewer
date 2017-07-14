package org.huxizhijian.hhcomicviewer2.presenter;

import org.huxizhijian.hhcomicviewer2.model.ComicChapter;

/**
 * Created by wei on 2017/1/7.
 */

public interface IComicChapterPresenter {
    void getComicChapter(ComicChapter comicChapter);
    void removeListener();
}
