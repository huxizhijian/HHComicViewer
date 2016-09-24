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
import android.widget.TextView;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.activities.GalleryActivity;
import org.huxizhijian.hhcomicviewer2.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer2.enities.Comic;
import org.huxizhijian.hhcomicviewer2.enities.ComicCapture;
import org.huxizhijian.hhcomicviewer2.service.DownloadService;
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
    private List<Comic> mDownloadedComicList;
    private Map<String, List<ComicCapture>> mDownloadedCaptureList;
    private boolean mIsEditMode = false; //是否打开checkBox
    private boolean[][] mIsCheckedChild; //记录每个Child的checkbox是否checked
    private boolean[] mIsCheckedGroup; //记录每个Group的checkbox是否checked
    private Bitmap mBitmapPlay;
    private Bitmap mBitmapPause;
    private OnNotifyDataSetChanged mOnNotifyDataSetChanged; //监听

    public DownloadManagerAdapter(Context context,
                                  List<Comic> downloadedComicList, Map<String, List<ComicCapture>> downloadedCaptureList) {
        this.mDownloadedComicList = downloadedComicList;
        this.mDownloadedCaptureList = downloadedCaptureList;
        this.mContext = context;
        mBitmapPlay = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_media_play);
        mBitmapPause = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_media_pause);
        initCheckedStatus();
    }

    private void initCheckedStatus() {
        //初始化checkBoxStatus
        mIsCheckedGroup = new boolean[mDownloadedComicList.size()];
        mIsCheckedChild = new boolean[mDownloadedComicList.size()][];
        for (int i = 0; i < mDownloadedComicList.size(); i++) {
            mIsCheckedChild[i] = new boolean[mDownloadedCaptureList.get(mDownloadedComicList.get(i).getComicUrl()).size()];
        }
    }

    public synchronized void setDownloadedCaptureList(Map<String, List<ComicCapture>> downloadedCaptureList) {
        mDownloadedCaptureList = downloadedCaptureList;
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
        return mDownloadedCaptureList.get(mDownloadedComicList.get(groupPosition).getComicUrl()).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mDownloadedComicList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mDownloadedCaptureList.get(mDownloadedComicList.get(groupPosition).getComicUrl()).get(childPosition);
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
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        if (groupPosition >= mDownloadedCaptureList.size()) {
            groupHolder.tv_comic_title.setText("");
            return convertView;
        }
        groupHolder.tv_comic_title.setText(mDownloadedComicList.get(groupPosition).getTitle());
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
            childHolder.tv_capture_name = (TextView) convertView.findViewById(R.id.textView_capture_name_download_manager);
            childHolder.tv_download_progress = (TextView) convertView.findViewById(R.id.textView_progress_download_manager);
            childHolder.pb = (HorizontalProgressBarWithProgress) convertView.findViewById(R.id.progress_bar_download_manager);
            childHolder.cb = (CheckBox) convertView.findViewById(R.id.checkbox_child_item);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }

        final ComicCapture capture = (ComicCapture) getChild(groupPosition, childPosition);
        int downloadStatus = capture.getDownloadStatus();
        //设置控件信息
        switch (downloadStatus) {
            case Constants.DOWNLOAD_FINISHED:
                childHolder.iv_btn.setImageBitmap(mBitmapPlay);
                childHolder.tv_capture_name.setText(capture.getCaptureName() + " - 下载完毕");
                childHolder.tv_download_progress.setText(capture.getDownloadPosition() + 1 + "/" + capture.getPageCount());
                childHolder.pb.setProgress((int) (((float) (capture.getDownloadPosition() + 1) /
                        (float) capture.getPageCount()) * 100f));
                break;
            case Constants.DOWNLOAD_PAUSE:
                childHolder.iv_btn.setImageBitmap(mBitmapPlay);
                childHolder.tv_capture_name.setText(capture.getCaptureName() + " - 下载暂停");
                childHolder.tv_download_progress.setText(capture.getDownloadPosition() + "/" + capture.getPageCount());
                childHolder.pb.setProgress((int) (((float) (capture.getDownloadPosition() + 1) /
                        (float) capture.getPageCount()) * 100f));
                break;
            case Constants.DOWNLOAD_ERROR:
                childHolder.iv_btn.setImageBitmap(mBitmapPlay);
                childHolder.tv_capture_name.setText(capture.getCaptureName() + " - 下载错误");
                childHolder.tv_download_progress.setText(capture.getDownloadPosition() + "/" + capture.getPageCount());
                childHolder.pb.setProgress((int) (((float) (capture.getDownloadPosition() + 1) /
                        (float) capture.getPageCount()) * 100f));
                break;
            case Constants.DOWNLOAD_DOWNLOADING:
                childHolder.iv_btn.setImageBitmap(mBitmapPause);
                childHolder.tv_capture_name.setText(capture.getCaptureName() + " - 正在下载");
                childHolder.tv_download_progress.setText(capture.getDownloadPosition() + "/" + capture.getPageCount());
                childHolder.pb.setProgress((int) (((float) (capture.getDownloadPosition() + 1) /
                        (float) capture.getPageCount()) * 100f));
                break;
            case Constants.DOWNLOAD_INIT:
                childHolder.iv_btn.setImageBitmap(mBitmapPause);
                childHolder.tv_capture_name.setText(capture.getCaptureName() + " - 等待");
                childHolder.tv_download_progress.setText(capture.getDownloadPosition() + "/" + capture.getPageCount());
                childHolder.pb.setProgress((int) (((float) (capture.getDownloadPosition()) /
                        (float) capture.getPageCount()) * 100f));
                break;
            case Constants.DOWNLOAD_START:
                childHolder.iv_btn.setImageBitmap(mBitmapPause);
                childHolder.tv_capture_name.setText(capture.getCaptureName() + " - 下载开始");
                childHolder.tv_download_progress.setText(capture.getDownloadPosition() + "/" + capture.getPageCount());
                childHolder.pb.setProgress((int) (((float) (capture.getDownloadPosition() + 1) /
                        (float) capture.getPageCount()) * 100f));
                break;
        }

        //设置onClickEvent
        childHolder.iv_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (capture.getDownloadStatus()) {
                    case Constants.DOWNLOAD_FINISHED:
                        //打开章节
                        Comic comic = ComicDBHelper.getInstance(mContext).findByUrl(capture.getComicUrl());
                        int position = comic.getCaptureUrl().indexOf(capture.getCaptureUrl());
                        Intent intent = new Intent(mContext, GalleryActivity.class);
                        intent.putExtra("comic", comic);
                        intent.putExtra("position", position);
                        mContext.startActivity(intent);
                        break;
                    case Constants.DOWNLOAD_PAUSE:
                        restart(capture);
                        break;
                    case Constants.DOWNLOAD_ERROR:
                        restart(capture);
                        break;
                    case Constants.DOWNLOAD_DOWNLOADING:
                        pause(capture);
                        break;
                    case Constants.DOWNLOAD_INIT:
                        pause(capture);
                        break;
                    case Constants.DOWNLOAD_START:
                        pause(capture);
                        break;
                }
            }
        });

        //如果是EditMode，显示CheckBox
        if (mIsEditMode) {
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

    private void pause(ComicCapture capture) {
        //暂停下载
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.setAction(DownloadService.ACTION_STOP);
        intent.putExtra("comicCapture", capture);
        mContext.startService(intent);
    }

    private void restart(ComicCapture capture) {
        //重新开始下载
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.setAction(DownloadService.ACTION_START_RANGE);
        intent.putExtra("comicCapture", capture);
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

    public List<ComicCapture> getSelectedCaptures() {
        List<ComicCapture> selectedCapture = new ArrayList<>();
        for (int i = 0; i < mIsCheckedGroup.length; i++) {
            if (mIsCheckedGroup[i]) {
                //如果父view的checked为true，将所有子view返回
                selectedCapture.addAll(mDownloadedCaptureList.get(mDownloadedComicList.get(i).getComicUrl()));
            } else {
                for (int j = 0; j < mIsCheckedChild[i].length; j++) {
                    if (mIsCheckedChild[i][j]) {
                        selectedCapture.add(mDownloadedCaptureList.get(mDownloadedComicList.get(i).getComicUrl()).get(j));
                    }
                }
            }
        }
        if (selectedCapture.size() == 0) {
            return null;
        }
        return selectedCapture;
    }

    public void delete() {
        //删除并刷新界面
        for (int i = 0; i < mIsCheckedGroup.length; i++) {
            if (mIsCheckedGroup[i]) {
                //如果父view的checked为true，将该父控件下的所有子控件删除
                mDownloadedCaptureList.get(mDownloadedComicList.get(i).getComicUrl()).clear();
            } else {
                //单个子控件删除
                for (int j = 0; j < mIsCheckedChild[i].length; j++) {
                    if (mIsCheckedChild[i][j]) {
                        ComicCapture comicCapture =
                                mDownloadedCaptureList.get(mDownloadedComicList.get(i).getComicUrl()).get(j);
                        mDownloadedCaptureList.get(mDownloadedComicList.get(i).getComicUrl()).remove(comicCapture);
                    }
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
            mIsCheckedChild[i] = new boolean[mDownloadedCaptureList.get(mDownloadedComicList.get(i).getComicUrl()).size()];
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
    }

    private class ChildHolder {
        ImageView iv_btn;
        TextView tv_capture_name, tv_download_progress;
        HorizontalProgressBarWithProgress pb;
        CheckBox cb;
    }
}
