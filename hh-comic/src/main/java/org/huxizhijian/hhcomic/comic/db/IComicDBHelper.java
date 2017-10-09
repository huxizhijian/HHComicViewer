package org.huxizhijian.hhcomic.comic.db;

import org.huxizhijian.hhcomic.comic.bean.Comic;

import java.util.List;

/**
 * @Author huxizhijian on 2017/9/26.
 */

public interface IComicDBHelper {

    // 最后阅读时间倒序
    public static final int ORDER_LAST_DESC = 0x0;
    // 最后阅读时间正序
    public static final int ORDER_LAST_ASC = 0x1;
    // 更新时间倒序
    public static final int ORDER_UPDATE_DESC = 0x0;
    // 更新时间正序
    public static final int ORDER_UPDATE_ASC = 0x1;

    void insert(Comic comic);

    void update(Comic comic);

    void delete(Comic comic);

    Comic get(int source, long comicId);

    List<Comic> getAll();

    /**
     * 获取历史
     *
     * @param order 排序方式
     */
    List<Comic> getHistoryList(int order);

    /**
     * 获取收藏列表
     *
     * @param order     排序方式
     * @param highLight 是否将标记为高亮的Comic排在前面
     */
    List<Comic> getFavoriteList(int order, boolean highLight);

    /**
     * 获取下载列表
     *
     * @param order 排序方式
     */
    List<Comic> getDownloadList(int order);

}
