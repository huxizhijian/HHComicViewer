package org.huxizhijian.hhcomicviewer.view.base;

import android.os.Bundle;

import org.huxizhijian.hhcomic.viewmodel.base.ComicViewModel;

import java.lang.reflect.ParameterizedType;

import androidx.annotation.Nullable;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataObserver();
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(getClassType());
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
    }

    /**
     * 数据观察者的绑定，在初始化数据和视图之后
     */
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
