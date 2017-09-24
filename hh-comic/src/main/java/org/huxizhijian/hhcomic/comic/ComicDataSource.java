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

package org.huxizhijian.hhcomic.comic;

import org.huxizhijian.hhcomic.comic.bean.Comic;
import org.huxizhijian.hhcomic.comic.parser.BaseParser;
import org.huxizhijian.hhcomic.comic.type.ComicDataState;

import java.util.List;

/**
 * Comic数据仓库接口，实现类见{@link ComicRepository}，Comic实体类{@link Comic}
 *
 * @author huxizhijian 2017/9/21
 */
public interface ComicDataSource {

    /**
     * 单个Comic获取回调
     */
    interface GetComicCallback {
        void onComicLoaded(Comic comic);

        void onDataNotAvailable();
    }

    /**
     * Comic集合获取回调
     */
    interface GetComicsCallback {
        void onComicsLoaded(List<Comic> comics, int page, int size);

        void onDataNotAvailable();
    }

    /**
     * 获取单个Comic
     *
     * @param sComicId    来源网站唯一编号+来源网站标识的唯一id
     * @param checkUpdate 是否使用网络检查其更新（如果有DB保存亦不立即返回）
     * @param callback    回调
     */
    void getComic(long sComicId, boolean checkUpdate, GetComicCallback callback);

    /**
     * 从网络或者本地获取到Comic的集合
     *
     * @param type     ComicDataSourceType或者其子类标注的常量值，通常为来源网站类{@link BaseParser}的子类的内部类实现
     * @param page     页码
     * @param size     每页返回的Comic数，通常不管用
     * @param callback 回调
     */
    void getComics(int type, int page, int size, GetComicsCallback callback);

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
     * @param sComicId 来源网站唯一编号+来源网站标识的唯一id
     * @param state    回调
     */
    void deleteComic(long sComicId, ComicDataState state);

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
     * @param sComicIds 需要删除的Comic的来源网站唯一编号+来源网站标识的唯一id
     * @param state     删除的Comic的保存状态
     */
    void deleteComicsRange(long[] sComicIds, ComicDataState state);

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
    void checkMarkComicUpdate(GetComicsCallback callback);

}
