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

package org.huxizhijian.hhcomicviewer2.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.VolRecyclerViewAdapter;
import org.huxizhijian.hhcomicviewer2.databinding.ActivityComicDetailsBinding;
import org.huxizhijian.hhcomicviewer2.db.ComicCaptureDBHelper;
import org.huxizhijian.hhcomicviewer2.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer2.enities.Comic;
import org.huxizhijian.hhcomicviewer2.enities.ComicCapture;
import org.huxizhijian.hhcomicviewer2.fragment.DownloadSelectorFragment;
import org.huxizhijian.hhcomicviewer2.service.DownloadManagerService;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.view.FullyGridLayoutManager;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComicDetailsActivity extends AppCompatActivity implements View.OnClickListener,
        DownloadSelectorFragment.SelectorDataBinder {

    ActivityComicDetailsBinding mBinding = null;

    //数据操作
    private ComicDBHelper mComicDBHelper;
    private ComicCaptureDBHelper mComicCaptureDBHelper;

    //数据
    private List<ComicCapture> mDownloadedComicCaptures; //开始下载的章节列表
    private List<String> mFinishedComicCaptures; //下载完成的章节名列表
    private Comic mComic;
    private List<String> mSelectedCaptures;
    private String mUrl;
    private boolean isDescriptionOpen = false;

    //下载选择页fragment
    private FragmentTransaction mFt;
    private DownloadSelectorFragment mDsFragment;

    //章节列表adapter
    private VolRecyclerViewAdapter mVolAdapter;

    //Handler，用于开启下载任务
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == Constants.MSG_DOWNLOAD) {
                ComicCapture capture = (ComicCapture) msg.obj;
                Intent intent = new Intent(ComicDetailsActivity.this, DownloadManagerService.class);
                intent.setAction(DownloadManagerService.ACTION_START);
                intent.putExtra("comicCapture", capture);
                startService(intent);
            }
        }
    };

    //广播接收器
    ComicCaptureDownloadUpdateReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_comic_details);
        initView();
        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");
        String thumbnailUrl = intent.getStringExtra("thumbnailUrl");
        String title = intent.getStringExtra("title");

        //加载缩略图
        Glide.with(this)
                .load(thumbnailUrl)
                .placeholder(R.mipmap.blank)
                .error(R.mipmap.blank)
                .fitCenter()
                .into(mBinding.comicThumbnailComicDetails);
        mBinding.comicTitleComicDetails.setText(title);

        initDBValues();
        initData();
    }

    private void initData() {
        mComic = mComicDBHelper.findByUrl(mUrl);
        RequestParams params = new RequestParams(mUrl);
        x.http().get(params, new Callback.CommonCallback<byte[]>() {
                    @Override
                    public void onSuccess(byte[] result) {
                        try {
                            final String content = new String(result, "utf-8");
                            //初始化
                            if (mComic == null) {
                                mComic = new Comic(mUrl, content);
                            } else {
                                mComic.checkUpdate(content);
                            }
                            updateViews();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ex.printStackTrace();
                        if (BaseUtils.getAPNType(ComicDetailsActivity.this) == BaseUtils.NONEWTWORK) {
                            Toast.makeText(getApplicationContext(), Constants.NO_NETWORK, Toast.LENGTH_SHORT).show();
                            //没有网络，读取数据库中的信息
                            if (mComic != null) {
                                if (mComic.isMark() || mComic.isDownload()) {
                                    updateViews();
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "出错！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        cex.printStackTrace();
                    }

                    @Override
                    public void onFinished() {
                    }
                }

        );
        mReceiver = new ComicCaptureDownloadUpdateReceiver();
    }

    private void updateViews() {
        //加载其他信息
        mBinding.comicAuthorComicDetails.setText(mComic.getAuthor());
        mBinding.comicVolStatusComicDetails.setText(mComic.getComicStatus());
        mBinding.comicFavoriteNumberComicDetails.setText(mComic.getComicFavorite());
        mBinding.comicUpdateTimeComicDetails.setText(mComic.getComicUpdateTime());

        //加载简介
        mBinding.comicDescriptionComicDetails.setText(mComic.getDescription());

        //加载评分信息
        mBinding.ratingBarComicDetails.setRating((mComic.getRatingNumber() / 10.0f) * 5.0f);
        mBinding.ratingBarDescriptionComicDetails.setText(mComic.getRatingNumber() + "分(10分制), " +
                "共计" + mComic.getRatingPeopleNum() + "人评分");

        //章节列表加载
        if (mComic.isMark() || mComic.isDownload()) {
            mVolAdapter = new VolRecyclerViewAdapter(ComicDetailsActivity.this,
                    mComic.getCaptureName(), mComic.getReadCapture(), mFinishedComicCaptures);
        } else {
            mVolAdapter = new VolRecyclerViewAdapter(ComicDetailsActivity.this,
                    mComic.getCaptureName(), mFinishedComicCaptures);
        }

        //初始化RecyclerView
        mBinding.recyclerViewComicDetails.setLayoutManager(new FullyGridLayoutManager(ComicDetailsActivity.this, 4));
        mBinding.recyclerViewComicDetails.setItemAnimator(new DefaultItemAnimator());
        mBinding.recyclerViewComicDetails.setHasFixedSize(true);
        mBinding.recyclerViewComicDetails.setNestedScrollingEnabled(false);
        mVolAdapter.setOnItemClickListener(new VolRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(ComicDetailsActivity.this, GalleryActivity.class);
                intent.putExtra("comic", mComic);
                intent.putExtra("position", position);
                startActivityForResult(intent, 0);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        mBinding.recyclerViewComicDetails.setAdapter(mVolAdapter);

        //设定收藏状态
        if (mComic.isMark()) {
            mBinding.btnFavoriteComicDetails.setImageResource(R.mipmap.my_favorite);
            mBinding.buttonTextFavoriteComicDetails.setText("已收藏");
        }

        //单击时间注册
        mBinding.FABComicDetails.setOnClickListener(this);
        //四大按钮
        mBinding.btnFavorite.setOnClickListener(this);
        mBinding.btnShare.setOnClickListener(this);
        mBinding.btnFind.setOnClickListener(this);
        mBinding.btnDownload.setOnClickListener(this);
        //其他控件
        mBinding.comicAuthorComicDetails.setOnClickListener(this);
        mBinding.comicDescriptionComicDetails.setOnClickListener(this);
    }

    private void initDBValues() {
        mComicDBHelper = ComicDBHelper.getInstance(this);
        mComicCaptureDBHelper = ComicCaptureDBHelper.getInstance(this);
        //查找已经开始下载的章节
        mDownloadedComicCaptures = mComicCaptureDBHelper.findByComicUrl(mUrl);
        //遍历
        if (mDownloadedComicCaptures != null) {
            for (int i = 0; i < mDownloadedComicCaptures.size(); i++) {
                if (mDownloadedComicCaptures.get(i).getDownloadStatus() == Constants.DOWNLOAD_FINISHED) {
                    if (mFinishedComicCaptures == null) {
                        mFinishedComicCaptures = new ArrayList<>();
                    }
                    mFinishedComicCaptures.add(mDownloadedComicCaptures.get(i).getCaptureName());
                }
            }
        }
    }

    private void initView() {
        //actionBar设定
        setSupportActionBar(mBinding.toolbarComicDetails);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        BaseUtils.setStatusBarTint(this, getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comic_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.menu_read_comic_info:
                read();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册receiver
        IntentFilter filter = new IntentFilter(DownloadManagerService.ACTION_RECEIVER);
        registerReceiver(mReceiver, filter);

        //如果有下载内容更新界面
        if (mVolAdapter != null && mComicCaptureDBHelper != null) {
            mDownloadedComicCaptures = mComicCaptureDBHelper.findByComicUrl(mComic.getComicUrl());
            //遍历
            if (mDownloadedComicCaptures != null && mDownloadedComicCaptures.size() != 0) {
                for (int i = 0; i < mDownloadedComicCaptures.size(); i++) {
                    if (mDownloadedComicCaptures.get(i).getDownloadStatus()
                            == Constants.DOWNLOAD_FINISHED) {
                        if (mFinishedComicCaptures == null) {
                            mFinishedComicCaptures = new ArrayList<>();
                        }
                        mFinishedComicCaptures.add(mDownloadedComicCaptures.get(i).getCaptureName());
                    }
                }
                mVolAdapter.setFinishedComicCaptureList(mFinishedComicCaptures);
                //刷新界面
                mVolAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mComic != null && mComic.getLastReadTime() != 0) {
            mComic.setLastReadTime(System.currentTimeMillis());
            if (mComicDBHelper.findByUrl(mComic.getComicUrl()) != null) {
                mComicDBHelper.update(mComic);
                System.out.println("comic update");
            } else {
                mComicDBHelper.add(mComic);
            }
        }

        //注销receiver
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mComic = (Comic) data.getSerializableExtra("comic");
        mComic.setLastReadTime(System.currentTimeMillis());
        if (mVolAdapter != null) {
            mVolAdapter.setReadCapture(mComic.getReadCapture());
        }
    }

    private void read() {
        Intent intent;
        if (mComic == null) return;
        intent = new Intent(ComicDetailsActivity.this, GalleryActivity.class);
        intent.putExtra("comic", mComic);
        intent.putExtra("position", mComic.getReadCapture());
        startActivityForResult(intent, 0);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.FAB_comic_details:
                read();
                break;
            case R.id.btn_favorite:
                //收藏
                if (mComic == null) return;
                Comic findComic = mComicDBHelper.findByUrl(mComic.getComicUrl());
                if (findComic != null) {
                    if (findComic.isMark()) {
                        mComic.setMark(false);
                        mComic.setLastReadTime(System.currentTimeMillis());
                        mComicDBHelper.update(mComic);
                        Toast.makeText(ComicDetailsActivity.this, "取消收藏成功！", Toast.LENGTH_SHORT).show();
                        mBinding.btnFavoriteComicDetails.setImageResource(R.mipmap.favorite);
                        mBinding.buttonTextFavoriteComicDetails.setText("收藏");
                    } else {
                        mComic.setMark(true);
                        mComic.setLastReadTime(System.currentTimeMillis());
                        mComicDBHelper.update(mComic);
                        Toast.makeText(ComicDetailsActivity.this, "收藏成功!", Toast.LENGTH_SHORT).show();
                        mBinding.btnFavoriteComicDetails.setImageResource(R.mipmap.my_favorite);
                        mBinding.buttonTextFavoriteComicDetails.setText("已收藏");
                    }
                } else if (mComic != null) {
                    mComic.setMark(true);
                    mComic.setLastReadTime(System.currentTimeMillis());
                    mComicDBHelper.add(mComic);
                    mComic.setId(mComicDBHelper.findByUrl(mComic.getComicUrl()).getId());
                    Toast.makeText(ComicDetailsActivity.this, "收藏成功!", Toast.LENGTH_SHORT).show();
                    mBinding.btnFavoriteComicDetails.setImageResource(R.mipmap.my_favorite);
                    mBinding.buttonTextFavoriteComicDetails.setText("已收藏");
                } else if (mComic == null) {
                    Toast.makeText(ComicDetailsActivity.this, "还没有加载完成，请耐心等待~", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_share:
                //分享
                break;
            case R.id.btn_find:
                //搜索同一作者的作品
                if (mComic == null) return;
                intent = new Intent(this, ComicResultListActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, mComic.getAuthor());
                startActivity(intent);
                break;
            case R.id.btn_download:
                //下载
                if (mComic == null) {
                    Toast.makeText(ComicDetailsActivity.this, "请等待加载完成~", Toast.LENGTH_SHORT).show();
                } else {
                    //下载按钮事件
                    if (mFt == null) {
                        mFt = getSupportFragmentManager().beginTransaction();
                    }
                    mDsFragment = new DownloadSelectorFragment();
                    //设置动画
                    mFt.setCustomAnimations(
                            R.anim.menu_show_action,
                            R.anim.menu_hide_action,
                            R.anim.menu_show_action,
                            R.anim.menu_hide_action);

                    //让FAB消失
                    mBinding.FABComicDetails.setVisibility(View.GONE);

                    mFt.replace(R.id.comic_details_content, mDsFragment);
                    mFt.addToBackStack(null);
                    mFt.commit();
                    mDsFragment = null;
                    mFt = null;
                }
                break;
            case R.id.comic_author_comic_details:
                //搜索同一作者的作品
                if (mComic == null) return;
                intent = new Intent(this, ComicResultListActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, mComic.getAuthor());
                startActivity(intent);
                break;
            case R.id.comic_description_comic_details:
                //打开漫画简介
                if (!isDescriptionOpen) {
                    mBinding.comicDescriptionComicDetails.setMaxLines(1024);
                    mBinding.comicDescriptionComicDetails.setText(mComic.getDescription());
                    isDescriptionOpen = !isDescriptionOpen;
                } else {
                    mBinding.comicDescriptionComicDetails.setMaxLines(4);
                    mBinding.comicDescriptionComicDetails.setText(mComic.getDescription());
                    isDescriptionOpen = !isDescriptionOpen;
                }
                break;
        }
    }

    @Override
    public Comic getComic() {
        return mComic;
    }

    @Override
    public List<String> getDownloadedComicCaptures() {
        if (mDownloadedComicCaptures != null) {
            List<String> downloadedComicCapture = new ArrayList<>();
            for (int i = 0; i < mDownloadedComicCaptures.size(); i++) {
                downloadedComicCapture.add(mDownloadedComicCaptures.get(i).getCaptureName());
            }
            return downloadedComicCapture;
        } else {
            return null;
        }
    }

    @Override
    public List<String> getFinishedComicCaptures() {
        return mFinishedComicCaptures;
    }

    @Override
    public void sendSelectedCaptures(List<String> selectedCaptures) {
        //更新漫画的数据库资料
        Comic findComic = mComicDBHelper.findByUrl(mComic.getComicUrl());
        //设置下载标记为true
        mComic.setDownload(true);
        if (findComic != null) {
            //如果在数据库中，更新
            mComic.setLastReadTime(System.currentTimeMillis());
            mComicDBHelper.update(mComic);
        } else if (mComic != null) {
            //如果该漫画不在数据库中
            mComic.setLastReadTime(System.currentTimeMillis());
            //加入数据库
            mComicDBHelper.add(mComic);
            mComic.setId(mComicDBHelper.findByUrl(mComic.getComicUrl()).getId());
        } else if (mComic == null) {
            Toast.makeText(ComicDetailsActivity.this, "还没有加载完成，请耐心等待~", Toast.LENGTH_SHORT).show();
            return;
        }

        //开始下载
        if (selectedCaptures.size() != 0) {
            //检查自身权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "没有获得权限，无法下载！", Toast.LENGTH_SHORT).show();
                    mSelectedCaptures = selectedCaptures;
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    mSelectedCaptures = selectedCaptures;
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
                return;
            }

            int position = -1;
            List<Integer> positionList = new ArrayList<>();
            ComicCapture capture = null;
            for (String volName : selectedCaptures) {
                position = mComic.getCaptureName().indexOf(volName);
                positionList.add(position);
            }
            //将下载任务排序
            Collections.sort(positionList);
            for (int i = 0; i < positionList.size(); i++) {
                position = positionList.get(i);
                capture = new ComicCapture(mComic.getTitle(), mComic.getCaptureName().get(position),
                        mComic.getCaptureUrl().get(position), mComic.getComicUrl());
                Message msg = new Message();
                msg.what = Constants.MSG_DOWNLOAD;
                msg.obj = capture;
                //将任务按照顺序加入队伍，时间间隔1000ms
                mHandler.sendMessageDelayed(msg, 1000 * i);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    if (mSelectedCaptures != null && mSelectedCaptures.size() != 0) {
                        int position = -1;
                        List<Integer> positionList = new ArrayList<>();
                        ComicCapture capture = null;
                        for (String volName : mSelectedCaptures) {
                            position = mComic.getCaptureName().indexOf(volName);
                            positionList.add(position);
                        }
                        //将下载任务排序
                        Collections.sort(positionList);
                        for (int i = 0; i < positionList.size(); i++) {
                            position = positionList.get(i);
                            capture = new ComicCapture(mComic.getTitle(), mComic.getCaptureName().get(position),
                                    mComic.getCaptureUrl().get(position), mComic.getComicUrl());
                            Message msg = new Message();
                            msg.what = Constants.MSG_DOWNLOAD;
                            msg.obj = capture;
                            //将任务按照顺序加入队伍，时间间隔1000ms
                            mHandler.sendMessageDelayed(msg, 1000 * i);
                        }
                        mSelectedCaptures = null;
                    }
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    return;
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void hideFragment() {
        onBackPressed();
        mFt = getSupportFragmentManager().beginTransaction();
    }

    @Override
    public void onBackPressed() {
        if (mBinding.FABComicDetails.getVisibility() == View.GONE) {
            mBinding.FABComicDetails.setVisibility(View.VISIBLE);
        }
        super.onBackPressed();
    }

    class ComicCaptureDownloadUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(DownloadManagerService.ACTION_RECEIVER)) return;
            if (mComic == null || mComicCaptureDBHelper == null) return;
            ComicCapture comicCapture = (ComicCapture) intent.getSerializableExtra("comicCapture");
            if (comicCapture == null) return;
            if (!comicCapture.getComicUrl().equals(mComic.getComicUrl())) return;

            if (comicCapture.getDownloadStatus() != Constants.DOWNLOAD_DOWNLOADING ||
                    comicCapture.getDownloadStatus() != Constants.DOWNLOAD_ERROR ||
                    comicCapture.getDownloadStatus() != Constants.DOWNLOAD_PAUSE) {
                mDownloadedComicCaptures = mComicCaptureDBHelper.findByComicUrl(mComic.getComicUrl());
                if (comicCapture.getDownloadStatus() == Constants.DOWNLOAD_FINISHED) {
                    //更新界面
                    if (mVolAdapter == null) return;
                    //遍历
                    for (int i = 0; i < mDownloadedComicCaptures.size(); i++) {
                        if (mDownloadedComicCaptures.get(i).getDownloadStatus()
                                == Constants.DOWNLOAD_FINISHED) {
                            if (mFinishedComicCaptures == null) {
                                mFinishedComicCaptures = new ArrayList<>();
                            }
                            mFinishedComicCaptures.add(mDownloadedComicCaptures.get(i).getCaptureName());
                        }
                    }
                    mVolAdapter.setFinishedComicCaptureList(mFinishedComicCaptures);
                    //刷新界面
                    mVolAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
