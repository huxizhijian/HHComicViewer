package org.huxizhijian.hhcomic.comic.value;

import java.util.Map;

/**
 * 调用策略时候的返回结果
 *
 * @Author huxizhijian on 2017/10/9.
 */

public interface IComicResponse {

    Object getResponse();

    IComicResponse setResponse(Object field);

    IComicResponse addField(int key, Object field);

    <T> T getField(int key);

    IComicResponse addAllField(Map<Integer, Object> fields);

    Map<?, ?> getFields();

}
