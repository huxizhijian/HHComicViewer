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
package org.huxizhijian.hhcomicviewer2.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.entity.ComicTabList;
import org.huxizhijian.hhcomicviewer2.model.Comic;
import org.huxizhijian.hhcomicviewer2.ui.entry.ComicDetailsActivity;
import org.huxizhijian.hhcomicviewer2.ui.recommend.ComicShowActivity;
import org.huxizhijian.sdk.imageloader.ImageLoaderOptions;
import org.huxizhijian.sdk.imageloader.listener.ImageLoaderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐视图的适配器
 * Created by wei on 2017/1/4.
 */

public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;

    private List<ComicTabList> mTabLists;

    private static final int ITEM_HEADER = 0;
    private static final int ITEM_NEW = 1;
    private static final int ITEM_HOT = 2;

    private ImageLoaderManager mImageLoader = ImageLoaderOptions.getImageLoaderManager();

    public RecommendAdapter(Context context, List<ComicTabList> tabLists) {
        this.mContext = context;
        this.mTabLists = tabLists;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == 2) {
            return ITEM_HEADER;
        } else if (position == 1) {
            return ITEM_HOT;
        } else {
            return ITEM_NEW;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        if (viewType == ITEM_HEADER) {
            vh = new HeaderViewHolder(mInflater.inflate(R.layout.item_recommend_title, parent, false));
        } else if (viewType == ITEM_NEW) {
            vh = new NewViewHolder(mInflater.inflate(R.layout.item_recycler_view, parent, false));
        } else {
            vh = new HotViewHolder(mInflater.inflate(R.layout.item_comic_hot, parent, false));
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof NewViewHolder) {
            final NewViewHolder vh = (NewViewHolder) holder;
            final Comic comic = mTabLists.get(1).getComics().get(position - 3);
            vh.title.setText(comic.getTitle());
            vh.desc.setText(comic.getAuthor());
            vh.info.setText(comic.getComicStatus());

            mImageLoader.displayThumbnail(mContext, comic.getThumbnailUrl(), vh.iv,
                    R.mipmap.blank, R.mipmap.blank, 165, 220);

            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ComicDetailsActivity.class);
                    intent.putExtra("cid", comic.getCid());
                    intent.putExtra("thumbnailUrl", comic.getThumbnailUrl());
                    intent.putExtra("title", comic.getTitle());

                    if (vh.iv.getDrawable() != null) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            //如果是android5.0及以上，开启shareElement动画
                            String transitionName = mContext.getString(R.string.image_transition_name);

                            ActivityOptions transitionActivityOptions = ActivityOptions
                                    .makeSceneTransitionAnimation((Activity) mContext, vh.iv, transitionName);
                            mContext.startActivity(intent, transitionActivityOptions.toBundle());
                        } else {
                            mContext.startActivity(intent);
                        }
                    } else {
                        mContext.startActivity(intent);
                    }
                }
            });
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder vh = (HeaderViewHolder) holder;
            if (position == 0) {
                vh.iv_title.setImageResource(R.drawable.ic_comic);
                vh.itemView.setPadding(8, 16, 8, 8);
            } else {
                vh.iv_title.setImageResource(R.drawable.ic_toys_black_24dp);
            }
            vh.tv_title.setText(mTabLists.get(0).getTabName());
            vh.btn_more.setVisibility(View.VISIBLE);
            //加载更多
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ComicShowActivity.class);
                    for (int i = 0; i < 5; i++) {
                        intent.putExtra("tab_list_" + i, mTabLists.get(i));
                    }
                    if (position != 0) {
                        intent.putExtra("page", 1);
                    }
                    mContext.startActivity(intent);
                }
            });
        } else if (holder instanceof HotViewHolder) {
            final HotViewHolder vh = (HotViewHolder) holder;
            final List<Comic> comics = mTabLists.get(0).getComics();
            for (int i = 0; i < vh.ivs.size(); i++) {
                vh.titles.get(i).setText(comics.get(i).getTitle());
                mImageLoader.displayThumbnail(mContext, comics.get(i).getThumbnailUrl(), vh.ivs.get(i),
                        R.mipmap.blank, R.mipmap.blank, 165, 220);
                final int finalI = i;
                vh.cvs.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ComicDetailsActivity.class);
                        intent.putExtra("cid", comics.get(finalI).getCid());
                        intent.putExtra("thumbnailUrl", comics.get(finalI).getThumbnailUrl());
                        intent.putExtra("title", comics.get(finalI).getTitle());

                        if (vh.ivs.get(finalI).getDrawable() != null) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                                //如果是android5.0及以上，开启shareElement动画
                                String transitionName = mContext.getString(R.string.image_transition_name);

                                ActivityOptions transitionActivityOptions = ActivityOptions
                                        .makeSceneTransitionAnimation((Activity) mContext, vh.ivs.get(finalI),
                                                transitionName);
                                mContext.startActivity(intent, transitionActivityOptions.toBundle());
                            } else {
                                mContext.startActivity(intent);
                            }
                        } else {
                            mContext.startActivity(intent);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mTabLists.get(1).getComics().size() + 3;
    }


    private class HotViewHolder extends RecyclerView.ViewHolder {

        List<ImageView> ivs = new ArrayList<>();
        List<TextView> titles = new ArrayList<>();
        List<CardView> cvs = new ArrayList<>();

        public HotViewHolder(View itemView) {
            super(itemView);
            ivs.add((ImageView) itemView.findViewById(R.id.iv_hot0));
            ivs.add((ImageView) itemView.findViewById(R.id.iv_hot1));
            ivs.add((ImageView) itemView.findViewById(R.id.iv_hot2));
            titles.add((TextView) itemView.findViewById(R.id.tv_hot0));
            titles.add((TextView) itemView.findViewById(R.id.tv_hot1));
            titles.add((TextView) itemView.findViewById(R.id.tv_hot2));
            cvs.add((CardView) itemView.findViewById(R.id.cv_hot0));
            cvs.add((CardView) itemView.findViewById(R.id.cv_hot1));
            cvs.add((CardView) itemView.findViewById(R.id.cv_hot2));
        }
    }

    private class NewViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView title, desc, info;

        public NewViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.imageView_item);
            title = (TextView) itemView.findViewById(R.id.tv_title_item);
            desc = (TextView) itemView.findViewById(R.id.tv_description_item);
            info = (TextView) itemView.findViewById(R.id.tv_read_info_item);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title, btn_more;
        ImageView iv_title;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            btn_more = (TextView) itemView.findViewById(R.id.btn_more);
            iv_title = (ImageView) itemView.findViewById(R.id.iv_title);
        }
    }
}
