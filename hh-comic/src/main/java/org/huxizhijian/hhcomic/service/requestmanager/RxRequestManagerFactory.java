package org.huxizhijian.hhcomic.service.requestmanager;

import org.huxizhijian.core.app.ConfigKeys;
import org.huxizhijian.hhcomic.HHComic;

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
