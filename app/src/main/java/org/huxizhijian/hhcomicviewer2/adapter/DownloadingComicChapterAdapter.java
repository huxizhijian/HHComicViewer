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

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.model.Comic;
import org.huxizhijian.hhcomicviewer2.model.ComicChapter;
import org.huxizhijian.hhcomicviewer2.service.DownloadManagerService;
import org.huxizhijian.hhcomicviewer2.ui.download.listener.OnEditModeListener;
import org.huxizhijian.hhcomicviewer2.utils.CommonUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.sdk.imagedownload.ImageDownloader;
import org.huxizhijian.sdk.imagedownload.core.ImageDownloadRequest;
import org.huxizhijian.sdk.imagedownload.core.Request;
import org.huxizhijian.sdk.imagedownload.core.model.ImageEntity;
import org.huxizhijian.sdk.imageloader.ImageLoaderOptions;
import org.huxizhijian.sdk.imageloader.listener.ImageLoaderManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author huxizhijian 2017/2/24
 */
public class DownloadingComicChapterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //item类型
    private final static int ITEM_COMIC_CHAPTER = 0x03;
    private final static int ITEM_FOOTER = 0x04;

    private Activity mContext;
    private LayoutInflater mInflater;
    private OnEditModeListener mControlListener;

    //数据
    private List<ComicChapter> mUnfinishedChapterList;
    private SparseArray<Comic> mComics;

    //checkbox选择判断
    private Set<Integer> mChapterCbChecked;

    private long mLastStartOrPausePressed;

    private boolean isEditModeOn;

    private ImageLoaderManager mImageLoader = ImageLoaderOptions.getImageLoaderManager();

    private ImageDownloader mImageDownloader = ImageDownloader.getInstance();

    public DownloadingComicChapterAdapter(Activity context, List<ComicChapter> unfinishedChapterList,
                                          SparseArray<Comic> comics) {
        mContext = context;
        mControlListener = (OnEditModeListener) context;
        mUnfinishedChapterList = unfinishedChapterList;
        mComics = comics;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == ITEM_COMIC_CHAPTER) {
            viewHolder = new ComicChapterItemViewHolder(mInflater
                    .inflate(R.layout.item_chapter_downing, parent, false));
        } else if (viewType == ITEM_FOOTER) {
            viewHolder = new FooterItemViewHolder(mInflater
                    .inflate(R.layout.footer_view_comic_download_menu, parent, false));
        }
        return viewHolder;
    }

    public void setComics(SparseArray<Comic> comics) {
        mComics = comics;
    }

    public void setUnfinishedChapterList(List<ComicChapter> unfinishedChapterList) {
        mUnfinishedChapterList = unfinishedChapterList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return ITEM_FOOTER;
        } else {
            return ITEM_COMIC_CHAPTER;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, final int position) {
        if (vh.getItemViewType() == ITEM_COMIC_CHAPTER) {
            ComicChapterItemViewHolder holder = (ComicChapterItemViewHolder) vh;
            setupItemComicChapter(position, holder);
        } else if (vh.getItemViewType() == ITEM_FOOTER) {
            final FooterItemViewHolder holder = (FooterItemViewHolder) vh;
            holder.ll.setVisibility(View.VISIBLE);
        }
    }

    private void setupItemComicChapter(final int position, final ComicChapterItemViewHolder holder) {
        final ComicChapter comicChapter = mUnfinishedChapterList.get(position);
        final Comic comic = mComics.get(comicChapter.getCid());
        mImageLoader.displayThumbnail(mContext, comic.getThumbnailUrl(), holder.iv, R.mipmap.blank,
                R.mipmap.blank, 165, 220);

        //标题
        holder.tv_title.setText(comic.getTitle());
        holder.tv_chapter_title.setText(comicChapter.getChapterName());

        holder.icon_pause.setVisibility(View.VISIBLE);
        holder.cb.setVisibility(View.GONE);
        if (isEditModeOn) {
            holder.icon_pause.setVisibility(View.GONE);
            holder.cb.setVisibility(View.VISIBLE);
            holder.cb.setChecked(mChapterCbChecked.contains(position));
            holder.cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mChapterCbChecked.contains(position)) {
                        mChapterCbChecked.remove(position);
                    } else {
                        mChapterCbChecked.add(position);
                    }
                    isAllSelected();
                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditModeOn) {
                    if (mChapterCbChecked.contains(position)) {
                        mChapterCbChecked.remove(position);
                        holder.cb.setChecked(false);
                    } else {
                        mChapterCbChecked.add(position);
                        holder.cb.setChecked(true);
                    }
                    isAllSelected();
                } else {
                    //禁止连续进行操作，禁止时间1.5s
                    if (System.currentTimeMillis() - mLastStartOrPausePressed <= 1500) return;
                    //进行暂停，开始的操作
                    Intent intent = null;
                    ImageEntity entity = new ImageEntity(comicChapter.getChid(),
                            CommonUtils.getChapterUrl(comicChapter.getCid(),
                                    comicChapter.getChid(), comicChapter.getServerId()),
                            comicChapter.getSavePath(), comicChapter.getServerId());
                    Request request = new ImageDownloadRequest(entity);
                    if (mImageDownloader.isInQueueOrActive(request)) {
                        intent = new Intent(mContext, DownloadManagerService.class);
                        intent.setAction(DownloadManagerService.ACTION_STOP);
                        intent.putExtra("comicChapter", comicChapter);
                        mContext.startService(intent);
                    } else {
                        intent = new Intent(mContext, DownloadManagerService.class);
                        intent.setAction(DownloadManagerService.ACTION_START_RANGE);
                        intent.putExtra("comicChapter", comicChapter);
                        mContext.startService(intent);
                    }
                    mLastStartOrPausePressed = System.currentTimeMillis();
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openEditMode(position);
                return true;
            }
        });

        //状态
        switch (comicChapter.getDownloadStatus()) {
            case Constants.DOWNLOAD_INIT:
            case Constants.DOWNLOAD_IN_QUEUE:
            case Constants.DOWNLOAD_START:
                holder.icon_pause.setImageResource(R.drawable.ic_wait);
                break;
            case Constants.DOWNLOAD_DOWNLOADING:
                holder.icon_pause.setImageResource(R.drawable.ic_pause);
                break;
            case Constants.DOWNLOAD_ERROR:
            case Constants.DOWNLOAD_PAUSE:
            case Constants.DOWNLOAD_FINISHED:
                holder.icon_pause.setImageResource(R.drawable.ic_download);
                break;
        }

        //进度
        holder.tv_progress.setText(comicChapter.getDownloadPosition() + "/" + comicChapter.getPageCount());
        holder.pb.setProgress(comicChapter.getDownloadPosition());
        holder.pb.setMax(comicChapter.getPageCount());
    }

    private void isAllSelected() {
        if (mControlListener == null) return;
        //判断是否全选
        if (mChapterCbChecked.size() == mUnfinishedChapterList.size()) {
            //全选
            mControlListener.onAllSelected();
        } else {
            mControlListener.onNoAllSelected();
        }
    }

    public boolean isEditModeOn() {
        return isEditModeOn;
    }

    public void openEditMode(int chP) {
        //初始化
        if (mChapterCbChecked == null) {
            mChapterCbChecked = new HashSet<>();
        }
        mChapterCbChecked.clear();

        if (chP != -1) {
            mChapterCbChecked.add(chP);
        }

        isEditModeOn = true;
        notifyDataSetChanged();
        if (mControlListener != null) {
            mControlListener.onEditModeOpen();
        }
        isAllSelected();
    }

    public void closeEditMode() {
        isEditModeOn = false;
        notifyDataSetChanged();
        if (mControlListener != null) {
            mControlListener.onEditModeClose();
        }
    }

    public void selectAll() {
        if (mChapterCbChecked.size() == mUnfinishedChapterList.size()) {
            //取消全选
            mChapterCbChecked.clear();
        } else {
            //全选
            mChapterCbChecked.clear();
            for (int i = 0; i < mUnfinishedChapterList.size(); i++) {
                mChapterCbChecked.add(i);
            }
        }
        notifyDataSetChanged();
        isAllSelected();
    }

    public void deleteClick() {
        //删除选中的章节
        if (mChapterCbChecked.size() > 0) {
            List<ComicChapter> removeChapters = new ArrayList<>();
            for (int position : mChapterCbChecked) {
                Intent intent = new Intent(mContext, DownloadManagerService.class);
                intent.setAction(DownloadManagerService.ACTION_DELETE);
                intent.putExtra("comicChapter", mUnfinishedChapterList.get(position));
                removeChapters.add(mUnfinishedChapterList.get(position));
                mContext.startService(intent);
            }
            mUnfinishedChapterList.removeAll(removeChapters);
        }
        closeEditMode();
        if (mControlListener != null) {
            mControlListener.onEditModeClose();
        }
    }

    @Override
    public int getItemCount() {
        return mUnfinishedChapterList.size() + 1;
    }

    private class ComicChapterItemViewHolder extends RecyclerView.ViewHolder {

        CheckBox cb;
        ImageView iv, icon_pause;
        TextView tv_title, tv_chapter_title, tv_progress;
        ProgressBar pb;

        ComicChapterItemViewHolder(View itemView) {
            super(itemView);
            cb = (CheckBox) itemView.findViewById(R.id.checkbox);
            iv = (ImageView) itemView.findViewById(R.id.imageView);
            icon_pause = (ImageView) itemView.findViewById(R.id.icon_play_pause);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_chapter_title = (TextView) itemView.findViewById(R.id.tv_chapter_title);
            tv_progress = (TextView) itemView.findViewById(R.id.tv_progress_download);
            pb = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }

    }

    private class FooterItemViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll;

        FooterItemViewHolder(View itemView) {
            super(itemView);
            ll = (LinearLayout) itemView.findViewById(R.id.ll_comic_download);
        }

    }

}