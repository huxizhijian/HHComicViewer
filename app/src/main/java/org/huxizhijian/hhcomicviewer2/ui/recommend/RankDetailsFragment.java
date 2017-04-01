package org.huxizhijian.hhcomicviewer2.ui.recommend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.RankDetailsAdapter;
import org.huxizhijian.hhcomicviewer2.ui.common.RefreshBaseFragment;
import org.huxizhijian.hhcomicviewer2.model.Comic;
import org.huxizhijian.hhcomicviewer2.persenter.IRankDetailsPresenter;
import org.huxizhijian.hhcomicviewer2.persenter.implpersenter.RankDetailsPresenterImpl;
import org.huxizhijian.hhcomicviewer2.persenter.viewinterface.IRankDetailsFragment;

import java.util.List;

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
                mRecyclerView.setAdapter(adapter);
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
