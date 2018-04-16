/*
 * Copyright 2016-2018 huxizhijian
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

package org.huxizhijian.hhcomicviewer.ui.recommend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.huxizhijian.hhcomicviewer.R;
import org.huxizhijian.hhcomicviewer.adapter.RankDetailsAdapter;
import org.huxizhijian.hhcomicviewer.ui.base.RefreshBaseFragment;
import org.huxizhijian.hhcomicviewer.model.Comic;
import org.huxizhijian.hhcomicviewer.presenter.IRankDetailsPresenter;
import org.huxizhijian.hhcomicviewer.presenter.implpersenter.RankDetailsPresenterImpl;
import org.huxizhijian.hhcomicviewer.presenter.viewinterface.IRankDetailsFragment;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * 排行榜详情
 * Created by wei on 2017/1/19.
 */

public class RankDetailsFragment extends RefreshBaseFragment implements IRankDetailsFragment {

    private String mUrl;
    private IRankDetailsPresenter mPresenter = new RankDetailsPresenterImpl(this);
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank_details, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.pink_500,
                R.color.purple_500, R.color.blue_500);
        mUrl = getArguments().getString("url");
        return view;
    }

    @Override
    public void initData() {
        mRefreshLayout.setRefreshing(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getRankList(mUrl);
            }
        });
        mPresenter.getRankList(mUrl);
    }

    @Override
    public void refreshData() {

    }

    @Override
    public void onInVisible() {

    }

    @Override
    public void onSuccess(List<Comic> comics) {
        if (getActivity() == null) return;
        final RankDetailsAdapter adapter = new RankDetailsAdapter(getActivity(), comics);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(false);
                mRecyclerView.setAdapter(new ScaleInAnimationAdapter(adapter));
            }
        });
    }

    @Override
    public void onException(Throwable e) {

    }

    @Override
    public void onFailure(int errorCode, String errorMsg) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.removeListener();
    }
}
