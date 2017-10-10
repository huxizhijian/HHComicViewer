package org.huxizhijian.hhcomic.comic.value;

/**
 * 调用策略的请求参数
 *
 * @Author huxizhijian on 2017/10/9.
 */

public interface IHHComicRequest {

    int getRequestType();

    void setRequestType(int type);

    void addField(int key, Object field);

    <T> T getField(int key);

}
