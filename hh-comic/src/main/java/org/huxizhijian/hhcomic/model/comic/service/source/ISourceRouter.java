package org.huxizhijian.hhcomic.model.comic.service.source;

import org.huxizhijian.hhcomic.model.comic.service.source.base.Source;

import java.io.IOException;
import java.util.Map;

/**
 * 源单例容器管理类接口
 *
 * @author huxizhijian
 * @date 2019/4/23
 */
public interface ISourceRouter {
    /**
     * 获取源列表
     *
     * @return map of source
     */
    Map<String, String> getSourceKeyNameMap();

    /**
     * 获取源实例
     *
     * @param sourceKey source key
     * @return source instance
     */
    Source getSource(String sourceKey) throws IOException;
}
