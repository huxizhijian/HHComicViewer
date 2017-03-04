/*
 * Copyright 2016 huxizhijian
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

package org.huxizhijian.hhcomicviewer2.ui.entry;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.app.SimpleDialog;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.StaggeredComicAdapter;
import org.huxizhijian.hhcomicviewer2.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer2.ui.common.RefreshBaseFragment;
import org.huxizhijian.hhcomicviewer2.model.Comic;
import org.huxizhijian.hhcomicviewer2.service.DownloadManagerService;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MarkedFragment extends RefreshBaseFragment {

    //控件
    private RecyclerView mRecyclerView;
    private TextView mTv; //提示没有收藏字符串
    private SwipeRefreshLayout mRefreshLayout;

    //数据
    private ComicDBHelper mComicDBHelper;
    private List<Comic> mMarkedComics;
    private StaggeredComicAdapter mAdapter = null;

    //删除收藏对话框
    private SimpleDialog mDialog;
    private View mDeleteMarkView;
    private CheckBox mCheckBox;

    public MarkedFragment() {
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marked, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_marked);
        mTv = (TextView) view.findViewById(R.id.textView_no_marked);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_marked);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.pink_500,
                R.color.purple_500, R.color.blue_500);
        mComicDBHelper = ComicDBHelper.getInstance(getActivity());
        return view;
    }

    @Override
    public void initData() {
        mRefreshLayout.setRefreshing(true);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initRecyclerView();
                mRefreshLayout.setRefreshing(false);
            }
        });
        initRecyclerView();
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void refreshData() {
        mRefreshLayout.setRefreshing(true);
        initRecyclerView();
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onInVisible() {

    }

    @Override
    public void onResume() {
        super.onResume();
        //兼具初始设定view的作用
        loadData();
    }

    //刷新值
    public void loadData() {
        if (mAdapter == null) {
            initRecyclerView();
        } else {
            mAdapter.notifyItemMoved(mAdapter.getLastClickComic(), 0);
        }
    }

    private void initRecyclerView() {
        if (mComicDBHelper == null) {
            mComicDBHelper = ComicDBHelper.getInstance(getActivity());
        }
        mMarkedComics = mComicDBHelper.findMarkedComics();
        if (mMarkedComics != null && mMarkedComics.size() != 0) {
            mMarkedComics = mComicDBHelper.findMarkedComics();
            mAdapter = new StaggeredComicAdapter(getActivity(), this, mMarkedComics);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,
                    StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setVisibility(View.VISIBLE);
            mTv.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mTv.setVisibility(View.VISIBLE);
        }
    }

    public void showDialog(final int position, final Comic comic) {
        if (mDeleteMarkView == null) {
            mDeleteMarkView = LayoutInflater.from(getActivity())
                    .inflate(R.layout.dialog_delete_comic_mark, null, false);
            mCheckBox = (CheckBox) mDeleteMarkView.findViewById(R.id.cb_delete_download);
        } else {
            mCheckBox.setChecked(true);
        }

        if (mDialog == null) {
            mDialog = new SimpleDialog(getActivity());
            mDialog.title("删除")
                    .positiveAction("确定")
                    .negativeAction("取消")
                    .contentView(mDeleteMarkView)
                    .cancelable(true)
                    .negativeActionClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.dismiss();
                        }
                    })
                    .positiveActionClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCheckBox.isChecked()) {
                                //删除漫画下载章节
                                Intent intent = new Intent(getActivity(), DownloadManagerService.class);
                                intent.setAction(DownloadManagerService.ACTION_DELETE_COMIC);
                                intent.putExtra("comic", comic);
                                getActivity().startService(intent);
                            }

                            //删除mark标记
                            comic.setMark(false);
                            //更新数据库
                            mComicDBHelper.update(comic);
                            //播放动画
                            mAdapter.removeItem(position);

                            Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                    });
        } else {
            mDialog.positiveActionClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCheckBox.isChecked()) {
                        //删除漫画下载章节
                        Intent intent = new Intent(getActivity(), DownloadManagerService.class);
                        intent.setAction(DownloadManagerService.ACTION_DELETE_COMIC);
                        intent.putExtra("comic", comic);
                        getActivity().startService(intent);
                    }

                    //删除mark标记
                    comic.setMark(false);
                    //更新数据库
                    mComicDBHelper.update(comic);
                    //播放动画
                    mAdapter.removeItem(position);

                    Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            });
        }

        mDialog.show();
    }
}
