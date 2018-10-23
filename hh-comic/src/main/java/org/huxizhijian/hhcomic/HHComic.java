package org.huxizhijian.hhcomic;

import org.huxizhijian.generate.SourceRouterApp;
import org.huxizhijian.hhcomic.service.requestmanager.RxCategoryRequestManager;
import org.huxizhijian.hhcomic.service.requestmanager.RxChapterImageRequestManager;
import org.huxizhijian.hhcomic.service.requestmanager.RxComicInfoRequestManager;
import org.huxizhijian.hhcomic.service.requestmanager.RxRankAndRecommendRequestManager;
import org.huxizhijian.hhcomic.service.requestmanager.RxRequestManagerFactory;
import org.huxizhijian.hhcomic.service.requestmanager.RxSearchComicRequestManager;
import org.huxizhijian.hhcomic.service.source.base.Source;

import java.io.IOException;
import java.util.List;

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
     * @return source key list
     */
    public static List<String> getSourceKeyList() {
        return SourceRouterApp.getInstance().getSourceKeyList();
    }

    /**
     * 获取对应source类（唯一）实例
     *
     * @param sourceId 源唯一id
     * @return source
     */
    public static Source getSource(String sourceId) throws IOException {
        return SourceRouterApp.getInstance().getSource(sourceId);
    }

    /**
     * 获取model的服务，即使用源的各个功能
     *
     * @return service guide
     */
    public static ServiceGuide service() {
        return ServiceGuide.INSTANCE;
    }

    public enum ServiceGuide {

        /**
         * 使用枚举类实现单例模式，这通常是实现单例的最佳方式。
         * 提供了免费的序列化机制，并提供了针对多个实例化的坚固保证，即使是在复杂的序列化或反射攻击的情况下。
         */
        INSTANCE;

        /**
         * 源的分类列表结果网络请求管理器
         *
         * @return manager
         */
        public RxCategoryRequestManager category() {
            return RxRequestManagerFactory.category();
        }

        /**
         * 源的章节图片列表获取网络请求管理器
         *
         * @return manager
         */
        public RxChapterImageRequestManager chapterImage() {
            return RxRequestManagerFactory.chapterImage();
        }

        /**
         * 源的漫画详情获取网络请求管理器
         *
         * @return manager
         */
        public RxComicInfoRequestManager comicInfo() {
            return RxRequestManagerFactory.comicInfo();
        }

        /**
         * 源的排行榜以及推荐结果列表获取网络请求管理器
         *
         * @return manager
         */
        public RxRankAndRecommendRequestManager rankAndRecommend() {
            return RxRequestManagerFactory.rankAndRecommend();
        }

        /**
         * 源的搜索结果列表获取网络请求管理器
         *
         * @return manager
         */
        public RxSearchComicRequestManager search() {
            return RxRequestManagerFactory.search();
        }
    }
}
