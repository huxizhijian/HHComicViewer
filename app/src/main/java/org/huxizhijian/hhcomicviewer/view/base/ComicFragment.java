package org.huxizhijian.hhcomicviewer.view.base;

import android.view.View;

import org.huxizhijian.hhcomic.viewmodel.base.ComicViewModel;

import java.lang.reflect.ParameterizedType;

import androidx.lifecycle.ViewModelProviders;

/**
 * 结合了ComicViewModel的基类
 *
 * @author huxizhijian
 * @date 2018/11/21
 */
public abstract class ComicFragment<T extends ComicViewModel> extends BaseFragment {

    protected T mViewModel;

    @Override
    protected void initView(View rootView) {
        if (useActivityViewModel()) {
            mViewModel = ViewModelProviders.of(mActivity).get(getClassType());
        } else {
            mViewModel = ViewModelProviders.of(this).get(getClassType());
        }
    }

    protected void dataObserver() {
    }

    /**
     * 是否使用Activity的ViewModel，这样可以在fragment之间共有一个ViewModel，默认不使用
     *
     * @return 是否使用Activity的ViewModel
     */
    protected boolean useActivityViewModel() {
        return false;
    }

    /**
     * 将T转换成Class类型数据，使用反射
     *
     * @return T的Class类型
     */
    @SuppressWarnings("unchecked cast")
    protected Class<T> getClassType() {
        return (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
    }
}
