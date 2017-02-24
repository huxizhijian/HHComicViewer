package org.huxizhijian.hhcomicviewer2.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;

/**
 * 自定义瀑布流manager，清除该版本的一个错误
 * Created by wei on 2017/1/20.
 */

public class MyStaggerLayoutManager extends StaggeredGridLayoutManager {

    public MyStaggerLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    public MyStaggerLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void collectAdjacentPrefetchPositions(int dx, int dy, RecyclerView.State state, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        try {
            super.collectAdjacentPrefetchPositions(dx, dy, state, layoutPrefetchRegistry);
        } catch (IllegalArgumentException e) {
            Log.e("MyStaggerLayoutManager", "collectAdjacentPrefetchPositions: ", e);
        }
    }
}