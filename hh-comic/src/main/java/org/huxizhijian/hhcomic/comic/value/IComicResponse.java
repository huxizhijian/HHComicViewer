package org.huxizhijian.hhcomic.comic.value;

import java.util.Map;

/**
 * 调用策略时候的返回结果
 *
 * @author huxizhijian
 * @date 2017/10/9
 */
public interface IComicResponse {

    /**
     * 获取结果，通常为{@link org.huxizhijian.hhcomic.comic.bean.Comic}或者它的List集合
     *
     * @return Comic或其集合或Null
     */
    <T> T  getResponse();

    /**
     * 设置返回结果
     *
     * @param field Comic或其集合
     * @return 链式调用
     */
    IComicResponse setResponse(Object field);

    /**
     * 添加返回结果
     *
     * @param key   {@link org.huxizhijian.hhcomic.comic.type.ResponseFieldType}标识的key
     * @param field 结果类
     * @return 链式调用
     */
    IComicResponse addField(int key, Object field);

    /**
     * 获取返回结果
     *
     * @param key {@link org.huxizhijian.hhcomic.comic.type.ResponseFieldType}标识的key
     * @param <T> 结果
     * @return 链式调用
     */
    <T> T getField(int key);

    /**
     * 添加返回结果map集合
     *
     * @param fields 结果map
     * @return 链式调用
     */
    IComicResponse addAllField(Map<Integer, Object> fields);

    /**
     * 获取返回结果map结合
     *
     * @return 结果map
     */
    Map<?, ?> getFields();

}
