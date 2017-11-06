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

package org.huxizhijian.hhcomicviewer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.huxizhijian.hhcomicviewer.R;
import org.huxizhijian.hhcomicviewer.adapter.entity.ClassifiesEntity;
import org.huxizhijian.hhcomicviewer.ui.recommend.ClassifiesShowActivity;
import org.huxizhijian.sdk.imageloader.ImageLoaderOptions;
import org.huxizhijian.sdk.imageloader.listener.ImageLoaderManager;

import java.util.List;

/**
 * 分类，排行榜显示适配器
 *
 * @author huxizhijian
 * @date 2017/1/8
 */

public class ClassifiesAdapter extends RecyclerView.Adapter<ClassifiesAdapter.ClassifiesViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<ClassifiesEntity> mClassifiesEntities;

    //图片加载工具
    private ImageLoaderManager mImageLoader = ImageLoaderOptions.getImageLoaderManager();

    public ClassifiesAdapter(Context context, List<ClassifiesEntity> classifiesEntities) {
        this.mContext = context;
        this.mClassifiesEntities = classifiesEntities;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ClassifiesAdapter.ClassifiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ClassifiesViewHolder(mInflater.inflate(R.layout.item_classifies, parent, false));
    }

    @Override
    public void onBindViewHolder(ClassifiesAdapter.ClassifiesViewHolder holder, final int position) {
        mImageLoader.displayThumbnail(mContext, mClassifiesEntities.get(position).getClassifiesPicUrl(), holder.iv,
                R.mipmap.blank, R.mipmap.blank, 165, 220);
        holder.tv.setText(mClassifiesEntities.get(position).getClassifiesName());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ClassifiesShowActivity.class);
            intent.putExtra("url", mClassifiesEntities.get(position).getClassifiesUrl());
            intent.putExtra("classifies_name", mClassifiesEntities.get(position).getClassifiesName());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mClassifiesEntities.size();
    }

    class ClassifiesViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tv;

        ClassifiesViewHolder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.imageView_classifies);
            tv = itemView.findViewById(R.id.textView_classifies);
        }
    }

}
