package org.huxizhijian.hhcomicviewer2.ui.download;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.SparseArray;
import android.view.View;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.DownloadingComicChapterAdapter;
import org.huxizhijian.hhcomicviewer2.databinding.ActivityDownloadingChapterBinding;
import org.huxizhijian.hhcomicviewer2.model.Comic;
import org.huxizhijian.hhcomicviewer2.model.ComicChapter;
import org.huxizhijian.hhcomicviewer2.service.DownloadManagerService;
import org.huxizhijian.hhcomicviewer2.utils.Constants;

import java.util.List;

public class DownloadingChapterActivity extends OfflineDownloadBaseActivity {

    private ActivityDownloadingChapterBinding mBinding;

    private SparseArray<Comic> mUnFinishedComicList;
    private List<ComicChapter> mUnfinishedComicChapterList;

    private DownloadingComicChapterAdapter mAdapter;

    public final static int NO_ACTION = 0x00;
    public final static int ACTION_PAUSE = 0x01;
    public final static int ACTION_START = 0x02;

    private int mLastAction = NO_ACTION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_downloading_chapter);
        setSupportActionBar(mBinding.toolbar);
        initSupportAppBar("下载中");
        //设置过渡动画
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(buildEnterTransition());
        }
        //设置事件
        mBinding.btnAllSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditModeOpen()) {
                    mAdapter.selectAll();
                } else {
                    editModeOpen();
                }
            }
        });
        mBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditModeOpen()) {
                    mAdapter.deleteClick();
                } else {
                    if (mLastAction == NO_ACTION) {
                        if (checkAllPause()) {
                            startAll();
                        } else {
                            pauseAll();
                        }
                    } else if (mLastAction == ACTION_PAUSE) {
                        if (checkAllPause()) {
                            startAll();
                        }
                    } else if (mLastAction == ACTION_START) {
                        if (!checkAllPause()) {
                            pauseAll();
                        }
                    }
                }
            }
        });
    }

    private void startAll() {
        Intent intent = new Intent(DownloadingChapterActivity.this, DownloadManagerService.class);
        intent.setAction(DownloadManagerService.ACTION_ALL_START);
        startService(intent);
        mLastAction = ACTION_START;
    }

    private void pauseAll() {
        Intent intent = new Intent(DownloadingChapterActivity.this, DownloadManagerService.class);
        intent.setAction(DownloadManagerService.ACTION_ALL_STOP);
        startService(intent);
        mLastAction = ACTION_PAUSE;
    }

    @Override
    protected void initData() {
        mUnfinishedComicChapterList = mComicChapterDBHelper.findUnFinishedChapters();
        mUnFinishedComicList = new SparseArray<>();
        for (ComicChapter chapter : mUnfinishedComicChapterList) {
            if (mUnFinishedComicList.get(chapter.getCid()) != null) {
                //已经记录
                continue;
            }
            Comic comic = mComicDBHelper.findByCid(chapter.getCid());
            mUnFinishedComicList.put(chapter.getCid(), comic);
        }
        if (!isEditModeOpen()) {
            checkAllPause();
        }
    }

    @Override
    protected void initView() {
        if (mAdapter != null) {
            mAdapter.setComics(mUnFinishedComicList);
            mAdapter.setUnfinishedChapterList(mUnfinishedComicChapterList);
            mAdapter.notifyDataSetChanged();
        } else {
            mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
            mAdapter = new DownloadingComicChapterAdapter(this, mUnfinishedComicChapterList, mUnFinishedComicList);
            mBinding.recyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onDownloadStateChanged(int downloadState, ComicChapter comicChapter) {
        initData();
        initView();
        if (!isEditModeOpen()) {
            checkAllPause();
        }
    }

    @Override
    protected void editModeOpen() {
        if (mAdapter != null) {
            mAdapter.openEditMode(-1);
        }
    }

    @Override
    protected void editModeClose() {
        if (mAdapter != null) {
            mAdapter.closeEditMode();
        }
    }

    @Override
    protected boolean isEditModeOpen() {
        return mAdapter != null && mAdapter.isEditModeOn();
    }

    @Override
    public void onEditModeOpen() {
        invalidateOptionsMenu();
        mBinding.btnAllSelect.setText(getResources().getText(R.string.all_select));
        mBinding.btnDelete.setText(getResources().getText(R.string.delete));
    }

    @Override
    public void onEditModeClose() {
        invalidateOptionsMenu();
        initView();
        mBinding.btnAllSelect.setText(getResources().getText(R.string.edit));
        mBinding.btnDelete.setText(getResources().getText(R.string.all_start));
    }

    @Override
    public void onAllSelected() {
        mBinding.btnAllSelect.setText(getResources().getText(R.string.cancel));
    }

    @Override
    public void onNoAllSelected() {
        mBinding.btnAllSelect.setText(getResources().getText(R.string.all_select));
    }

    private boolean checkAllPause() {
        if (mUnfinishedComicChapterList == null && mUnfinishedComicChapterList.size() == 0) {
            mBinding.btnDelete.setText(getResources().getText(R.string.all_start));
            return true;
        }
        for (ComicChapter comicChapter : mUnfinishedComicChapterList) {
            switch (comicChapter.getDownloadStatus()) {
                case Constants.DOWNLOAD_DOWNLOADING:
                case Constants.DOWNLOAD_INIT:
                case Constants.DOWNLOAD_IN_QUEUE:
                case Constants.DOWNLOAD_START:
                    mBinding.btnDelete.setText(getResources().getText(R.string.all_pause));
                    return false;
                case Constants.DOWNLOAD_ERROR:
                case Constants.DOWNLOAD_PAUSE:
                case Constants.DOWNLOAD_FINISHED:
                    break;
            }
        }
        mBinding.btnDelete.setText(getResources().getText(R.string.all_start));
        return true;
    }
}
