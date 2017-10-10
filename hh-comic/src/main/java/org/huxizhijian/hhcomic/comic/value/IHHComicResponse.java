package org.huxizhijian.hhcomic.comic.value;

/**
 * 调用策略时候的返回结果
 *
 * @Author huxizhijian on 2017/10/9.
 */

public interface IHHComicResponse {

    Object getResponse();

    void setResponse(Object field);

    void addField(int key, Object field);

    <T> T getField(int key);

}
