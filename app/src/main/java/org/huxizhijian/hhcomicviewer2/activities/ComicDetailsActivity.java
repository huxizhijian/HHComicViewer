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
import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
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
import android.transition.Fade;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.VolRecyclerViewAdapter;
import org.huxizhijian.hhcomicviewer2.app.HHApplication;
import org.huxizhijian.hhcomicviewer2.databinding.ActivityComicDetailsBinding;
import org.huxizhijian.hhcomicviewer2.db.ComicChapterDBHelper;
import org.huxizhijian.hhcomicviewer2.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer2.enities.Comic;
import org.huxizhijian.hhcomicviewer2.enities.ComicChapter;
import org.huxizhijian.hhcomicviewer2.fragment.DownloadSelectorFragment;
import org.huxizhijian.hhcomicviewer2.service.DownloadManagerService;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.view.FullyGridLayoutManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ComicDetailsActivity extends AppCompatActivity implements View.OnClickListener,
        DownloadSelectorFragment.SelectorDataBinder {

    private ActivityComicDetailsBinding mBinding = null;

    //数据操作
    private ComicDBHelper mComicDBHelper;
    private ComicChapterDBHelper mComicChapterDBHelper;

    //数据
    private List<ComicChapter> mDownloadedComicChapters; //开始下载的章节列表
    private List<String> mFinishedComicChapters; //下载完成的章节名列表
    private Comic mComic;
    private List<String> mSelectedChapters;
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
                ComicChapter Chapter = (ComicChapter) msg.obj;
                Intent intent = new Intent(ComicDetailsActivity.this, DownloadManagerService.class);
                intent.setAction(DownloadManagerService.ACTION_START);
                intent.putExtra("comicChapter", Chapter);
                startService(intent);
            }
        }
    };

    //广播接收器
    ComicChapterDownloadUpdateReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_comic_details);
        initView();
        preLoadingImageAndTitle();
        initDBValues();
        initData();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            setupEnterAnimations();
            setupExitAnimations();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animateRevealShow(View viewRoot) {
        //获取中心点坐标
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        //获取宽高的中的最大值
        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
        viewRoot.setVisibility(View.VISIBLE); //设为可见
        anim.setDuration(getResources().getInteger(R.integer.anim_duration_medium));//设置时间
        anim.setInterpolator(new AccelerateInterpolator());//设置插补器 一开始慢后来快
        anim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupEnterAnimations() {
        Fade enterTransition = new Fade();  //淡入淡出
        getWindow().setEnterTransition(enterTransition);
        enterTransition.setDuration(getResources().getInteger(R.integer.anim_duration_medium));//时间
        enterTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                transition.removeListener(this);
                animateRevealShow(mBinding.appBarComicDetails);//toolbar的缩放动画
            }

            @Override
            public void onTransitionEnd(Transition transition) {

            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupExitAnimations() {
        Fade returnTransition = new Fade();  //淡入淡出
        getWindow().setReturnTransition(returnTransition);
        returnTransition.setDuration(getResources().getInteger(R.integer.anim_duration_medium));//时间
        returnTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                //移除监听
                transition.removeListener(this);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });
    }

    private void preLoadingImageAndTitle() {
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
    }

    private void initData() {
        mComic = mComicDBHelper.findByUrl(mUrl);
        Request request = new Request.Builder().url(mUrl).build();
        OkHttpClient client = ((HHApplication) getApplication()).getClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (BaseUtils.getAPNType(ComicDetailsActivity.this) == BaseUtils.NONEWTWORK) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), Constants.NO_NETWORK, Toast.LENGTH_SHORT).show();
                            //没有网络，读取数据库中的信息
                            if (mComic != null) {
                                if (mComic.isMark() || mComic.isDownload()) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateViews();
                                        }
                                    });
                                }
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "出错！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    byte[] result = response.body().bytes();
                    final String content = new String(result, "utf-8");
                    //初始化
                    if (mComic == null) {
                        mComic = new Comic(mUrl, content);
                    } else {
                        mComic.checkUpdate(content);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateViews();
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        mReceiver = new ComicChapterDownloadUpdateReceiver();
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
                    mComic.getChapterName(), mComic.getReadChapter(), mFinishedComicChapters);
        } else {
            mVolAdapter = new VolRecyclerViewAdapter(ComicDetailsActivity.this,
                    mComic.getChapterName(), mFinishedComicChapters);
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
        mComicChapterDBHelper = ComicChapterDBHelper.getInstance(this);
        //查找已经开始下载的章节
        mDownloadedComicChapters = mComicChapterDBHelper.findByComicUrl(mUrl);
        //遍历
        if (mDownloadedComicChapters != null) {
            for (int i = 0; i < mDownloadedComicChapters.size(); i++) {
                if (mDownloadedComicChapters.get(i).getDownloadStatus() == Constants.DOWNLOAD_FINISHED) {
                    if (mFinishedComicChapters == null) {
                        mFinishedComicChapters = new ArrayList<>();
                    }
                    mFinishedComicChapters.add(mDownloadedComicChapters.get(i).getChapterName());
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
        if (mVolAdapter != null && mComicChapterDBHelper != null) {
            mDownloadedComicChapters = mComicChapterDBHelper.findByComicUrl(mComic.getComicUrl());
            //遍历
            if (mDownloadedComicChapters != null && mDownloadedComicChapters.size() != 0) {
                for (int i = 0; i < mDownloadedComicChapters.size(); i++) {
                    if (mDownloadedComicChapters.get(i).getDownloadStatus()
                            == Constants.DOWNLOAD_FINISHED) {
                        if (mFinishedComicChapters == null) {
                            mFinishedComicChapters = new ArrayList<>();
                        }
                        mFinishedComicChapters.add(mDownloadedComicChapters.get(i).getChapterName());
                    }
                }
                mVolAdapter.setFinishedComicChapterList(mFinishedComicChapters);
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
            mVolAdapter.setReadChapter(mComic.getReadChapter());
        }
    }

    private void read() {
        //单击toolbar的按钮
        if (mComic == null || mComic.getChapterUrl() == null || mComic.getChapterUrl().size() == 0)
            return;
        Intent intent = new Intent(ComicDetailsActivity.this, GalleryActivity.class);
        intent.putExtra("comic", mComic);
        intent.putExtra("position", mComic.getReadChapter());
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

    public List<String> getDownloadedComicChapters() {
        if (mDownloadedComicChapters != null) {
            List<String> downloadedComicChapter = new ArrayList<>();
            for (int i = 0; i < mDownloadedComicChapters.size(); i++) {
                downloadedComicChapter.add(mDownloadedComicChapters.get(i).getChapterName());
            }
            return downloadedComicChapter;
        } else {
            return null;
        }
    }

    @Override
    public List<String> getFinishedComicChapters() {
        return mFinishedComicChapters;
    }

    @Override
    public void sendSelectedChapters(List<String> selectedChapters) {
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
        if (selectedChapters.size() != 0) {
            //检查自身权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "没有获得权限，无法下载！", Toast.LENGTH_SHORT).show();
                    mSelectedChapters = selectedChapters;
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    mSelectedChapters = selectedChapters;
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
                return;
            }

            int position = -1;
            List<Integer> positionList = new ArrayList<>();
            ComicChapter Chapter = null;
            for (String volName : selectedChapters) {
                position = mComic.getChapterName().indexOf(volName);
                positionList.add(position);
            }
            //将下载任务排序
            Collections.sort(positionList);
            for (int i = 0; i < positionList.size(); i++) {
                position = positionList.get(i);
                Chapter = new ComicChapter(mComic.getTitle(), mComic.getChapterName().get(position),
                        mComic.getChapterUrl().get(position), mComic.getComicUrl());
                Message msg = new Message();
                msg.what = Constants.MSG_DOWNLOAD;
                msg.obj = Chapter;
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
                    if (mSelectedChapters != null && mSelectedChapters.size() != 0) {
                        int position = -1;
                        List<Integer> positionList = new ArrayList<>();
                        ComicChapter Chapter = null;
                        for (String volName : mSelectedChapters) {
                            position = mComic.getChapterName().indexOf(volName);
                            positionList.add(position);
                        }
                        //将下载任务排序
                        Collections.sort(positionList);
                        for (int i = 0; i < positionList.size(); i++) {
                            position = positionList.get(i);
                            Chapter = new ComicChapter(mComic.getTitle(), mComic.getChapterName().get(position),
                                    mComic.getChapterUrl().get(position), mComic.getComicUrl());
                            Message msg = new Message();
                            msg.what = Constants.MSG_DOWNLOAD;
                            msg.obj = Chapter;
                            //将任务按照顺序加入队伍，时间间隔1000ms
                            mHandler.sendMessageDelayed(msg, 1000 * i);
                        }
                        mSelectedChapters = null;
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

    class ComicChapterDownloadUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(DownloadManagerService.ACTION_RECEIVER)) return;
            if (mComic == null || mComicChapterDBHelper == null) return;
            ComicChapter comicChapter = (ComicChapter) intent.getSerializableExtra("comicChapter");
            if (comicChapter == null) return;
            if (!comicChapter.getComicUrl().equals(mComic.getComicUrl())) return;

            if (comicChapter.getDownloadStatus() != Constants.DOWNLOAD_DOWNLOADING ||
                    comicChapter.getDownloadStatus() != Constants.DOWNLOAD_ERROR ||
                    comicChapter.getDownloadStatus() != Constants.DOWNLOAD_PAUSE) {
                mDownloadedComicChapters = mComicChapterDBHelper.findByComicUrl(mComic.getComicUrl());
                if (comicChapter.getDownloadStatus() == Constants.DOWNLOAD_FINISHED) {
                    //更新界面
                    if (mVolAdapter == null) return;
                    //遍历
                    for (int i = 0; i < mDownloadedComicChapters.size(); i++) {
                        if (mDownloadedComicChapters.get(i).getDownloadStatus()
                                == Constants.DOWNLOAD_FINISHED) {
                            if (mFinishedComicChapters == null) {
                                mFinishedComicChapters = new ArrayList<>();
                            }
                            mFinishedComicChapters.add(mDownloadedComicChapters.get(i).getChapterName());
                        }
                    }
                    mVolAdapter.setFinishedComicChapterList(mFinishedComicChapters);
                    //刷新界面
                    mVolAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
