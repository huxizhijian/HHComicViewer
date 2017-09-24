/*
 * Copyright 2017 huxizhijian
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

package org.huxizhijian.hhcomicviewer2.ui.download;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.DownloadedComicChapterAdapter;
import org.huxizhijian.hhcomicviewer2.databinding.ActivityComicChapterDownloadBinding;
import org.huxizhijian.hhcomicviewer2.model.Comic;
import org.huxizhijian.hhcomicviewer2.model.ComicChapter;
import org.huxizhijian.hhcomicviewer2.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ComicChapterDownloadActivity extends OfflineDownloadBaseActivity {

    private ActivityComicChapterDownloadBinding mBinding;
    private Comic mComic;
    private List<ComicChapter> mComicChapters;
    private DownloadedComicChapterAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_comic_chapter_download);
        setSupportActionBar(mBinding.toolbar);
        Intent intent = getIntent();
        mComic = (Comic) intent.getSerializableExtra("comic");
        initSupportAppBar(mComic.getTitle());
        //设置过渡动画
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(buildEnterTransition());
        }
        //设置事件
        mBinding.btnAllSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter != null) {
                    mAdapter.selectAll();
                }
            }
        });
        mBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter != null) {
                    mAdapter.deleteClick();
                }
            }
        });
    }

    @Override
    protected void editModeOpen() {
        if (mAdapter != null) {
            mAdapter.openEditMode(-1);
        }
    }

    @Override
    protected void editModeClose() {
        if (mAdapter != null) {
            mAdapter.closeEditMode();
        }
    }

    @Override
    protected boolean isEditModeOpen() {
        return mAdapter != null && mAdapter.isEditModeOn();
    }

    @Override
    protected void initData() {
        List<ComicChapter> allChapters = mComicChapterDBHelper.findByComicCid(mComic.getCid());
        mComicChapters = new ArrayList<>();
        for (ComicChapter chapter : allChapters) {
            if (chapter.getDownloadStatus() == Constants.DOWNLOAD_FINISHED) {
                mComicChapters.add(chapter);
            }
        }
    }

    @Override
    protected void initView() {
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new DownloadedComicChapterAdapter(this, mComicChapters, mComic);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onDownloadStateChanged(int downloadState, ComicChapter comicChapter) {
        if (downloadState == Constants.DOWNLOAD_FINISHED) {
            if (mComic.getCid() == comicChapter.getCid()) {
                initData();
                if (mAdapter == null || !mAdapter.isEditModeOn()) {
                    initView();
                }
            }
        }
    }

    @Override
    public void onEditModeOpen() {
        mBinding.llComicDownload.setVisibility(View.VISIBLE);
        invalidateOptionsMenu(); //重新绘制menu
    }

    @Override
    public void onEditModeClose() {
        mBinding.llComicDownload.setVisibility(View.GONE);
        invalidateOptionsMenu(); //重新绘制menu
        initView();
    }

    @Override
    public void onAllSelected() {
        mBinding.btnAllSelect.setText("取消");
    }

    @Override
    public void onNoAllSelected() {
        mBinding.btnAllSelect.setText("全选");
    }
}
