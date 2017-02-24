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
import org.huxizhijian.hhcomicviewer2.adapter.entity.ClassifiesEntity;
import org.huxizhijian.hhcomicviewer2.ui.recommend.ClassifiesShowActivity;
import org.huxizhijian.sdk.imageloader.ImageLoaderOptions;
import org.huxizhijian.sdk.imageloader.listener.ImageLoaderManager;

import java.util.List;

/**
 * 分类，排行榜显示适配器
 * Created by wei on 2017/1/8.
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ClassifiesShowActivity.class);
                intent.putExtra("url", mClassifiesEntities.get(position).getClassifiesUrl());
                intent.putExtra("classifies_name", mClassifiesEntities.get(position).getClassifiesName());
                mContext.startActivity(intent);
            }
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
            iv = (ImageView) itemView.findViewById(R.id.imageView_classifies);
            tv = (TextView) itemView.findViewById(R.id.textView_classifies);
        }
    }

}
