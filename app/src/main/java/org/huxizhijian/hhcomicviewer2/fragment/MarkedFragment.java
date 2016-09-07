package org.huxizhijian.hhcomicviewer2.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.huxizhijian.hhcomicviewer2.ComicInfoActivity;
import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.StaggeredComicAdapter;
import org.huxizhijian.hhcomicviewer2.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer2.vo.Comic;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MarkedFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ComicDBHelper mComicDBHelper;
    private List<Comic> mMarkedComics;
    private StaggeredComicAdapter mAdapter;

    public MarkedFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marked, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_marked);
        mComicDBHelper = ComicDBHelper.getInstance(getActivity());
        initData();
        return view;
    }

    private void initData() {
        mMarkedComics = mComicDBHelper.findMarkedComics();
        if (mMarkedComics == null) return;
        mAdapter = new StaggeredComicAdapter(getActivity(), mMarkedComics);
        mAdapter.setOnItemClickListener(new StaggeredComicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), ComicInfoActivity.class);
                intent.setAction(ComicInfoActivity.ACTION_MARKED);
                intent.putExtra("url", mMarkedComics.get(position).getComicUrl());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mMarkedComics = mComicDBHelper.findMarkedComics();
            if (mMarkedComics != null) {
                mAdapter.setComicList(mMarkedComics);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
