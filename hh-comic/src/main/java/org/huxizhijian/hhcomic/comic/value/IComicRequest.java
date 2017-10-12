package org.huxizhijian.hhcomic.comic.value;

import java.util.Map;

/**
 * 调用策略的请求参数
 *
 * @Author huxizhijian on 2017/10/9.
 */

public interface IComicRequest {

    int getRequestType();

    IComicRequest setRequestType(int type);

    IComicRequest addField(int key, Object field);

    <T> T getField(int key);

    IComicRequest addAllField(Map<Integer, Object> fields);

    Map<?, ?> getFields();

}
