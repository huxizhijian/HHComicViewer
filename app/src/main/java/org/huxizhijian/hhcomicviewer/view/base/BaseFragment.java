package org.huxizhijian.hhcomicviewer.view.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * 基类Fragment，封装了懒加载功能等
 *
 * @author huxizhijian
 * @date 2018/11/20
 */
public abstract class BaseFragment extends Fragment {

    private View mRootView;

    protected FragmentActivity mActivity;

    /**
     * 是否第一次可见（用于懒加载）
     */
    protected boolean mIsFirstVisible = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutId(), null, false);
        initData();
        initView(mRootView);
        return mRootView;
    }

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化view
     *
     * @param rootView 根布局
     */
    protected abstract void initView(View rootView);

    /**
     * 子类的布局文件id
     *
     * @return layout id
     */
    protected abstract int getLayoutId();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean isVis = isHidden() || getUserVisibleHint();
        if (isVis && mIsFirstVisible) {
            lazyLoad();
            mIsFirstVisible = false;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onVisible();
        } else {
            onInVisible();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onVisible();
        } else {
            onInVisible();
        }
    }

    /**
     * 当界面可见时的操作
     */
    protected void onVisible() {
        if (mIsFirstVisible && isResumed()) {
            lazyLoad();
            mIsFirstVisible = false;
        }
    }

    /**
     * 数据懒加载
     */
    protected void lazyLoad() {
    }

    /**
     * 当界面不可见时的操作
     */
    protected void onInVisible() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.mActivity = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (FragmentActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mActivity = null;
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T getViewById(int id) {
        return (T) mRootView.findViewById(id);
    }
}
