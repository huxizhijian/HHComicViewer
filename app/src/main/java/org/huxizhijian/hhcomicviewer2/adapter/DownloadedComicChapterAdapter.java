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
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.model.Comic;
import org.huxizhijian.hhcomicviewer2.model.ComicChapter;
import org.huxizhijian.hhcomicviewer2.service.DownloadManagerService;
import org.huxizhijian.hhcomicviewer2.ui.download.listener.OnEditModeListener;
import org.huxizhijian.hhcomicviewer2.ui.entry.ComicDetailsActivity;
import org.huxizhijian.hhcomicviewer2.ui.entry.GalleryActivity;
import org.huxizhijian.sdk.imageloader.ImageLoaderOptions;
import org.huxizhijian.sdk.imageloader.listener.ImageLoaderManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author huxizhijian 2017/2/24
 */
public class DownloadedComicChapterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //item类型
    private final static int ITEM_COMIC_DETAIL = 0x00;
    private final static int ITEM_COMIC_CHAPTER = 0x01;
    private final static int ITEM_FOOTER = 0x02;

    private Activity mContext;
    private LayoutInflater mInflater;
    private OnEditModeListener mControlListener;

    private Comic mComic;
    private List<ComicChapter> mComicChapterList;

    private boolean isEditModeOn;

    private Set<Integer> mChapterCbChecked;

    private ImageLoaderManager mImageLoader = ImageLoaderOptions.getImageLoaderManager();

    public DownloadedComicChapterAdapter(Activity context, List<ComicChapter> comicChapterList, Comic comic) {
        mContext = context;
        mControlListener = (OnEditModeListener) context;
        this.mComic = comic;
        this.mComicChapterList = comicChapterList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == ITEM_COMIC_DETAIL) {
            viewHolder = new ComicDetailItemViewHolder(mInflater
                    .inflate(R.layout.item_comic_chapter_download_comic, parent, false));
        } else if (viewType == ITEM_COMIC_CHAPTER) {
            viewHolder = new ComicChapterItemViewHolder(mInflater
                    .inflate(R.layout.item_comic_chapter_download, parent, false));
        } else if (viewType == ITEM_FOOTER) {
            viewHolder = new FooterItemViewHolder(mInflater
                    .inflate(R.layout.footer_view_comic_download_menu, parent, false));
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return ITEM_FOOTER;
        } else if (position == 0) {
            return ITEM_COMIC_DETAIL;
        } else {
            return ITEM_COMIC_CHAPTER;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, final int position) {
        if (vh.getItemViewType() == ITEM_COMIC_DETAIL) {
            ComicDetailItemViewHolder holder = (ComicDetailItemViewHolder) vh;
            setupItemComicDetail(holder);
        } else if (vh.getItemViewType() == ITEM_FOOTER) {
            final FooterItemViewHolder holder = (FooterItemViewHolder) vh;
            if (isEditModeOn) {
                holder.ll.setVisibility(View.VISIBLE);
            } else {
                holder.ll.setVisibility(View.GONE);
            }
        } else if (vh.getItemViewType() == ITEM_COMIC_CHAPTER) {
            ComicChapterItemViewHolder holder = (ComicChapterItemViewHolder) vh;
            setupItemComicChapter(position, holder);
        }
    }

    private void setupItemComicChapter(final int position, final ComicChapterItemViewHolder holder) {
        final ComicChapter comicChapter = mComicChapterList.get(position - 1);
        holder.tv.setText(comicChapter.getChapterName());
        holder.cb.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditModeOn) {
                    if (mChapterCbChecked.contains(position - 1)) {
                        mChapterCbChecked.remove(position - 1);
                        holder.cb.setChecked(false);
                    } else {
                        mChapterCbChecked.add(position - 1);
                        holder.cb.setChecked(true);
                    }
                    isAllSelected();
                } else {
                    //打开章节
                    Intent intent = new Intent(mContext, GalleryActivity.class);
                    intent.putExtra("comic", mComic);
                    mComic.initChapterNameAndList();
                    intent.putExtra("position", mComic.getChapterId()
                            .indexOf(comicChapter.getChid()));
                    mContext.startActivity(intent);
                }
            }
        });
        if (isEditModeOn) {
            holder.cb.setVisibility(View.VISIBLE);
            holder.cb.setChecked(mChapterCbChecked.contains(position - 1));
            holder.cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mChapterCbChecked.contains(position - 1)) {
                        mChapterCbChecked.remove(position - 1);
                    } else {
                        mChapterCbChecked.add(position - 1);
                    }
                    isAllSelected();
                }
            });
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openEditMode(position - 1);
                return true;
            }
        });
    }

    private void setupItemComicDetail(final ComicDetailItemViewHolder holder) {
        mImageLoader.displayThumbnail(mContext, mComic.getThumbnailUrl(), holder.iv, R.mipmap.blank,
                R.mipmap.blank, 165, 220);

        holder.tv_title.setText(mComic.getTitle());
        holder.tv_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ComicDetailsActivity.class);
                intent.putExtra("cid", mComic.getCid());
                intent.putExtra("thumbnailUrl", mComic.getThumbnailUrl());
                intent.putExtra("title", mComic.getTitle());

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

    }

    private void isAllSelected() {
        //判断是否全选
        if (mChapterCbChecked.size() == mComicChapterList.size()) {
            //全选
            if (mControlListener != null) {
                mControlListener.onAllSelected();
            }
        } else {
            //没有全选
            if (mControlListener != null) {
                mControlListener.onNoAllSelected();
            }
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
        if (mChapterCbChecked.size() != mComicChapterList.size()) {
            //全选
            mComicChapterList.clear();
            for (int i = 0; i < mComicChapterList.size(); i++) {
                mChapterCbChecked.add(i);
            }
        } else {
            //取消全选
            mChapterCbChecked.clear();
        }
        notifyDataSetChanged();
    }

    public void deleteClick() {
        //删除选中的章节
        if (mChapterCbChecked.size() > 0) {
            ArrayList<ComicChapter> removeChapters = new ArrayList<>();
            for (int position : mChapterCbChecked) {
                ComicChapter comicChapter = mComicChapterList.get(position);
                Intent intent = new Intent(mContext, DownloadManagerService.class);
                intent.setAction(DownloadManagerService.ACTION_DELETE);
                intent.putExtra("comicChapter", comicChapter);
                mContext.startService(intent);
                removeChapters.add(comicChapter);
            }
            mComicChapterList.removeAll(removeChapters);
        }
        closeEditMode();
        if (mControlListener != null) {
            mControlListener.onEditModeClose();
        }
    }

    @Override
    public int getItemCount() {
        return mComicChapterList.size() + 2;
    }

    private class ComicDetailItemViewHolder extends RecyclerView.ViewHolder {

        CheckBox cb;
        ImageView iv;
        TextView tv_title, tv_desc, tv_details;

        ComicDetailItemViewHolder(View itemView) {
            super(itemView);
            cb = (CheckBox) itemView.findViewById(R.id.checkbox);
            iv = (ImageView) itemView.findViewById(R.id.imageView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_desc = (TextView) itemView.findViewById(R.id.tv_description);
            tv_details = (TextView) itemView.findViewById(R.id.tv_comic_details);
        }

    }

    private class FooterItemViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll;

        FooterItemViewHolder(View itemView) {
            super(itemView);
            ll = (LinearLayout) itemView.findViewById(R.id.ll_comic_download);
        }

    }

    private class ComicChapterItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv;
        CheckBox cb;

        ComicChapterItemViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.textView_chapter_name_download_manager);
            cb = (CheckBox) itemView.findViewById(R.id.checkbox_child_item);
        }

    }
}
