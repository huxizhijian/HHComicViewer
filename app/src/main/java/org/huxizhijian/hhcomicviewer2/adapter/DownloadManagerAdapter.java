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
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.activities.ComicDetailsActivity;
import org.huxizhijian.hhcomicviewer2.activities.DownloadManagerActivity;
import org.huxizhijian.hhcomicviewer2.activities.GalleryActivity;
import org.huxizhijian.hhcomicviewer2.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer2.enities.Comic;
import org.huxizhijian.hhcomicviewer2.enities.ComicChapter;
import org.huxizhijian.hhcomicviewer2.service.DownloadManager;
import org.huxizhijian.hhcomicviewer2.service.DownloadManagerService;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.view.HorizontalProgressBarWithProgress;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 下载管理适配器
 * Created by wei on 2016/9/15.
 */
public class DownloadManagerAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private DownloadManagerActivity mManagerActivity;
    private List<Comic> mDownloadedComicList;
    private Map<String, List<ComicChapter>> mDownloadedchapterList;
    private boolean mIsEditMode = false; //是否打开checkBox
    private boolean[][] mIsCheckedChild; //记录每个Child的checkbox是否checked
    private boolean[] mIsCheckedGroup; //记录每个Group的checkbox是否checked
    private Bitmap mBitmapPlay;
    private Bitmap mBitmapPause;
    private OnNotifyDataSetChanged mOnNotifyDataSetChanged; //监听
    private DownloadManager mDownloadManager;

    public DownloadManagerAdapter(Context context,
                                  List<Comic> downloadedComicList, Map<String, List<ComicChapter>> downloadedchapterList) {
        this.mDownloadedComicList = downloadedComicList;
        this.mDownloadedchapterList = downloadedchapterList;
        this.mContext = context;
        this.mManagerActivity = (DownloadManagerActivity) context;
        mBitmapPlay = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_media_play);
        mBitmapPause = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_media_pause);
        mDownloadManager = DownloadManager.getInstance(mContext);
        initCheckedStatus();
    }

    private void initCheckedStatus() {
        //初始化checkBoxStatus
        mIsCheckedGroup = new boolean[mDownloadedComicList.size()];
        mIsCheckedChild = new boolean[mDownloadedComicList.size()][];
        for (int i = 0; i < mDownloadedComicList.size(); i++) {
            mIsCheckedChild[i] = new boolean[mDownloadedchapterList.get(mDownloadedComicList.get(i).getComicUrl()).size()];
        }
    }

    public synchronized void setDownloadedchapterList(Map<String, List<ComicChapter>> downloadedchapterList) {
        mDownloadedchapterList = downloadedchapterList;
    }

    public void setDownloadedComicList(List<Comic> downloadedComicList) {
        mDownloadedComicList = downloadedComicList;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getGroupCount() {
        return mDownloadedComicList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mDownloadedchapterList.get(mDownloadedComicList.get(groupPosition).getComicUrl()).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mDownloadedComicList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mDownloadedchapterList.get(mDownloadedComicList.get(groupPosition).getComicUrl()).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.group_item_download_manager, parent, false);
            groupHolder = new GroupHolder();
            groupHolder.tv_comic_title = (TextView) convertView.findViewById(R.id.textView_comic_title_download_manager);
            groupHolder.cb = (CheckBox) convertView.findViewById(R.id.checkbox_group_item);
            groupHolder.iv_comic_info = (ImageView) convertView.findViewById(R.id.imageView_jump_to_comic_info);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        if (groupPosition >= mDownloadedchapterList.size()) {
            groupHolder.tv_comic_title.setText("");
            return convertView;
        }
        groupHolder.tv_comic_title.setText(mDownloadedComicList.get(groupPosition).getTitle());
        //跳转
        groupHolder.iv_comic_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ComicDetailsActivity.class);
                intent.putExtra("url", mDownloadedComicList.get(groupPosition).getComicUrl());
                intent.putExtra("thumbnailUrl", mDownloadedComicList.get(groupPosition).getThumbnailUrl());
                intent.putExtra("title", mDownloadedComicList.get(groupPosition).getTitle());
                mContext.startActivity(intent);
            }
        });
        //如果是EditMode，显示CheckBox
        if (mIsEditMode) {
            groupHolder.cb.setVisibility(View.VISIBLE);
            groupHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mIsCheckedGroup[groupPosition] = isChecked;
                    if (isChecked) {
                        //全部子view为选中
                        for (int i = 0; i < mIsCheckedChild[groupPosition].length; i++) {
                            mIsCheckedChild[groupPosition][i] = true;
                        }
                        notifyDataSetChanged();
                        //刷新子view视图
                        if (mOnNotifyDataSetChanged != null) {
                            mOnNotifyDataSetChanged.onNotify();
                        }
                    } else {
                        boolean childViewAllChecked = true;
                        //判断是否全部子view为选中
                        for (int i = 0; i < mIsCheckedChild[groupPosition].length; i++) {
                            if (!mIsCheckedChild[groupPosition][i]) {
                                //有一个不选中，即为false
                                childViewAllChecked = false;
                            }
                        }
                        //如果子view全都被选中
                        if (childViewAllChecked) {
                            //将全部子view设为不选中
                            for (int i = 0; i < mIsCheckedChild[groupPosition].length; i++) {
                                mIsCheckedChild[groupPosition][i] = false;
                            }
                            notifyDataSetChanged();
                            //刷新子view视图
                            if (mOnNotifyDataSetChanged != null) {
                                mOnNotifyDataSetChanged.onNotify();
                            }
                        }
                    }
                }
            });
            groupHolder.cb.setChecked(mIsCheckedGroup[groupPosition]);
        } else {
            groupHolder.cb.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //view的复用
        ChildHolder childHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.child_item_download_manager, parent, false);
            childHolder = new ChildHolder();
            childHolder.iv_btn = (ImageView) convertView.findViewById(R.id.imageView_download_manager);
            childHolder.tv_chapter_name = (TextView) convertView.findViewById(R.id.textView_chapter_name_download_manager);
            childHolder.tv_download_progress = (TextView) convertView.findViewById(R.id.textView_progress_download_manager);
            childHolder.pb = (HorizontalProgressBarWithProgress) convertView.findViewById(R.id.progress_bar_download_manager);
            childHolder.cb = (CheckBox) convertView.findViewById(R.id.checkbox_child_item);
            childHolder.ll = (LinearLayout) convertView.findViewById(R.id.ll_child);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }

        final ComicChapter chapter = (ComicChapter) getChild(groupPosition, childPosition);

        //设置控件信息
        int downloadStatus = chapter.getDownloadStatus();

        childHolder.pb.setProgress((int) (((float) (chapter.getDownloadPosition()) /
                (float) chapter.getPageCount()) * 100f));
        childHolder.tv_download_progress.setText((chapter.getDownloadPosition()) + "/" + chapter.getPageCount());

        if (mDownloadManager.hasMission() && mDownloadManager.isInQueue(chapter)) {
            childHolder.iv_btn.setImageBitmap(mBitmapPause);
            childHolder.tv_chapter_name.setText(chapter.getChapterName() + " - 队列中");
            chapter.setDownloadStatus(Constants.DOWNLOAD_IN_QUEUE);

        } else {
            switch (downloadStatus) {
                case Constants.DOWNLOAD_FINISHED:
                    childHolder.iv_btn.setImageBitmap(mBitmapPlay);
                    childHolder.tv_chapter_name.setText(chapter.getChapterName() + " - 下载完成");
                    childHolder.pb.setProgress((int) (((float) (chapter.getDownloadPosition() + 1) /
                            (float) chapter.getPageCount()) * 100f));
                    childHolder.tv_download_progress.setText(((chapter.getDownloadPosition()) + 1) +
                            "/" + chapter.getPageCount());
                    break;
                case Constants.DOWNLOAD_PAUSE:
                    childHolder.iv_btn.setImageBitmap(mBitmapPlay);
                    childHolder.tv_chapter_name.setText(chapter.getChapterName() + " - 下载暂停");
                    break;
                case Constants.DOWNLOAD_ERROR:
                    childHolder.iv_btn.setImageBitmap(mBitmapPlay);
                    childHolder.tv_chapter_name.setText(chapter.getChapterName() + " - 下载错误");
                    break;
                case Constants.DOWNLOAD_DOWNLOADING:
                    childHolder.iv_btn.setImageBitmap(mBitmapPause);
                    childHolder.tv_chapter_name.setText(chapter.getChapterName() + " - 正在下载");
                    break;
                case Constants.DOWNLOAD_INIT:
                    childHolder.iv_btn.setImageBitmap(mBitmapPlay);
                    childHolder.tv_chapter_name.setText(chapter.getChapterName() + " - 未开始");
                    break;
                case Constants.DOWNLOAD_START:
                    childHolder.iv_btn.setImageBitmap(mBitmapPause);
                    childHolder.tv_chapter_name.setText(chapter.getChapterName() + " - 下载开始");
                    break;
            }
        }

        if (!mIsEditMode) {
            //设置onClickEvent
            childHolder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (chapter.getDownloadStatus()) {
                        case Constants.DOWNLOAD_FINISHED:
                            //打开章节
                            Comic comic = ComicDBHelper.getInstance(mContext).findByUrl(chapter.getComicUrl());
                            int position = comic.getChapterUrl().indexOf(chapter.getChapterUrl());
                            Intent intent = new Intent(mContext, GalleryActivity.class);
                            intent.putExtra("comic", comic);
                            intent.putExtra("position", position);
                            mContext.startActivity(intent);
                            break;
                        case Constants.DOWNLOAD_PAUSE:
                            if (mManagerActivity.isManagerBackGroundDoing()) return;
                            restart(chapter);
                            mManagerActivity.setManagerBackGroundDoing(true);
                            break;
                        case Constants.DOWNLOAD_ERROR:
                            if (mManagerActivity.isManagerBackGroundDoing()) return;
                            restart(chapter);
                            mManagerActivity.setManagerBackGroundDoing(true);
                            break;
                        case Constants.DOWNLOAD_DOWNLOADING:
                            if (mManagerActivity.isManagerBackGroundDoing()) return;
                            pause(chapter);
                            mManagerActivity.setManagerBackGroundDoing(true);
                            break;
                        case Constants.DOWNLOAD_INIT:
                            if (mManagerActivity.isManagerBackGroundDoing()) return;
                            restart(chapter);
                            mManagerActivity.setManagerBackGroundDoing(true);
                            break;
                        case Constants.DOWNLOAD_START:
                            if (mManagerActivity.isManagerBackGroundDoing()) return;
                            pause(chapter);
                            mManagerActivity.setManagerBackGroundDoing(true);
                            break;
                        case Constants.DOWNLOAD_IN_QUEUE:
                            if (mManagerActivity.isManagerBackGroundDoing()) return;
                            pause(chapter);
                            mManagerActivity.setManagerBackGroundDoing(true);
                            break;
                    }
                }
            });
            childHolder.ll.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!mIsEditMode && mManagerActivity != null) {
                        mIsCheckedChild[groupPosition][childPosition] = true;
                        mManagerActivity.editModeOpen();
                    }
                    return false;
                }
            });
        }

        //如果是EditMode，显示CheckBox
        if (mIsEditMode) {
            final ChildHolder finalChildHolder = childHolder;
            childHolder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalChildHolder.cb.setChecked(!mIsCheckedChild[groupPosition][childPosition]);
                }
            });
            childHolder.ll.setOnLongClickListener(null);
            childHolder.cb.setVisibility(View.VISIBLE);
            childHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mIsCheckedChild[groupPosition][childPosition] = isChecked;
                    if (mIsCheckedGroup[groupPosition]) {
                        //如果子view有一个不选中，本组groupView的checkBox设为false
                        if (!isChecked) {
                            mIsCheckedGroup[groupPosition] = false;
                            notifyDataSetChanged();
                            if (mOnNotifyDataSetChanged != null) {
                                mOnNotifyDataSetChanged.onNotify();
                            }
                        }
                    }
                    if (isChecked) {
                        //如果子view都选中，本组groupView的checkBox设为true
                        boolean childViewAllChecked = true;
                        //判断是否全部子view为选中
                        for (int i = 0; i < mIsCheckedChild[groupPosition].length; i++) {
                            if (!mIsCheckedChild[groupPosition][i]) {
                                //有一个不选中，即为false
                                childViewAllChecked = false;
                            }
                        }

                        if (childViewAllChecked) {
                            mIsCheckedGroup[groupPosition] = true;
                            notifyDataSetChanged();
                            if (mOnNotifyDataSetChanged != null) {
                                mOnNotifyDataSetChanged.onNotify();
                            }
                        }
                    }
                }
            });
            childHolder.cb.setChecked(mIsCheckedChild[groupPosition][childPosition]);
            childHolder.iv_btn.setVisibility(View.GONE);
        } else {
            childHolder.cb.setVisibility(View.GONE);
            childHolder.iv_btn.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private void pause(ComicChapter chapter) {
        //暂停下载
        if (!mManagerActivity.mHasWritePermission) return;
        Intent intent = new Intent(mContext, DownloadManagerService.class);
        intent.setAction(DownloadManagerService.ACTION_STOP);
        intent.putExtra("comicChapter", chapter);
        mContext.startService(intent);
    }

    private void restart(ComicChapter chapter) {
        //重新开始下载
        if (!mManagerActivity.mHasWritePermission) return;
        Intent intent = new Intent(mContext, DownloadManagerService.class);
        intent.setAction(DownloadManagerService.ACTION_START_RANGE);
        intent.putExtra("comicChapter", chapter);
        mContext.startService(intent);
    }

    public boolean isEditMode() {
        return this.mIsEditMode;
    }

    //打开编辑模式
    public void openEditMode() {
        this.mIsEditMode = true;
        //刷新界面
        notifyDataSetChanged();
        if (mOnNotifyDataSetChanged != null) {
            mOnNotifyDataSetChanged.onNotify();
        }
    }

    //关闭编辑模式
    public void closeEditMode() {
        this.mIsEditMode = false;
        //将全部checkBox值设为false
        for (int i = 0; i < mIsCheckedGroup.length; i++) {
            mIsCheckedGroup[i] = false;
            for (int j = 0; j < mIsCheckedChild[i].length; j++) {
                mIsCheckedChild[i][j] = false;
            }
        }
        //刷新界面
        notifyDataSetChanged();
        if (mOnNotifyDataSetChanged != null) {
            mOnNotifyDataSetChanged.onNotify();
        }
    }

    public void setEditMode(boolean editMode) {
        mIsEditMode = editMode;
    }

    public List<ComicChapter> getSelectedChapters() {
        List<ComicChapter> selectedChapter = new ArrayList<>();
        for (int i = 0; i < mIsCheckedGroup.length; i++) {
            if (mIsCheckedGroup[i]) {
                //如果父view的checked为true，将所有子view返回
                selectedChapter.addAll(mDownloadedchapterList.get(mDownloadedComicList.get(i).getComicUrl()));
            } else {
                for (int j = 0; j < mIsCheckedChild[i].length; j++) {
                    if (mIsCheckedChild[i][j]) {
                        selectedChapter.add(mDownloadedchapterList.get(mDownloadedComicList.get(i).getComicUrl()).get(j));
                    }
                }
            }
        }
        if (selectedChapter.size() == 0) {
            return null;
        }
        return selectedChapter;
    }

    public void delete() {
        //删除并刷新界面
        for (int i = 0; i < mIsCheckedGroup.length; i++) {
            if (mIsCheckedGroup[i]) {
                //如果父view的checked为true，将该父控件下的所有子控件删除
                mDownloadedchapterList.get(mDownloadedComicList.get(i).getComicUrl()).clear();
            } else {
                //待删除的章节
                List<ComicChapter> willDeleteChapters = new ArrayList<>();
                //单个子控件删除
                for (int j = 0; j < mIsCheckedChild[i].length; j++) {
                    if (mIsCheckedChild[i][j]) {
                        ComicChapter comicChapter =
                                mDownloadedchapterList.get(mDownloadedComicList.get(i).getComicUrl()).get(j);
                        willDeleteChapters.add(comicChapter);
                    }
                }
                for (ComicChapter chapter : willDeleteChapters) {
                    mDownloadedchapterList.get(mDownloadedComicList.get(i).getComicUrl()).remove(chapter);
                }
            }
        }
        resetCheckStatus();
        //刷新界面
        notifyDataSetChanged();
        if (mOnNotifyDataSetChanged != null) {
            mOnNotifyDataSetChanged.onNotify();
        }
    }

    public void resetCheckStatus() {
        mIsCheckedGroup = new boolean[mDownloadedComicList.size()];
        mIsCheckedChild = new boolean[mDownloadedComicList.size()][];
        for (int i = 0; i < mDownloadedComicList.size(); i++) {
            mIsCheckedChild[i] = new boolean[mDownloadedchapterList.get(mDownloadedComicList.get(i).getComicUrl()).size()];
        }
    }

    public interface OnNotifyDataSetChanged {
        void onNotify();
    }

    public void setOnNotifyDataSetChanged(OnNotifyDataSetChanged onNotifyDataSetChanged) {
        mOnNotifyDataSetChanged = onNotifyDataSetChanged;
    }

    @Override

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
    }

    private class GroupHolder {
        TextView tv_comic_title;
        CheckBox cb;
        ImageView iv_comic_info;
    }

    private class ChildHolder {
        LinearLayout ll;
        ImageView iv_btn;
        TextView tv_chapter_name, tv_download_progress;
        HorizontalProgressBarWithProgress pb;
        CheckBox cb;
    }
}
