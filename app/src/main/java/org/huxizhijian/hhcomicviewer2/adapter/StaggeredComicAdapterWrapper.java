package org.huxizhijian.hhcomicviewer2.adapter;

import android.support.v7.widget.RecyclerView;

import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

/**
 * @author huxizhijian 2017/3/25
 */
public class StaggeredComicAdapterWrapper extends LRecyclerViewAdapter {
    public StaggeredComicAdapterWrapper(RecyclerView.Adapter innerAdapter) {
        super(innerAdapter);
    }
}
