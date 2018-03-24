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

package org.huxizhijian.hhcomic.comic.repository;

import org.huxizhijian.hhcomic.comic.bean.Comic;
import org.huxizhijian.hhcomic.comic.source.base.ComicSource;
import org.huxizhijian.hhcomic.comic.type.ComicDataState;
import org.huxizhijian.hhcomic.comic.value.IComicRequest;
import org.huxizhijian.hhcomic.comic.value.IComicResponse;

import java.util.List;

/**
 * Comic数据仓库接口，实现类见{@link ComicRepository}，Comic实体类{@link Comic}
 *
 * @author huxizhijian
 * @date 2017/9/21
 */
public interface ComicDataSource {

    /**
     * 结果回调接口
     */
    interface ComicDataCallback {
        /**
         * 完成回调
         *
         * @param responseValues 结果
         */
        void onSuccess(IComicResponse responseValues);

        /**
         * 异常回调
         *
         * @param throwable 异常信息
         */
        void onError(Throwable throwable);

        /**
         * 资源不存在回调
         */
        void onDataNotAvailable();
    }

    /**
     * 获取单个或者多个Comic，具体根据{@link IComicRequest}的实现类封装的参数决定
     *
     * @param source        漫画来源
     * @param requestValues 请求参数
     * @param callback      回调
     */
    void get(ComicSource source, IComicRequest requestValues, ComicDataCallback callback);

    /**
     * 保存一个Comic到DB或者云端中，如果已经存在，改变其状态
     *
     * @param comic 保存的Comic
     * @param state 状态
     */
    void saveComic(Comic comic, ComicDataState state);

    /**
     * 将一个Comic从DB或者云端中删除
     *
     * @param source  漫画来源
     * @param comicId 来源网站标识的唯一id
     * @param state   回调
     */
    void deleteComic(ComicSource source, String comicId, ComicDataState state);

    /**
     * 将一个Comic从DB或者云端中删除
     *
     * @param comic Comic实体类
     * @param state 回调
     */
    void deleteComic(Comic comic, ComicDataState state);

    /**
     * 从DB或者云端中删除多个Comic
     *
     * @param source   漫画来源
     * @param comicIds 需要删除的Comic的来源网站唯一编号+来源网站标识的唯一id
     * @param state    删除的Comic的保存状态
     */
    void deleteComicsRange(ComicSource source, String[] comicIds, ComicDataState state);

    /**
     * 从DB或者远端中删除多个Comic
     *
     * @param comics 需要删除的Comic
     * @param state  删除的Comic保存状态
     */
    void deleteComicsRange(List<Comic> comics, ComicDataState state);

    /**
     * 检查收藏列表的漫画更新
     *
     * @param callback 回调
     */
    void checkMarkComicUpdate(ComicDataCallback callback);

}
