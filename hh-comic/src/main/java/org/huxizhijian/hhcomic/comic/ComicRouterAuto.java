package org.huxizhijian.hhcomic.comic;

import android.util.SparseArray;

/**
 * @author huxizhijian
 * @date 2018/3/31
 */
public class ComicRouterAuto {

    /**
     * 这里使用类名的大写作为名称,此后可能作为唯一ID使用
     */
    public static final int DMZJ = 1;
    public static final int HHMANHUA = 2;

    private final SparseArray<String> mSourceName = new SparseArray<>();

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
        // 这里加入Source注册的名字,作为向外公式的名字
        mSourceName.put(DMZJ, "动漫之家");
    }
}
