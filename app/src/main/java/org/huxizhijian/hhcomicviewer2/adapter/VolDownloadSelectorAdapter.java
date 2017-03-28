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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

    //两个事件回调，单机、长按事件回调，所有选择事件发生和取消时事件回调
    private OnItemClickListener mOnItemClickListener;
    private OnAllSelectedChangedListener mChangedListener;

    private List<String> mComicChapterList; //已经开始下载的章节
    private List<String> mFinishedComicChapterList;  //下载好的章节
    private List<String> mSelectedChapterNames; //选择的下载章节

    private Bitmap mBitmap_ok = null;
    private Bitmap mBitmap_downloading = null;

    public VolDownloadSelectorAdapter(Context context, List<String> volName,
                                      List<String> comicChapters, List<String> finishedComicChapterList) {
        this.mVolName = volName;
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mComicChapterList = comicChapters;
        this.mFinishedComicChapterList = finishedComicChapterList;
        mSelectedChapterNames = new ArrayList<>();
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
        if (mComicChapterList != null && mComicChapterList.contains(mVolName.get(position))) {
            //进行标记
            if (mFinishedComicChapterList != null &&
                    mFinishedComicChapterList.contains(mVolName.get(position))) {
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
        if (mSelectedChapterNames.contains(mVolName.get(position))) {
            //选择的章节
            holder.frame.setBackgroundColor(mContext.getResources().getColor(R.color.green_color_download));
        } else {
            holder.frame.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
        setUpItemEvent(holder);
    }

    public void chapterClick(int position) {
        String chapterName = mVolName.get(position);
        if (mComicChapterList != null && mComicChapterList.contains(chapterName)) return;
        if (!mSelectedChapterNames.contains(chapterName)) {
            mSelectedChapterNames.add(chapterName);
            notifyItemChanged(position);
        } else {
            mSelectedChapterNames.remove(chapterName);
            notifyItemChanged(position);
        }
        checkAllSelected();
    }


    public List<String> getSelectedChapterNames() {
        return mSelectedChapterNames;
    }

    public void allSelect() {
        //根据是否存在开始下载的章节，判断是否去掉开始下载的章节数
        int allSize;
        if (mComicChapterList == null || mComicChapterList.size() == 0) {
            allSize = mVolName.size();
        } else {
            allSize = mVolName.size() - mComicChapterList.size();
        }
        if (mSelectedChapterNames.size() != allSize) {
            mSelectedChapterNames.clear();
            //非全选时才进行全选操作
            for (int i = 0; i < mVolName.size(); i++) {
                if (!(mComicChapterList != null && mComicChapterList.contains(mVolName.get(i)))) {
                    mSelectedChapterNames.add(mVolName.get(i));
                }
            }
        } else {
            mSelectedChapterNames.clear();
        }
        checkAllSelected();
        notifyDataSetChanged();
    }

    private boolean checkAllSelected() {
        int allSize;
        //根据是否存在开始下载的章节，判断是否去掉开始下载的章节数
        if (mComicChapterList == null || mComicChapterList.size() == 0) {
            allSize = mVolName.size();
        } else {
            allSize = mVolName.size() - mComicChapterList.size();
        }
        if (mSelectedChapterNames.size() == allSize) {
            //全选
            if (mChangedListener != null) {
                mChangedListener.onAllSelected();
            }
            return true;
        } else {
            if (mChangedListener != null) {
                mChangedListener.onNoAllSelected();
            }
            return false;
        }
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

    public interface OnAllSelectedChangedListener {
        void onAllSelected();

        void onNoAllSelected();
    }

    public void setOnAllSelectedChangedListener(OnAllSelectedChangedListener changedListener) {
        mChangedListener = changedListener;
    }

    class VolViewHolder extends RecyclerView.ViewHolder {

        TextView tv;
        FrameLayout frame;
        ImageView iv;

        public VolViewHolder(View itemView) {
            super(itemView);

            tv = (TextView) itemView.findViewById(R.id.tv_vol_name_item);
            frame = (FrameLayout) itemView.findViewById(R.id.frame_layout);
            iv = (ImageView) itemView.findViewById(R.id.imageView_vol_item);
        }
    }
}
