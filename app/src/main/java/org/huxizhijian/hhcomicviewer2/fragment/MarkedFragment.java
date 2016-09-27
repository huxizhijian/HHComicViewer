package org.huxizhijian.hhcomicviewer2.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.activities.ComicInfoActivity;
import org.huxizhijian.hhcomicviewer2.adapter.StaggeredComicAdapter;
import org.huxizhijian.hhcomicviewer2.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer2.enities.Comic;

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
                showDialog(position);
            }
        });
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mMarkedComics = mComicDBHelper.findMarkedComics();
            if (mMarkedComics != null) {
                mAdapter.setComicList(mMarkedComics);
//                preloadBitmap();
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void refreshData() {
        if (mAdapter != null) {
            mMarkedComics = mComicDBHelper.findMarkedComics();
            if (mMarkedComics != null) {
                mAdapter.setComicList(mMarkedComics);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showDialog(final int position) {
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
