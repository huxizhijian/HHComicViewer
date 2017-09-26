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

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.huxizhijian.hhcomicviewer.R;
import org.huxizhijian.hhcomicviewer.model.Comic;
import org.huxizhijian.hhcomicviewer.model.ComicChapter;
import org.huxizhijian.hhcomicviewer.service.DownloadManagerService;
import org.huxizhijian.hhcomicviewer.ui.download.ComicChapterDownloadActivity;
import org.huxizhijian.hhcomicviewer.ui.download.DownloadingChapterActivity;
import org.huxizhijian.hhcomicviewer.ui.download.listener.OnEditModeListener;
import org.huxizhijian.hhcomicviewer.ui.entry.ComicDetailsActivity;
import org.huxizhijian.sdk.imageloader.ImageLoaderOptions;
import org.huxizhijian.sdk.imageloader.listener.ImageLoaderManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 下载漫画显示适配器
 * Created by wei on 2017/2/4.
 */

public class DownloadShowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //type
    private final static int TYPE_HEADER = 0x1;
    private final static int TYPE_ITEM = 0x2;
    private final static int TYPE_FOOTER = 0x3;

    //context
    private Activity mContext;
    private LayoutInflater mInflater;
    private OnEditModeListener mControlListener;

    //数据
    private List<Comic> mFinishedComicList;
    private SparseArray<List<ComicChapter>> mDownloadedChapterList;
    private List<ComicChapter> mUnFinishedChapterList;

    //状态
    private boolean isEditModeOn = false;

    //checkbox选中状态
    private Set<Integer> mCbChecked;

    //图片加载控件
    private ImageLoaderManager mImageLoader = ImageLoaderOptions.getImageLoaderManager();

    public DownloadShowAdapter(Activity context, SparseArray<List<ComicChapter>> downloadedChapterList,
                               List<Comic> finishedComicList, List<ComicChapter> unFinishedChapterList,
                               OnEditModeListener listener) {
        mContext = context;
        mDownloadedChapterList = downloadedChapterList;
        mFinishedComicList = finishedComicList;
        mInflater = LayoutInflater.from(context);
        mUnFinishedChapterList = unFinishedChapterList;
        mControlListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new DownloadShowItemViewHolder(mInflater.inflate(R.layout.item_download_show, parent, false));
        } else if (viewType == TYPE_HEADER) {
            return new HeaderItemViewHolder(mInflater.inflate(R.layout.header_view_comic_download, parent, false));
        } else {
            return new FooterItemViewHolder(mInflater
                    .inflate(R.layout.footer_view_comic_download_menu, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    public void addItem(Comic comic) {
        mFinishedComicList.add(comic);
        if (!isEditModeOn) {
            notifyDataSetChanged();
        }
    }

    public void setFinishedComicList(List<Comic> finishedComicList) {
        mFinishedComicList = finishedComicList;
    }

    public void setDownloadedChapterList(SparseArray<List<ComicChapter>> downloadedChapterList) {
        mDownloadedChapterList = downloadedChapterList;
    }

    public void setUnFinishedChapterList(List<ComicChapter> unFinishedChapterList) {
        mUnFinishedChapterList = unFinishedChapterList;
    }

    public boolean isEditModeOn() {
        return isEditModeOn;
    }

    public void openEditMode(int position) {
        if (mCbChecked == null) {
            mCbChecked = new HashSet<>();
        } else {
            mCbChecked.clear();
        }
        if (position != -1) {
            mCbChecked.add(position);
        }
        isEditModeOn = true;
        notifyDataSetChanged();
        if (mControlListener != null) {
            mControlListener.onEditModeOpen();
        }
    }

    public void closeEditMode() {
        isEditModeOn = false;
        notifyDataSetChanged();
        if (mControlListener != null) {
            mControlListener.onEditModeClose();
        }
    }

    public void selectAll() {
        if (mCbChecked.size() != mFinishedComicList.size()) {
            mCbChecked.clear();
            for (int i = 0; i < mFinishedComicList.size(); i++) {
                mCbChecked.add(i);
            }
            notifyDataSetChanged();
            if (mControlListener != null) {
                mControlListener.onAllSelected();
            }
        } else {
            mCbChecked.clear();
            notifyDataSetChanged();
            if (mControlListener != null) {
                mControlListener.onNoAllSelected();
            }
        }
    }

    public void deleteComic() {

        //deleteAction
        List<Comic> comics = new ArrayList<>();
        Intent intent = null;

        //循环删除已经下载完成的任务（未下载好的不会删除）
        for (int position : mCbChecked) {
            List<ComicChapter> chapters = mDownloadedChapterList.get(mFinishedComicList.get(position).getCid());
            for (int i = 0; i < chapters.size(); i++) {
                intent = new Intent(mContext, DownloadManagerService.class);
                intent.setAction(DownloadManagerService.ACTION_DELETE);
                intent.putExtra("comicChapter", chapters.get(i));
                mContext.startService(intent);
            }
            comics.add(mFinishedComicList.get(position));
        }
        mFinishedComicList.removeAll(comics);

        closeEditMode();
        if (mControlListener != null) {
            mControlListener.onEditModeClose();
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, int p) {
        if (vh.getItemViewType() == TYPE_HEADER) {
            final HeaderItemViewHolder holder = (HeaderItemViewHolder) vh;
            if (mUnFinishedChapterList == null || mUnFinishedChapterList.size() == 0) {
                holder.tv_title.setText("共0个任务");
                holder.tv_mission.setText("一个进行中的都没有");
            } else {
                holder.tv_title.setText("共" + mUnFinishedChapterList.size() + "个任务");
                holder.tv_mission.setText("进行中:" + mUnFinishedChapterList.get(0).getChapterName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, DownloadingChapterActivity.class);
                        mContext.startActivity(intent);
                    }
                });
            }
            if (isEditModeOn) {
                holder.itemView.setVisibility(View.GONE);
            } else {
                holder.itemView.setVisibility(View.VISIBLE);
            }
            return;
        }
        if (vh.getItemViewType() == TYPE_FOOTER) {
            final FooterItemViewHolder holder = (FooterItemViewHolder) vh;
            //计算高度
            if (isEditModeOn) {
                holder.ll.setVisibility(View.VISIBLE);
                holder.fl.setVisibility(View.GONE);
            } else {
                holder.fl.setVisibility(View.VISIBLE);
                holder.ll.setVisibility(View.GONE);
            }
            return;
        }

        if (vh.getItemViewType() == TYPE_ITEM) {
            final int position = p - 1;
            final DownloadShowItemViewHolder holder = (DownloadShowItemViewHolder) vh;
            final Comic comic = mFinishedComicList.get(position);

            mImageLoader.displayThumbnail(mContext, comic.getThumbnailUrl(), holder.iv,
                    R.mipmap.blank, R.mipmap.blank, 165, 220);

            holder.tv_desc.setText("已完成" + mDownloadedChapterList.get(comic.getCid()).size() + "章节");
            holder.tv_title.setText(comic.getTitle());
            holder.tv_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEditModeOn) {
                        checkedChange(position, holder);
                        return;
                    }

                    Intent intent = new Intent(mContext, ComicDetailsActivity.class);
                    intent.putExtra("cid", comic.getCid());
                    intent.putExtra("thumbnailUrl", comic.getThumbnailUrl());
                    intent.putExtra("title", comic.getTitle());

                    if (holder.iv.getDrawable() != null) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            //如果是android5.0及以上，开启shareElement动画
                            String transitionName = mContext.getString(R.string.image_transition_name);

                            ActivityOptions transitionActivityOptions = ActivityOptions
                                    .makeSceneTransitionAnimation(mContext, holder.iv, transitionName);
                            mContext.startActivity(intent, transitionActivityOptions.toBundle());
                        } else {
                            mContext.startActivity(intent);
                        }
                    } else {
                        mContext.startActivity(intent);
                    }
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!isEditModeOn) {
                        openEditMode(position);
                    }
                    return true;
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEditModeOn) {
                        checkedChange(position, holder);
                    } else {
                        Intent intent = new Intent(mContext, ComicChapterDownloadActivity.class);
                        intent.putExtra("comic", comic);
                        mContext.startActivity(intent);
                    }
                }
            });
            if (isEditModeOn) {
                holder.cb.setVisibility(View.VISIBLE);
                holder.cb.setChecked(mCbChecked.contains(position));
                holder.cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkedChange(position, holder);
                    }
                });
            } else {
                holder.cb.setVisibility(View.GONE);
            }
        }

    }

    private void checkedChange(int position, DownloadShowItemViewHolder holder) {
        if (mCbChecked.contains(position)) {
            mCbChecked.remove(position);
        } else {
            mCbChecked.add(position);
        }
        holder.cb.setChecked(mCbChecked.contains(position));
        if (mCbChecked.size() == mFinishedComicList.size()) {
            mControlListener.onAllSelected();
        } else {
            mControlListener.onNoAllSelected();
        }
    }

    @Override
    public int getItemCount() {
        return mFinishedComicList.size() + 2;
    }

    private class DownloadShowItemViewHolder extends RecyclerView.ViewHolder {

        CheckBox cb;
        ImageView iv;
        TextView tv_title, tv_desc, tv_details;

        DownloadShowItemViewHolder(View itemView) {
            super(itemView);
            cb = (CheckBox) itemView.findViewById(R.id.checkbox);
            iv = (ImageView) itemView.findViewById(R.id.imageView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_desc = (TextView) itemView.findViewById(R.id.tv_description);
            tv_details = (TextView) itemView.findViewById(R.id.tv_comic_details);
        }

    }

    private class HeaderItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title, tv_mission;

        HeaderItemViewHolder(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.header_title);
            tv_mission = (TextView) itemView.findViewById(R.id.header_mission_name);
        }

    }

    private class FooterItemViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll;
        FrameLayout fl;

        FooterItemViewHolder(View itemView) {
            super(itemView);
            ll = (LinearLayout) itemView.findViewById(R.id.ll_comic_download);
            fl = (FrameLayout) itemView.findViewById(R.id.frame_progress_bar);
        }

    }

}
