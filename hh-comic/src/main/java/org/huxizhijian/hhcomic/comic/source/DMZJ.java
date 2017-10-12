package org.huxizhijian.hhcomic.comic.source;

import org.huxizhijian.hhcomic.comic.source.base.ComicSource;
import org.huxizhijian.hhcomic.comic.source.base.Source;

/**
 * TODO 动漫之家解析类
 *
 * @Author huxizhijian on 2017/10/12.
 */

public class DMZJ extends ComicSource {

    private static final String SOURCE_NAME = "动漫之家";
    private static final String API_BASE_URL = "v2.api.dmzj.com";

    private static final int SOURCE_TYPE = Source.DMZJ;

    @Override
    public String setSourceName() {
        return SOURCE_NAME;
    }

    @Override
    public String setBaseUrl() {
        return API_BASE_URL;
    }

    @Override
    public int getSourceType() {
        return SOURCE_TYPE;
    }

    public DMZJ() {
        // 没有任何鸟用的构造方法
    }

    /**
     * 默认添加所有策略，也可以自行添加
     */
    public ComicSource defaultConfig() {
        return this;
    }

}
