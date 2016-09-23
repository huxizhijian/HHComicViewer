package org.huxizhijian.hhcomicviewer2.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import org.huxizhijian.hhcomicviewer2.R;

/**
 * 下拉刷新的ListView
 * Created by wei on 2016/8/17.
 */
public class LoadPageListView extends ListView implements AbsListView.OnScrollListener {

    private View mFooter;
    private int mTotalItemCount; //总的数量
    private int mLastVisibleItem;//最后一个可见的Item
    private boolean mIsLoading; //是否正在加载
    private ILoaderListener mLoaderListener;
    private LayoutInflater mInflater;

    public void setLoaderListener(ILoaderListener loaderListener) {
        this.mLoaderListener = loaderListener;
    }

    public LoadPageListView(Context context) {
        super(context);
        initView(context);
    }

    public LoadPageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoadPageListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化界面，添加底部布局文件到ListView
     */
    public void initView(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void addFootView(boolean needFootView) {
        if (needFootView) {
            mFooter = mInflater.inflate(R.layout.foot_layout, null);
            this.setOnScrollListener(this);
            //设置底部布局
            mFooter.findViewById(R.id.loader_layout).setVisibility(GONE);
            this.addFooterView(mFooter);
        } else {
            if (mFooter != null) {
                this.removeFooterView(mFooter);
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //判断是否最后一个，并且滚动状态为滚动停止
        if (mTotalItemCount == mLastVisibleItem
                && scrollState == SCROLL_STATE_IDLE) {
            if (!mIsLoading) {
                mFooter.findViewById(R.id.loader_layout).setVisibility(VISIBLE);
                mIsLoading = true;
                //加载更多
                if (mLoaderListener != null) {
                    mLoaderListener.onLoad();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.mLastVisibleItem = firstVisibleItem + visibleItemCount;
        if (this.mTotalItemCount != totalItemCount) {
            this.mTotalItemCount = totalItemCount;
        }
    }

    public void loadComplete() {
        mIsLoading = false;
        mFooter.findViewById(R.id.loader_layout).setVisibility(GONE);
    }

    //加载更多数据的回调接口
    public interface ILoaderListener {
        void onLoad();
    }
}
