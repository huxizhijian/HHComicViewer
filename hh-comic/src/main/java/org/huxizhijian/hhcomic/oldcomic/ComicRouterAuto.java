package org.huxizhijian.hhcomic.oldcomic;

import android.util.SparseArray;

import org.huxizhijian.hhcomic.comic.sources.base.Source;

/**
 * @author huxizhijian
 * @date 2018/3/31
 */
public class ComicRouterAuto {

    public static final int DMZJ = 1;
    public static final int HHMANHUA = 2;

    private final SparseArray<Source> mSourceArray = new SparseArray<>();
    private final SparseArray<String> mSourceNameArray = new SparseArray<>();

    private static ComicRouterAuto sComicRouterAuto;

    public static ComicRouterAuto getInstance() {
        //这是一个DoubleCheck的单例模式
        if (sComicRouterAuto == null) {
            synchronized (ComicRouterAuto.class) {
                if (sComicRouterAuto == null) {
                    sComicRouterAuto = new ComicRouterAuto();
                }
            }
        }
        return sComicRouterAuto;
    }

    private ComicRouterAuto() {
    }

    public String getSourceName(int type) {
        return mSourceNameArray.get(type);
    }
}
