package org.huxizhijian.hhcomicviewer.view.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 基础Activity
 *
 * @author huxizhijian
 * @date 2018/11/20
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        // 初始化数据
        initData(savedInstanceState);
        // 初始化控件
        initView(savedInstanceState);
    }

    /**
     * 初始化数据
     *
     * @param savedInstanceState 帮助恢复意外关闭的activity的bundle
     */
    protected abstract void initData(Bundle savedInstanceState);

    /**
     * 初始化view
     *
     * @param savedInstanceState 帮助恢复意外关闭的activity的bundle
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 子类的布局文件id
     *
     * @return layout id
     */
    protected abstract int getLayoutId();
}
