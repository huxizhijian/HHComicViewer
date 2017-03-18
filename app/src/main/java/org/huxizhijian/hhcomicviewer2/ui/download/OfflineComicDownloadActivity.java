/*
 * Copyright 2016-2017 huxizhijian
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

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseArray;
import android.view.View;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.DownloadShowAdapter;
import org.huxizhijian.hhcomicviewer2.databinding.ActivityOfflineComicDownloadBinding;
import org.huxizhijian.hhcomicviewer2.model.Comic;
import org.huxizhijian.hhcomicviewer2.model.ComicChapter;
import org.huxizhijian.hhcomicviewer2.utils.CommonUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class OfflineComicDownloadActivity extends OfflineDownloadBaseActivity {

    //view
    private ActivityOfflineComicDownloadBinding mBinding;
    private DownloadShowAdapter mAdapter;

    //data
    private List<Comic> mFinishedComicList;
    private SparseArray<List<ComicChapter>> mDownloadedChapterList;
    private List<ComicChapter> mUnFinishedChapterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_offline_comic_download);
        setSupportActionBar(mBinding.toolbar);
        initSupportAppBar("下载管理");
        //设置过渡动画
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(buildEnterTransition());
        }
        //按钮事件注册
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
                    mAdapter.deleteComic();
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
        List<Comic> downloadComicList = mComicDBHelper.findDownloadedComics();
        mFinishedComicList = mComicChapterDBHelper.findFinishedComicList(downloadComicList);
        mDownloadedChapterList = mComicChapterDBHelper.findDownloadedChapterMap(mFinishedComicList);
        mUnFinishedChapterList = mComicChapterDBHelper.findUnFinishedChapters();
    }

    @Override
    protected void initView() {
        if (mFinishedComicList == null) {
            mFinishedComicList = new ArrayList<>();
        }
        if (mAdapter != null) {
            mAdapter.setDownloadedChapterList(mDownloadedChapterList);
            mAdapter.setFinishedComicList(mFinishedComicList);
            mAdapter.setUnFinishedChapterList(mUnFinishedChapterList);
            mAdapter.notifyDataSetChanged();
            return;
        }
        mAdapter = new DownloadShowAdapter(this,
                mDownloadedChapterList, mFinishedComicList, mUnFinishedChapterList, this);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerView.setAdapter(mAdapter);
        //剩余空间显示
        mBinding.cacheSizeText.setText(CommonUtils.getStorageBlockSpace(mDownloadPath));
        mBinding.progressBar.setProgress(CommonUtils.getStorageBlockSpacePercent(mDownloadPath));
    }

    @Override
    protected void onDownloadStateChanged(int downloadState, ComicChapter comicChapter) {
        if (downloadState == Constants.DOWNLOAD_FINISHED) {
            initData();
            if (mAdapter == null || !mAdapter.isEditModeOn()) {
                initView();
            }
        }
    }

    @Override
    public void onEditModeOpen() {
        mBinding.frameProgressBar.setVisibility(View.GONE);
        mBinding.llComicDownload.setVisibility(View.VISIBLE);
        invalidateOptionsMenu(); //重新绘制menu
    }

    @Override
    public void onEditModeClose() {
        mBinding.frameProgressBar.setVisibility(View.VISIBLE);
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
