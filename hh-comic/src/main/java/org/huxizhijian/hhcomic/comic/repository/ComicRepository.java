/*
 * Copyright 2017 huxizhijian
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

package org.huxizhijian.hhcomic.comic.repository;

import org.huxizhijian.hhcomic.comic.bean.Comic;
import org.huxizhijian.hhcomic.comic.source.base.ComicSource;
import org.huxizhijian.hhcomic.comic.type.ComicDataSourceType;
import org.huxizhijian.hhcomic.comic.type.ComicDataState;
import org.huxizhijian.hhcomic.comic.value.IComicRequest;

import java.util.List;

/**
 * 将远程{@link LocalComicRepository}、本地{@link RemoteComicRepository}两个实现类整合起来，根据设定的策略使用不同的来源
 *
 * @author huxizhijian 2017/9/21
 */
public class ComicRepository implements ComicDataSource {

    private final ComicDataSource mLocalRepository;
    private final ComicDataSource mRemoteRepository;

    private ComicRepository() {
        mLocalRepository = new LocalComicRepository();
        mRemoteRepository = new RemoteComicRepository();
    }

    public static ComicRepository getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final ComicRepository INSTANCE = new ComicRepository();
    }

    @Override
    public void get(ComicSource source, IComicRequest requestValues, ComicDataCallback callback) {
        int requestType = requestValues.getRequestType();
        switch (requestType) {
            case ComicDataSourceType.DB_DOWNLOADED:
            case ComicDataSourceType.DB_FAVORITE:
            case ComicDataSourceType.DB_HISTORY:
                // 调用本地仓库
                mLocalRepository.get(source, requestValues, callback);
                break;
            case ComicDataSourceType.WEB_CATEGORY:
            case ComicDataSourceType.WEB_DETAIL:
            case ComicDataSourceType.WEB_GET_CHAPTER:
            case ComicDataSourceType.WEB_RANK:
            case ComicDataSourceType.WEB_RECOMMENDED:
            case ComicDataSourceType.WEB_SEARCH:
                // 调用网络仓库
                mRemoteRepository.get(source, requestValues, callback);
                break;
            default:
                callback.onError(new NullPointerException("Request type is not exist!"));
                break;
        }
    }

    @Override
    public void saveComic(Comic comic, ComicDataState state) {
        mLocalRepository.saveComic(comic, state);
    }

    @Override
    public void deleteComic(ComicSource source, String comicId, ComicDataState state) {
        mLocalRepository.deleteComic(source, comicId, state);
    }

    @Override
    public void deleteComic(Comic comic, ComicDataState state) {
        mLocalRepository.deleteComic(comic, state);
    }

    @Override
    public void deleteComicsRange(ComicSource source, String[] comicIds, ComicDataState state) {
        mLocalRepository.deleteComicsRange(source, comicIds, state);
    }

    @Override
    public void deleteComicsRange(List<Comic> comics, ComicDataState state) {
        mLocalRepository.deleteComicsRange(comics, state);
    }

    @Override
    public void checkMarkComicUpdate(ComicDataCallback callback) {
        mRemoteRepository.checkMarkComicUpdate(callback);
    }

}
