package org.huxizhijian.hhcomic.model.comic;

import android.content.Context;

import org.huxizhijian.core.app.ConfigKeys;
import org.huxizhijian.core.app.HHGlobalVariable;
import org.huxizhijian.generate.SourceRouterApp;
import org.huxizhijian.hhcomic.model.comic.config.HHComicConfig;
import org.huxizhijian.hhcomic.model.comic.config.SourceConfig;
import org.huxizhijian.hhcomic.model.comic.db.HHDatabase;
import org.huxizhijian.hhcomic.model.comic.db.dao.ChapterDao;
import org.huxizhijian.hhcomic.model.comic.db.dao.ComicDao;
import org.huxizhijian.hhcomic.model.comic.service.requestmanager.RxCategoryRequestManager;
import org.huxizhijian.hhcomic.model.comic.service.requestmanager.RxChapterImageRequestManager;
import org.huxizhijian.hhcomic.model.comic.service.requestmanager.RxComicInfoRequestManager;
import org.huxizhijian.hhcomic.model.comic.service.requestmanager.RxRankAndRecommendRequestManager;
import org.huxizhijian.hhcomic.model.comic.service.requestmanager.RxRequestManagerFactory;
import org.huxizhijian.hhcomic.model.comic.service.requestmanager.RxSearchComicRequestManager;
import org.huxizhijian.hhcomic.model.comic.service.source.base.Source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import androidx.room.Room;

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
     * 初始化core集成的第三方库和一些全局变量
     *
     * @param context context
     */
    public static void init(Context context) {
        HHGlobalVariable.init(context)
                .withConnectTimeOut(15000, TimeUnit.MILLISECONDS)
                .configure();
    }

    /**
     * 全局OkHttpClient等第三方库配置信息
     *
     * @param key config keys
     * @param <T> 返回类型随ConfigKeys不同而不同
     * @return 结果
     */
    public static <T> T getConfiguration(ConfigKeys key) {
        return HHGlobalVariable.getConfiguration(key);
    }

    /**
     * 获取源id列表，这个列表如果用户排过序，则结果为排序后的列表列表
     *
     * @return source key list
     */
    public static List<SourceConfig> getSourceConfigList() {
        List<SourceConfig> sourceConfigList = config().getSourceConfigs();
        if (sourceConfigList == null) {
            Map<String, String> sourceKeyNameMap = SourceRouterApp.getInstance().getSourceKeyNameMap();
            Set<String> keySet = sourceKeyNameMap.keySet();
            sourceConfigList = new ArrayList<>();
            SourceConfig sourceConfig;
            for (String key : keySet) {
                sourceConfig = new SourceConfig(key, sourceKeyNameMap.get(key), true);
                sourceConfigList.add(sourceConfig);
            }
        }
        return sourceConfigList;
    }

    /**
     * 获取对应source类（唯一）实例
     *
     * @param sourceKey 源唯一key
     * @return source
     */
    public static Source getSource(String sourceKey) throws IOException {
        return SourceRouterApp.getInstance().getSource(sourceKey);
    }

    public static DatabaseGuide db() {
        return DatabaseGuide.INSTANCE;
    }

    public enum DatabaseGuide {
        /**
         * 单例模式
         */
        INSTANCE;

        /**
         * 创建数据库实例，应遵循单例设计模式，因为每个RoomDatabase实例都相当消耗性能，并且您很少需要访问多个实例。
         */
        private HHDatabase mDatabase = Room.databaseBuilder(HHGlobalVariable.getApplicationContext(),
                HHDatabase.class, "hh-comic").build();

        public ComicDao comicDao() {
            return mDatabase.comicDao();
        }

        public ChapterDao chapterDao() {
            return mDatabase.chapterDao();
        }
    }

    public static HHComicConfig config() {
        return HHComicConfig.getInstance();
    }

    /**
     * 获取model的服务，即使用源的各个功能
     *
     * @return service guide
     */
    public static ServiceGuide service() {
        return ServiceGuide.INSTANCE;
    }

    /**
     * 源的网络服务功能导航
     */
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