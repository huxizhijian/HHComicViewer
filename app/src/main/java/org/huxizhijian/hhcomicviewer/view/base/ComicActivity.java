package org.huxizhijian.hhcomicviewer.view.base;

import android.os.Bundle;

import org.huxizhijian.hhcomic.viewmodel.base.ComicViewModel;

import java.lang.reflect.ParameterizedType;

import androidx.lifecycle.ViewModelProviders;

/**
 * 结合了ComicViewModel的基类
 *
 * @author huxizhijian
 * @date 2018/11/21
 */
public abstract class ComicActivity<T extends ComicViewModel> extends BaseActivity {

    protected T mViewModel;

    @Override
    protected void initView(Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(getClassType());
        dataObserver();
    }

    protected void dataObserver() {
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
