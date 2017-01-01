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

package org.huxizhijian.hhcomicviewer2.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.activities.ComicDetailsActivity;
import org.huxizhijian.hhcomicviewer2.activities.MainActivity;
import org.huxizhijian.hhcomicviewer2.enities.Comic;
import org.huxizhijian.hhcomicviewer2.fragment.MarkedFragment;

import java.util.List;

/**
 * 瀑布流版RecyclerView适配器
 * Created by wei on 2016/9/7.
 */
public class StaggeredComicAdapter extends RecyclerView.Adapter<StaggeredComicAdapter.StaggeredViewHolder> {

    private List<Comic> mComicList;
    private LayoutInflater mInflater;
    private MainActivity mContext;
    private MarkedFragment mFragment;

    public StaggeredComicAdapter(Context context, MarkedFragment fragment, List<Comic> comicList) {
        this.mInflater = LayoutInflater.from(context);
        this.mComicList = comicList;
        this.mContext = (MainActivity) context;
        this.mFragment = fragment;
    }

    @Override
    public StaggeredViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_staggered_recycler_view, parent, false);
        return new StaggeredViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StaggeredViewHolder holder, int position) {
        holder.tv.setText(mComicList.get(holder.getLayoutPosition()).getTitle());
        Glide.with(mContext)
                .load(mComicList.get(holder.getLayoutPosition()).getThumbnailUrl())
                .placeholder(R.mipmap.blank)
                .dontAnimate()
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable glideDrawable,
                                                GlideAnimation<? super GlideDrawable> glideAnimation) {
                        holder.iv.setImageDrawable(glideDrawable);
                    }
                });
        setUpItemEvent(holder);
    }

    @Override
    public int getItemCount() {
        return mComicList.size();
    }

    private void setUpItemEvent(final StaggeredViewHolder holder) {
        final int position = holder.getLayoutPosition();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ComicDetailsActivity.class);
                intent.putExtra("url", mComicList.get(position).getComicUrl());
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
            }
        });

        //longClick
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mFragment.showDialog(position);
                return false;
            }
        });
    }

    public void updateComicList(List<Comic> comicList) {
        this.mComicList = comicList;
    }

    public void removeItem(int position) {
        mComicList.remove(position);
        notifyItemRemoved(position);
    }

    class StaggeredViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tv;

        public StaggeredViewHolder(View itemView) {
            super(itemView);
            //绑定控件
            iv = (ImageView) itemView.findViewById(R.id.imageView_staggered);
            tv = (TextView) itemView.findViewById(R.id.textView_staggered);
        }
    }
}
