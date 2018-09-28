package org.huxizhijian.hhcomic.comic;

import org.huxizhijian.hhcomic.comic.request.RxChapterImageRequestManager;
import org.huxizhijian.hhcomic.comic.request.RxComicInfoRequestManager;
import org.huxizhijian.hhcomic.comic.request.RxRankComicRequestManager;
import org.huxizhijian.hhcomic.comic.request.RxRequestManagerFactory;
import org.huxizhijian.hhcomic.comic.request.RxCategoryRequestManager;
import org.huxizhijian.hhcomic.comic.request.RxSearchComicRequestManager;
import org.huxizhijian.hhcomic.comic.source.Source;

/**
 * 工具类
 *
 * @author huxizhijian
 * @date 2018/9/25
 */
public class HHComic {

    /**
     * 采用私有构造方法，该类仅仅集合了comic的所有model功能
     */
    private HHComic() {
    }

    /**
     * 获取源id列表，这个列表如果用户排过序，则获取那个列表
     *
     * @return source ids
     */
    public static String[] getSourceIdList() {
        return null;
    }

    /**
     * 获取对应source类（唯一）实例
     *
     * @param sourceId 源唯一id
     * @return source
     */
    public static Source getSource(String sourceId) {
        return null;
    }

    /**
     * 生成对应Category功能的RequestManager
     *
     * @return rx category request manager
     */
    public static RxCategoryRequestManager category() {
        return RxRequestManagerFactory.category();
    }

    public static RxChapterImageRequestManager chapterImage() {
        return RxRequestManagerFactory.chapterImage();
    }

    public static RxComicInfoRequestManager comicInfo() {
        return RxRequestManagerFactory.comicInfo();
    }

    public static RxRankComicRequestManager rank() {
        return RxRequestManagerFactory.rank();
    }

    public static RxSearchComicRequestManager search() {
        return RxRequestManagerFactory.search();
    }
}
