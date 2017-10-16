package org.huxizhijian.hhcomic.comic.value;

import java.util.Map;

/**
 * 调用策略的请求参数
 *
 * @author huxizhijian
 * @date 2017/10/9
 */
public interface IComicRequest {

    /**
     * 获取请求的{@link org.huxizhijian.hhcomic.comic.type.ComicDataSourceType}
     *
     * @return type
     */
    int getRequestType();

    /**
     * 设置请求的{@link org.huxizhijian.hhcomic.comic.type.ComicDataSourceType}
     */
    IComicRequest setRequestType(int type);

    /**
     * @param key   {@link org.huxizhijian.hhcomic.comic.type.RequestFieldType}标记的key值
     * @param field 传入的数据
     * @return 链式调用
     */
    IComicRequest addField(int key, Object field);

    /**
     * 获取数据
     *
     * @param key {@link org.huxizhijian.hhcomic.comic.type.RequestFieldType}标记的key值
     * @return 数据
     */
    <T> T getField(int key);

    /**
     * 参入一个map作为参数
     *
     * @param fields 参数map
     * @return 链式调用
     */
    IComicRequest addAllField(Map<Integer, Object> fields);

    /**
     * 获取所有参数
     *
     * @return 参数map
     */
    Map<?, ?> getFields();

}
