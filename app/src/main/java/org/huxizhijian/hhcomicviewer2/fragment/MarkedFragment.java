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

package org.huxizhijian.hhcomicviewer2.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.StaggeredComicAdapter;
import org.huxizhijian.hhcomicviewer2.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer2.enities.Comic;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MarkedFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private TextView mTv;
    private ComicDBHelper mComicDBHelper;
    private List<Comic> mMarkedComics;
    private StaggeredComicAdapter mAdapter = null;

    public MarkedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marked, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_marked);
        mTv = (TextView) view.findViewById(R.id.textView_no_marked);
        mComicDBHelper = ComicDBHelper.getInstance(getActivity());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //兼具初始设定view的作用
        refreshData();
    }

    //刷新值
    public void refreshData() {
        if (mComicDBHelper == null) {
            mComicDBHelper = ComicDBHelper.getInstance(getActivity());
        }
        mMarkedComics = mComicDBHelper.findMarkedComics();
        if (mMarkedComics != null && mMarkedComics.size() != 0) {
            if (mAdapter == null) {
                mAdapter = new StaggeredComicAdapter(getActivity(), this, mMarkedComics);
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,
                        StaggeredGridLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(mAdapter);
                mTv.setVisibility(View.GONE);
            } else {
                mAdapter = null;
                mAdapter = new StaggeredComicAdapter(getActivity(), this, mMarkedComics);
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,
                        StaggeredGridLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setAdapter(mAdapter);
                mTv.setVisibility(View.GONE);
            }
        } else {
            mTv.setVisibility(View.VISIBLE);
        }
    }

    public void showDialog(final int position) {
        //先new出一个监听器，设置好监听
        DialogInterface.OnClickListener dialogOnClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE:
                        //删除mark标记
                        mMarkedComics.get(position).setMark(false);
                        //更新数据库
                        mComicDBHelper.update(mMarkedComics.get(position));
                        //播放动画
                        mAdapter.removeItem(position);
                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        //dialog参数设置
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());  //先得到构造器
        builder.setTitle("删除"); //设置标题
        builder.setMessage("是否确认删除收藏?"); //设置内容
        builder.setPositiveButton("确认", dialogOnClickListener);
        builder.setNegativeButton("取消", dialogOnClickListener);
        builder.create().show();
    }
}
