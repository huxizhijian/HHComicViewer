/*
 * Copyright 2018 huxizhijian
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

import org.huxizhijian.core.app.ConfigKeys;
import org.huxizhijian.core.app.HHEngine;
import org.huxizhijian.core.util.log.HHLogger;
import org.huxizhijian.hhcomic.oldcomic.bean.Comic;
import org.huxizhijian.hhcomic.oldcomic.parser.comic.ComicParseStrategy;
import org.huxizhijian.hhcomic.oldcomic.source.base.ComicSource;
import org.huxizhijian.hhcomic.oldcomic.type.ComicDataState;
import org.huxizhijian.hhcomic.oldcomic.value.ComicResponseValues;
import org.huxizhijian.hhcomic.oldcomic.value.IComicRequest;
import org.huxizhijian.hhcomic.oldcomic.value.IComicResponse;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 远程仓库，实现从远程拉取Comic的操作
 * 由于没有把数据放在网络上备份，不实现部分功能
 *
 * @author huxizhijian
 * @date 2017/10/12
 */
public class RemoteComicRepository implements ComicDataSource {

    private static final String TAG = RemoteComicRepository.class.getSimpleName();

    private RemoteComicRepository() {
    }

    private static final class Holder {
        private static final RemoteComicRepository INSTANCE = new RemoteComicRepository();
    }

    public static RemoteComicRepository getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void get(ComicSource source, IComicRequest requestValues, ComicDataCallback callback) {
        int type = requestValues.getDataSourceType();
        ComicParseStrategy comicParseStrategy = source.getStrategy(type);
        if (comicParseStrategy == null) {
            callback.onError(new NullPointerException("This strategy is null!"));
            return;
        }
        try {
            Request request = comicParseStrategy.buildRequest(requestValues);
            HHLogger.i(TAG, request.url().toString());
            OkHttpClient client = HHEngine.getConfiguration(ConfigKeys.OKHTTP_CLIENT);
            Response response = client.newCall(request).execute();
            IComicResponse comicResponse = new ComicResponseValues();
            if (!response.isSuccessful() || response.code() >= 300 || response.code() <= 199) {
                throw new IOException("OKHttp connect no successful!");
            }
            byte[] data = response.body().bytes();
            callback.onSuccess(comicParseStrategy.parseData(comicResponse, data));
        } catch (IOException | NullPointerException e) {
            callback.onError(e);
        }
    }

    @Deprecated
    @Override
    public void saveComic(Comic comic, ComicDataState state) {

    }

    @Deprecated
    @Override
    public void deleteComic(ComicSource source, String comicId, ComicDataState state) {

    }

    @Deprecated
    @Override
    public void deleteComic(Comic comic, ComicDataState state) {

    }

    @Deprecated
    @Override
    public void deleteComicsRange(ComicSource source, String[] comicIds, ComicDataState state) {

    }

    @Deprecated
    @Override
    public void deleteComicsRange(List<Comic> comics, ComicDataState state) {

    }

    @Override
    public void checkMarkComicUpdate(ComicDataCallback callback) {
        //TODO 检查收藏的漫画更新
    }

}
