/*
 * Copyright 2018 huxizhijian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.huxizhijian.hhcomicviewer.ui.debug;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.huxizhijian.core.util.log.HHLogger;
import org.huxizhijian.hhcomic.comic.bean.Comic;
import org.huxizhijian.hhcomic.comic.source.base.SourceEnum;
import org.huxizhijian.hhcomicviewer.R;
import org.huxizhijian.hhcomicviewer.base.Injection;
import org.huxizhijian.sdk.imageloader.ImageLoaderOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试Activity
 *
 * @author huxizhijian
 * @date 2017/11/9
 */
public class DebugActivity extends AppCompatActivity implements GetRecommendsContract.View {

    private static final String TAG = DebugActivity.class.getSimpleName();

    private GetRecommendsContract.Presenter mPresenter;
    private RecyclerView mRecyclerView;
    private RecommendAdapter mAdapter;
    private List<Comic> mComicList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        initView();
        mPresenter = new GetRecommendsPresenter(Injection.provideComicRepository(), this, Injection.provideUseCaseHandler(),
                Injection.provideGetRecommendsUseCase(), SourceEnum.HHManHua.hashCode());
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
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
        HHLogger.i(TAG, "获取成功");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mComicList.clear();
        if (comicList != null && comicList.size() != 0) {
            mComicList.addAll(comicList);
        }
        mAdapter = new RecommendAdapter(R.layout.item_list_view, mComicList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onError() {
        HHLogger.e(TAG, "发生了错误！");
        Toast.makeText(this, "出错！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyList() {
        HHLogger.e(TAG, "获取列表为空");
        mComicList.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetNextPageSuccess(List<Comic> comicList, boolean hasMore) {
        HHLogger.d(comicList);
        if (comicList != null && comicList.size() != 0) {
            mComicList.addAll(comicList);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNoMore() {
        Toast.makeText(this, "没有更多啦", Toast.LENGTH_SHORT).show();
    }

    private class RecommendAdapter extends BaseQuickAdapter<Comic, BaseViewHolder> {

        public RecommendAdapter(int layoutResId, @Nullable List<Comic> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Comic item) {
            ImageView imageView = helper.getView(R.id.imageView_item);
            ImageLoaderOptions.getImageLoaderManager().displayThumbnail(DebugActivity.this, item.getCover(), imageView,
                    R.mipmap.blank, R.mipmap.blank, 150, 220);
            helper.setText(R.id.tv_title_item, item.getTitle());
            helper.setText(R.id.tv_description_item, item.getIntro());
            helper.setText(R.id.tv_read_info_item, item.getAuthor());
        }
    }
}
