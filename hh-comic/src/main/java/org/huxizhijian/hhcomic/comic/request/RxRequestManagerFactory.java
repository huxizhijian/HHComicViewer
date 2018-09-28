package org.huxizhijian.hhcomic.comic.request;

import org.huxizhijian.core.app.ConfigKeys;
import org.huxizhijian.core.app.HHGolbalVariable;

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
        return new RxCategoryRequestManager(HHGolbalVariable.getConfiguration(ConfigKeys.OKHTTP_CLIENT));
    }

    public static RxChapterImageRequestManager chapterImage() {
        return new RxChapterImageRequestManager(HHGolbalVariable.getConfiguration(ConfigKeys.OKHTTP_CLIENT));
    }

    public static RxComicInfoRequestManager comicInfo() {
        return new RxComicInfoRequestManager(HHGolbalVariable.getConfiguration(ConfigKeys.OKHTTP_CLIENT));
    }

    public static RxRankComicRequestManager rank() {
        return new RxRankComicRequestManager(HHGolbalVariable.getConfiguration(ConfigKeys.OKHTTP_CLIENT));
    }

    public static RxSearchComicRequestManager search() {
        return new RxSearchComicRequestManager(HHGolbalVariable.getConfiguration(ConfigKeys.OKHTTP_CLIENT));
    }
}
