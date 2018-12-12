/*
 * Copyright 2016-2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.hhcomic.model.comic.service.requestmanager;

import org.huxizhijian.core.app.ConfigKeys;
import org.huxizhijian.hhcomic.model.comic.HHComic;

/**
 * RequestManager的简单工厂
 *
 * @author huxizhijian
 * @date 2018/9/27
 */
public class RxRequestManagerFactory {

    private RxRequestManagerFactory() {
    }

    public static RxCategoryRequestManager category() {
        return new RxCategoryRequestManager(HHComic.getConfiguration(ConfigKeys.OKHTTP_CLIENT));
    }

    public static RxChapterImageRequestManager chapterImage() {
        return new RxChapterImageRequestManager(HHComic.getConfiguration(ConfigKeys.OKHTTP_CLIENT));
    }

    public static RxComicInfoRequestManager comicInfo() {
        return new RxComicInfoRequestManager(HHComic.getConfiguration(ConfigKeys.OKHTTP_CLIENT));
    }

    public static RxRankAndRecommendRequestManager rankAndRecommend() {
        return new RxRankAndRecommendRequestManager(HHComic.getConfiguration(ConfigKeys.OKHTTP_CLIENT));
    }

    public static RxSearchComicRequestManager search() {
        return new RxSearchComicRequestManager(HHComic.getConfiguration(ConfigKeys.OKHTTP_CLIENT));
    }
}
