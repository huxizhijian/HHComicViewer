/*
 * Copyright 2016-2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.hhcomic.oldcomic.repository;

import org.huxizhijian.hhcomic.oldcomic.bean.Comic;
import org.huxizhijian.hhcomic.oldcomic.db.ComicDBHelper;
import org.huxizhijian.hhcomic.oldcomic.source.base.ComicSource;
import org.huxizhijian.hhcomic.oldcomic.type.DataSourceType;
import org.huxizhijian.hhcomic.oldcomic.type.ComicDataState;
import org.huxizhijian.hhcomic.oldcomic.type.RequestFieldType;
import org.huxizhijian.hhcomic.oldcomic.value.ComicResponseValues;
import org.huxizhijian.hhcomic.oldcomic.value.IComicRequest;
import org.huxizhijian.hhcomic.oldcomic.value.IComicResponse;

import java.util.List;

/**
 * 本地仓库，本质为Comic的数据库操作
 *
 * @author huxizhijian
 * @date 2017/10/12.
 */
public class LocalComicRepository implements ComicDataSource {

    private ComicDBHelper mComicDBHelper = ComicDBHelper.getInstance();

    private LocalComicRepository() {
    }

    private static final class Holder {
        private static final LocalComicRepository INSTANCE = new LocalComicRepository();
    }

    public static LocalComicRepository getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void get(ComicSource source, IComicRequest requestValues, ComicDataCallback callback) {
        int order = requestValues.getField(RequestFieldType.ORDER);
        boolean highLight = requestValues.getField(RequestFieldType.HIGH_LIGHT);
        List<Comic> comics;
        switch (requestValues.getDataSourceType()) {
            case DataSourceType.DB_DOWNLOADED:
                comics = mComicDBHelper.getDownloadList(order);
                break;
            case DataSourceType.DB_FAVORITE:
                comics = mComicDBHelper.getFavoriteList(order, highLight);
                break;
            case DataSourceType.DB_HISTORY:
                comics = mComicDBHelper.getHistoryList(order);
                break;
            default:
                callback.onError(new NullPointerException("Type is not exist!"));
                return;
        }
        if (comics == null) {
            callback.onDataNotAvailable();
        }
        IComicResponse values = new ComicResponseValues();
        values.setComicResponse(comics);
        callback.onSuccess(values);
    }

    @Override
    public void saveComic(Comic comic, ComicDataState state) {
        if (mComicDBHelper.get(comic.getSource(), comic.getCid()) != null) {
            setComicState(comic, state, System.currentTimeMillis());
            mComicDBHelper.update(comic);
        } else {
            mComicDBHelper.insert(comic);
        }
    }

    @Override
    public void deleteComic(ComicSource source, String comicId, ComicDataState state) {
        Comic comic = mComicDBHelper.get(source.getSourceType(), comicId);
        if (comic != null) {
            setComicState(comic, state, null);
            mComicDBHelper.update(comic);
        }
    }

    @Override
    public void deleteComic(Comic comic, ComicDataState state) {
        if (mComicDBHelper.get(comic.getSource(), comic.getCid()) != null) {
            setComicState(comic, state, null);
            mComicDBHelper.update(comic);
        }
    }

    @Override
    public void deleteComicsRange(ComicSource source, String[] comicIds, ComicDataState state) {
        for (String comicId : comicIds) {
            deleteComic(source, comicId, state);
        }
    }

    @Override
    public void deleteComicsRange(List<Comic> comics, ComicDataState state) {
        for (Comic comic : comics) {
            deleteComic(comic, state);
        }
    }

    @Deprecated
    @Override
    public void checkMarkComicUpdate(ComicDataCallback callback) {

    }

    private void setComicState(Comic comic, ComicDataState state, Long value) {
        if (state == ComicDataState.DOWNLOADED) {
            comic.setDownload(value);
        } else if (state == ComicDataState.FAVORITE) {
            comic.setFavorite(value);
        } else if (state == ComicDataState.HISTORY) {
            comic.setFavorite(value);
        }
    }

}
