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


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.activities.ComicDetailsActivity;
import org.huxizhijian.hhcomicviewer2.adapter.CommonAdapter;
import org.huxizhijian.hhcomicviewer2.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer2.enities.Comic;
import org.huxizhijian.hhcomicviewer2.utils.ViewHolder;

import java.util.List;

public class HistoryFragment extends Fragment {

    private ListView mListView;
    private ComicDBHelper mComicDBHelper;
    private List<Comic> mComics;
    private HistoryListViewAdapter mAdapter;

    public HistoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        mListView = (ListView) view.findViewById(R.id.listView_history);
        mComicDBHelper = ComicDBHelper.getInstance(getActivity());
        initData();
        return view;
    }

    private void initData() {
        mComics = mComicDBHelper.findAll();
        if (mComics == null) return;
        mAdapter = new HistoryListViewAdapter(getActivity(), mComics, R.layout.item_list_view);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), ComicDetailsActivity.class);
                intent.putExtra("url", mComics.get(position).getComicUrl());
                intent.putExtra("thumbnailUrl", mComics.get(position).getThumbnailUrl());
                intent.putExtra("title", mComics.get(position).getTitle());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mComics = mComicDBHelper.findAll();
            if (mComics != null) {
                mAdapter.setDatas(mComics);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void refreshData() {
        if (mAdapter != null) {
            mComics = mComicDBHelper.findAll();
            if (mComics != null) {
                mAdapter.setDatas(mComics);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    //自定义adapter
    class HistoryListViewAdapter extends CommonAdapter<Comic> {

        public HistoryListViewAdapter(Context context, List<Comic> comic, int layoutResId) {
            super(context, comic, layoutResId);
        }

        @Override
        public void convert(ViewHolder vh, Comic comic) {
            vh.setText(R.id.tv_title_item, comic.getTitle());
            vh.setText(R.id.tv_description_item, "作者：" + comic.getAuthor());
            vh.setText(R.id.tv_read_info_item, "上次看到第" + (comic.getReadCapture() + 1) + "集，" +
                    "第" + (comic.getReadPage() + 1) + "页");
            ImageView imageView = vh.getView(R.id.imageView_item);
            Glide.with(getActivity())
                    .load(comic.getThumbnailUrl())
                    .fitCenter()
                    .placeholder(R.mipmap.blank)
                    .error(R.mipmap.blank)
                    .into(imageView);
        }
    }
}