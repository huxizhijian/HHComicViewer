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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.huxizhijian.hhcomicviewer2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 下载选择RecyclerView适配器
 * Created by wei on 2016/9/13.
 */
public class VolDownloadSelectorAdapter extends RecyclerView.Adapter<VolDownloadSelectorAdapter.VolViewHolder> {

    private List<String> mVolName;
    private LayoutInflater mInflater;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private List<String> mComicchapterList; //已经开始下载的章节
    private List<String> mFinishedComicchapterList;  //下载好的章节
    private List<String> mSelectedchapterNames; //选择的下载章节

    private Bitmap mBitmap_ok = null;
    private Bitmap mBitmap_downloading = null;

    public VolDownloadSelectorAdapter(Context context, List<String> volName,
                                      List<String> comicchapters, List<String> finishedComicchapterList) {
        this.mVolName = volName;
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mComicchapterList = comicchapters;
        this.mFinishedComicchapterList = finishedComicchapterList;
        mSelectedchapterNames = new ArrayList<>();
    }

    @Override
    public VolViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_vol_recycler_view, parent, false);
        return new VolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VolViewHolder holder, int position) {
        position = holder.getLayoutPosition();
        holder.tv.setText(mVolName.get(position));
        holder.iv.setVisibility(View.GONE);
        //标记下载好的章节
        if (mComicchapterList != null && mComicchapterList.contains(mVolName.get(position))) {
            //进行标记
            if (mFinishedComicchapterList != null &&
                    mFinishedComicchapterList.contains(mVolName.get(position))) {
                //如果是下载完成的章节
                if (mBitmap_ok == null) {
                    //进行图片的初始化
                    mBitmap_ok = BitmapFactory.decodeResource(mContext.getResources(),
                            R.mipmap.ic_check_green_18dp);
                }
                holder.iv.setImageBitmap(mBitmap_ok);
                holder.iv.setVisibility(View.VISIBLE);
            } else {
                //如果是未下载完成的章节
                if (mBitmap_downloading == null) {
                    //进行图片的初始化
                    mBitmap_downloading = BitmapFactory.decodeResource(mContext.getResources(),
                            R.mipmap.ic_file_download_grey600_18dp);
                }
                holder.iv.setImageBitmap(mBitmap_downloading);
                holder.iv.setVisibility(View.VISIBLE);
            }
        }
        if (mSelectedchapterNames.contains(mVolName.get(position))) {
            //选择的章节
            holder.cv.setCardBackgroundColor(mContext.getResources().getColor(R.color.green_color_download));
        } else {
            holder.cv.setCardBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
        setUpItemEvent(holder);
    }

    public void chapterClick(int position) {
        String chapterName = mVolName.get(position);
        if (mComicchapterList != null && mComicchapterList.contains(chapterName)) return;
        if (!mSelectedchapterNames.contains(chapterName)) {
            mSelectedchapterNames.add(chapterName);
            notifyItemChanged(position);
        } else {
            mSelectedchapterNames.remove(chapterName);
            notifyItemChanged(position);
        }
    }


    public List<String> getSelectedchapterNames() {
        return mSelectedchapterNames;
    }

    public void allSelect() {
        mSelectedchapterNames.clear();
        for (int i = 0; i < mVolName.size(); i++) {
            if (!(mComicchapterList != null && mComicchapterList.contains(mVolName.get(i)))) {
                mSelectedchapterNames.add(mVolName.get(i));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mVolName.size();
    }

    protected void setUpItemEvent(final VolViewHolder holder) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });

            //longClick
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mOnItemClickListener.onItemLongClick(holder.itemView, holder.getLayoutPosition());
                    return false;
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    class VolViewHolder extends RecyclerView.ViewHolder {

        TextView tv;
        CardView cv;
        ImageView iv;

        public VolViewHolder(View itemView) {
            super(itemView);

            tv = (TextView) itemView.findViewById(R.id.tv_vol_name_item);
            cv = (CardView) itemView.findViewById(R.id.cardView_vol_item);
            iv = (ImageView) itemView.findViewById(R.id.imageView_vol_item);
        }
    }
}
