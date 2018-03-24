/*
 * Copyright 2018 huxizhijian
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

package org.huxizhijian.hhcomicviewer.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.huxizhijian.hhcomicviewer.R;
import org.huxizhijian.hhcomicviewer.model.Comic;
import org.huxizhijian.hhcomicviewer.ui.entry.ComicDetailsActivity;
import org.huxizhijian.hhcomicviewer.ui.entry.MarkedFragment;
import org.huxizhijian.sdk.imageloader.ImageLoaderOptions;
import org.huxizhijian.sdk.imageloader.listener.ImageLoaderManager;

import java.util.List;

/**
 * 瀑布流版RecyclerView适配器
 * Created by wei on 2016/9/7.
 */
public class StaggeredComicAdapter extends RecyclerView.Adapter<StaggeredComicAdapter.StaggeredViewHolder> {

    private List<Comic> mComicList;
    private LayoutInflater mInflater;
    private AppCompatActivity mContext;
    private MarkedFragment mFragment;
    private int mLastClickComic;

    //图片包装工具类
    private ImageLoaderManager mImageLoader = ImageLoaderOptions.getImageLoaderManager();

    public StaggeredComicAdapter(Context context, MarkedFragment fragment, List<Comic> comicList) {
        this.mInflater = LayoutInflater.from(context);
        this.mComicList = comicList;
        this.mContext = (AppCompatActivity) context;
        this.mFragment = fragment;
    }

    @Override
    public StaggeredViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_staggered_recycler_view, parent, false);
        return new StaggeredViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StaggeredViewHolder holder, int position) {
        if (position >= mComicList.size()) return;
        holder.tv.setText(mComicList.get(position).getTitle());
        mImageLoader.displayThumbnail(mContext, mComicList.get(position).getThumbnailUrl(), holder.iv,
                R.mipmap.blank, R.mipmap.blank, 165, 220);
        setUpItemEvent(holder, position);
    }

    @Override
    public int getItemCount() {
        return mComicList.size();
    }

    private void setUpItemEvent(final StaggeredViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ComicDetailsActivity.class);
                intent.putExtra("cid", mComicList.get(position).getCid());
                intent.putExtra("thumbnailUrl", mComicList.get(position).getThumbnailUrl());
                intent.putExtra("title", mComicList.get(position).getTitle());

                ImageView sharedView = (ImageView) view.findViewById(R.id.imageView_staggered);

                if (sharedView.getDrawable() != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        //如果是android5.0及以上，开启shareElement动画
                        String transitionName = mContext.getString(R.string.image_transition_name);

                        ActivityOptions transitionActivityOptions = ActivityOptions
                                .makeSceneTransitionAnimation(mContext, sharedView, transitionName);
                        mContext.startActivity(intent, transitionActivityOptions.toBundle());
                    } else {
                        mContext.startActivity(intent);
                    }
                } else {
                    mContext.startActivity(intent);
                }

                mLastClickComic = holder.getLayoutPosition();
            }
        });

        if (mFragment != null) {
            //longClick
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mFragment.showDialog(holder.getLayoutPosition(), mComicList.get(holder.getLayoutPosition()));
                    return false;
                }
            });
        }
    }

    public void updateComicList(List<Comic> comicList) {
        this.mComicList = comicList;
    }

    public void removeItem(int position) {
        mComicList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mComicList.size() - 1);
    }

    public int getLastClickComic() {
        return mLastClickComic;
    }

    public void setLastClickComic(int lastClickComic) {
        mLastClickComic = lastClickComic;
    }

    class StaggeredViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tv;

        StaggeredViewHolder(View itemView) {
            super(itemView);
            //绑定控件
            iv = (ImageView) itemView.findViewById(R.id.imageView_staggered);
            tv = (TextView) itemView.findViewById(R.id.textView_staggered);
        }
    }
}
