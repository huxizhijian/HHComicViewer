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
package org.huxizhijian.hhcomicviewer2.ui.recommend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.RecommendAdapter;
import org.huxizhijian.hhcomicviewer2.ui.common.RefreshBaseFragment;
import org.huxizhijian.hhcomicviewer2.adapter.entity.ComicTabList;
import org.huxizhijian.hhcomicviewer2.persenter.IComicRecommendPresenter;
import org.huxizhijian.hhcomicviewer2.persenter.implpersenter.ComicRecommendPresenter;
import org.huxizhijian.hhcomicviewer2.persenter.viewinterface.IComicRecommendFragment;

import java.util.List;

/**
 * 推荐界面
 * Created by wei on 2017/1/3.
 */

public class RecommendFragment extends RefreshBaseFragment implements IComicRecommendFragment {

    private SwipeRefreshLayout mRefreshLayout;
    private IComicRecommendPresenter mPresenter;
    private RecyclerView mRecyclerView;
    private RecommendAdapter mAdapter;

    private List<ComicTabList> mComicTabLists;

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_recommend);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_recommend);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.pink_500,
                R.color.purple_500, R.color.blue_500);
        return view;
    }

    @Override
    public void initData() {
        mRefreshLayout.setRefreshing(true);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mPresenter == null) {
                    mPresenter = new ComicRecommendPresenter(RecommendFragment.this);
                }
                if (!mPresenter.isConnecting()) {
                    mPresenter.getRecommendList();
                    Log.i("Recommend", "onRefresh: reset");
                }
            }
        });
        if (mPresenter == null) {
            mPresenter = new ComicRecommendPresenter(this);
        }
        if (!mPresenter.isConnecting()) {
            mPresenter.getRecommendList();
        }
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void onInVisible() {
        //do something
    }

    @Override
    public void onSuccess(List<ComicTabList> comicTabLists) {
        mComicTabLists = comicTabLists;
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter = new RecommendAdapter(getActivity(), mComicTabLists);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(mAdapter);
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onException(Throwable e) {
        e.printStackTrace();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "发生错误！", Toast.LENGTH_SHORT).show();
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onFailure(int errorCode, String errorMsg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "发生错误！", Toast.LENGTH_SHORT).show();
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }
}