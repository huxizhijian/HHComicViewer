package org.huxizhijian.hhcomicviewer.ui.debug;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.huxizhijian.core.util.log.HHLogger;
import org.huxizhijian.hhcomic.comic.bean.Comic;
import org.huxizhijian.hhcomic.comic.source.base.Source;
import org.huxizhijian.hhcomicviewer.R;
import org.huxizhijian.hhcomicviewer.base.Injection;

import java.util.List;

/**
 * @author huxizhijian
 * @date 2017/11/9
 */
public class DebugActivity extends AppCompatActivity implements GetRecommendsContract.View {

    private static final String TAG = DebugActivity.class.getSimpleName();

    private GetRecommendsContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        mPresenter = new GetRecommendsPresenter(Injection.provideComicRepository(), this, Injection.provideUseCaseHandler(),
                Injection.provideGetRecommendsUseCase(), Source.HHManHua.hashCode());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(GetRecommendsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onGetComicList(List<Comic> comicList, boolean hasMore) {
        HHLogger.d(comicList);
    }

    @Override
    public void onError() {
        HHLogger.e(TAG, "发生了错误！");
    }

    @Override
    public void onEmptyList() {
        HHLogger.e(TAG, "获取列表为空");
    }

    @Override
    public void onGetNextPageSuccess(List<Comic> comicList, boolean hasMore) {
        HHLogger.d(comicList);
    }

    @Override
    public void onNoMore() {
        HHLogger.e(TAG, "发生了什么");
    }
}
