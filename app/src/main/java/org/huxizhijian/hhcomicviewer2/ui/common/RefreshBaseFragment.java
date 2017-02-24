package org.huxizhijian.hhcomicviewer2.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * lazy load base fragment abstract class
 * Created by wei on 2017/1/3.
 */

public abstract class RefreshBaseFragment extends Fragment {

    protected Context mContext;
    protected View mViewRoot;

    protected boolean isPrepared = false;
    protected boolean isVisible = false;
    protected boolean isFirst = true;

    public RefreshBaseFragment() {
        //Empty
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            lazyLoad();
        } else {
            isVisible = false;
            onInVisible();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mViewRoot == null) {
            mViewRoot = initView(inflater, container, savedInstanceState);
        }
        return mViewRoot;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
        lazyLoad();
    }

    /**
     * fragment lazy load
     */
    protected void lazyLoad() {
        refreshLoad();
        if (!isPrepared || !isVisible || !isFirst) {
            return;
        }
        Log.d("TAG", getClass().getName() + "->initData()");
        initData();
        isFirst = false;
    }

    /**
     * fragment refresh
     */
    protected void refreshLoad() {
        if (!isPrepared || !isVisible || isFirst) {
            return;
        }
        Log.d("TAG", getClass().getName() + "->refreshData()");
        refreshData();
    }

    /**
     * create content view
     *
     * @return mViewRoot
     */
    public abstract View initView(LayoutInflater inflater, @Nullable ViewGroup container,
                                  @Nullable Bundle savedInstanceState);

    /**
     * loading data in web
     */
    public abstract void initData();

    /**
     * refresh data when it change
     */
    public abstract void refreshData();

    /**
     * onInvisible
     */
    public abstract void onInVisible();
}
