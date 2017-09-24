/*
 * Copyright 2017 huxizhijian
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

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.ui.recommend.ClassifiesShowActivity;
import org.huxizhijian.hhcomicviewer2.ui.recommend.RankShowActivity;
import org.huxizhijian.hhcomicviewer2.adapter.entity.RankTitleEntity;

import java.util.List;

/**
 * 排行榜adapter
 * Created by wei on 2017/1/10.
 */

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.HeaderViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<RankTitleEntity> mRankTitleEntities;

    public RankAdapter(Context context, List<RankTitleEntity> rankTitleEntities) {
        mContext = context;
        this.mRankTitleEntities = rankTitleEntities;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public HeaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HeaderViewHolder(mInflater.inflate(R.layout.item_rank_title, parent, false));
    }

    @Override
    public void onBindViewHolder(HeaderViewHolder holder, final int position) {
        holder.iv_title.setImageResource(mRankTitleEntities.get(position).getImgResId());
        holder.tv_title.setText(mRankTitleEntities.get(position).getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < 4) {
                    Intent intent = new Intent(mContext, RankShowActivity.class);
                    intent.putExtra("rank_type", position);
                    mContext.startActivity(intent);
                } else if (position == 4) {
                    //推荐漫画
                    Intent intent = new Intent(mContext, ClassifiesShowActivity.class);
                    intent.putExtra("url", "/comic/best_1/");
                    intent.putExtra("classifies_name", "汗妹推荐的漫画");
                    mContext.startActivity(intent);
                } else if (position == 5) {
                    //漫画目录
                    Intent intent = new Intent(mContext, ClassifiesShowActivity.class);
                    intent.putExtra("url", "/comic/");
                    intent.putExtra("classifies_name", "漫画大全");
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRankTitleEntities.size();
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        ImageView iv_title;

        HeaderViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            iv_title = (ImageView) itemView.findViewById(R.id.iv_title);
        }
    }
}
