package org.huxizhijian.hhcomicviewer2.activities;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.flyco.animation.FadeEnter.FadeEnter;
import com.flyco.animation.FadeExit.FadeExit;
import com.flyco.dialog.widget.popup.BubblePopup;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.VolRecyclerViewAdapter;
import org.huxizhijian.hhcomicviewer2.db.ComicCaptureDBHelper;
import org.huxizhijian.hhcomicviewer2.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer2.enities.Comic;
import org.huxizhijian.hhcomicviewer2.enities.ComicCapture;
import org.huxizhijian.hhcomicviewer2.fragment.DownloadSelectorFragment;
import org.huxizhijian.hhcomicviewer2.service.DownloadService;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.view.FullyGridLayoutManager;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ComicInfoActivity extends FragmentActivity implements OnClickListener, DownloadSelectorFragment.SelectorDataBinder {

    public static final String ACTION_MARKED = "ACTION_MARKED";
    public static final String ACTION_HISTORY = "ACTION_HISTORY";
    public static final String ACTION_SEARCH = "ACTION_SEARCH";

    private TextView mTv_author, mTv_title, mTv_details;
    private ImageView mImageView;
    private RecyclerView mRecyclerView;
    private VolRecyclerViewAdapter mVolAdapter;
    private ScrollView mScrollView;
    private ProgressBar mProgressBar;

    private ComicDBHelper mComicDBHelper;
    private ComicCaptureDBHelper mComicCaptureDBHelper;
    private List<ComicCapture> mDownloadedComicCaptures; //开始下载的章节列表
    private List<String> mFinishedComicCaptures; //下载完成的章节名列表
    private Comic mComic;
    private FragmentTransaction mFt;
    private DownloadSelectorFragment mDsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_info);
        initView();
        mComicDBHelper = ComicDBHelper.getInstance(this);
        mComicCaptureDBHelper = ComicCaptureDBHelper.getInstance(this);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        //查找已经开始下载的章节
        mDownloadedComicCaptures = mComicCaptureDBHelper.findByComicUrl(url);
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
        if (intent.getAction().equals(ACTION_SEARCH)) {
            initData(url);
        } else if (intent.getAction().equals(ACTION_MARKED)) {
            initData(url);
            //如果没有网络，也可以看已经下载的章节
        } else if (intent.getAction().equals(ACTION_HISTORY)) {
            initData(url);
        }

    }

    private void initView() {
        mTv_title = (TextView) findViewById(R.id.textView_comic_title_comic_info);
        mTv_author = (TextView) findViewById(R.id.textView_author_comic_info);
        mTv_details = (TextView) findViewById(R.id.textView_details_comic_info);
        mImageView = (ImageView) findViewById(R.id.imageView_comic_info);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_comic_info);
        mScrollView = (ScrollView) findViewById(R.id.scroll_comic_info);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_comic_info);
        Button btn_mark = (Button) findViewById(R.id.button_mark_comic_info);
        Button btn_read = (Button) findViewById(R.id.button_read_comic_info);
        Button btn_download = (Button) findViewById(R.id.button_download_comic_info);
        Button btn_share = (Button) findViewById(R.id.button_share_comic_info);

        //注册单击事件
        mTv_author.setOnClickListener(this);
        mTv_details.setOnClickListener(this);
        btn_mark.setOnClickListener(this);
        btn_read.setOnClickListener(this);
        btn_download.setOnClickListener(this);
        btn_share.setOnClickListener(this);

        //修改ActionBar
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            //增加左上角返回按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
            //修改ActionBar颜色
            BaseUtils.initActionBar(getActionBar(), Constants.THEME_COLOR);
        }
    }

    private void initData(final String url) {
        mComic = mComicDBHelper.findByUrl(url);
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<byte[]>() {
                    @Override
                    public void onSuccess(byte[] result) {
                        try {
                            final String content = new String(result, "gb2312");
                            if (mComic == null) {
                                mComic = new Comic(url, content);
                                mVolAdapter = new VolRecyclerViewAdapter(ComicInfoActivity.this,
                                        mComic.getCaptureName(), mFinishedComicCaptures);
                            } else {
                                mComic.checkUpdate(content);
                                mVolAdapter = new VolRecyclerViewAdapter(ComicInfoActivity.this,
                                        mComic.getCaptureName(), mComic.getReadCapture(), mFinishedComicCaptures);
                            }
                            //设置activity的label
                            setTitle(mComic.getTitle());
                            //设置各控件的值
                            mTv_title.setText(mComic.getTitle());
                            mTv_author.setText(mComic.getAuthor());
                            mTv_details.setText(mComic.getDescription());
                            Glide.with(ComicInfoActivity.this)
                                    .load(mComic.getThumbnailUrl())
                                    .placeholder(R.mipmap.blank)
                                    .error(R.mipmap.blank)
                                    .fitCenter()
                                    .into(mImageView);
                            //初始化RecyclerView
                            mRecyclerView.setLayoutManager(new FullyGridLayoutManager(ComicInfoActivity.this, 4));
                            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            mVolAdapter.setOnItemClickListener(new VolRecyclerViewAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    Intent intent = new Intent(ComicInfoActivity.this, GalleryActivity.class);
                                    intent.putExtra("comic", mComic);
                                    intent.putExtra("position", position);
                                    startActivityForResult(intent, 0);
                                }

                                @Override
                                public void onItemLongClick(View view, int position) {
                                }
                            });
                            mRecyclerView.setAdapter(mVolAdapter);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                            mScrollView.setVisibility(View.VISIBLE);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        ex.printStackTrace();
                        if (BaseUtils.getAPNType(ComicInfoActivity.this) == BaseUtils.NONEWTWORK) {
                            Toast.makeText(ComicInfoActivity.this, Constants.NO_NETWORK, Toast.LENGTH_SHORT).show();
                            //没有网络，读取数据库中的信息
                            mComic = mComicDBHelper.findByUrl(url);
                            if (mComic.isMark() || mComic.isDownload()) {
                                if (mComic != null && mComic.getCaptureName() != null) {
                                    mTv_title.setText(mComic.getTitle());
                                    mTv_author.setText(mComic.getAuthor());
                                    mTv_details.setText(mComic.getDescription());
                                    Glide.with(ComicInfoActivity.this)
                                            .load(mComic.getThumbnailUrl())
                                            .placeholder(R.mipmap.blank)
                                            .error(R.mipmap.blank)
                                            .crossFade()
                                            .fitCenter()
                                            .into(mImageView);
                                    //初始化RecyclerView
                                    mRecyclerView.setLayoutManager(new FullyGridLayoutManager(ComicInfoActivity.this, 4));
                                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                    mVolAdapter = new VolRecyclerViewAdapter(ComicInfoActivity.this, mComic.getCaptureName()
                                            , mFinishedComicCaptures);
                                    mVolAdapter.setOnItemClickListener(new VolRecyclerViewAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, int position) {
                                            Intent intent = new Intent(ComicInfoActivity.this, GalleryActivity.class);
                                            intent.putExtra("comic", mComic);
                                            intent.putExtra("position", position);
                                            startActivityForResult(intent, 0);
                                        }

                                        @Override
                                        public void onItemLongClick(View view, int position) {
                                        }
                                    });
                                    mRecyclerView.setAdapter(mVolAdapter);
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    mProgressBar.setVisibility(View.GONE);
                                    mScrollView.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            Toast.makeText(ComicInfoActivity.this, "出错！", Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mComic = (Comic) data.getSerializableExtra("comic");
        mComic.setLastReadTime(System.currentTimeMillis());
        if (mVolAdapter != null) {
            mVolAdapter.setReadCapture(mComic.getReadCapture());
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.button_mark_comic_info:
                //收藏
                if (mComic == null) return;
                Comic findComic = mComicDBHelper.findByUrl(mComic.getComicUrl());
                if (findComic != null) {
                    if (findComic.isMark()) {
                        Toast.makeText(ComicInfoActivity.this, "已经收藏过了哦~", Toast.LENGTH_SHORT).show();
                    } else {
                        mComic.setMark(true);
                        mComic.setLastReadTime(System.currentTimeMillis());
                        mComicDBHelper.update(mComic);
                        Toast.makeText(ComicInfoActivity.this, "收藏成功!", Toast.LENGTH_SHORT).show();
                    }
                } else if (mComic != null) {
                    mComic.setMark(true);
                    mComic.setLastReadTime(System.currentTimeMillis());
                    mComicDBHelper.add(mComic);
                    Toast.makeText(ComicInfoActivity.this, "收藏成功!", Toast.LENGTH_SHORT).show();
                } else if (mComic == null) {
                    Toast.makeText(ComicInfoActivity.this, "还没有加载完成，请耐心等待~", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_read_comic_info:
                //阅读
                if (mComic == null) return;
                intent = new Intent(ComicInfoActivity.this, GalleryActivity.class);
                intent.putExtra("comic", mComic);
                intent.putExtra("position", mComic.getReadCapture());
                startActivityForResult(intent, 0);
                break;
            case R.id.button_download_comic_info:
                if (mComic == null) {
                    Toast.makeText(ComicInfoActivity.this, "请等待加载完成~", Toast.LENGTH_SHORT).show();
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
                    mFt.replace(R.id.frame_layout_container, mDsFragment);
                    mFt.addToBackStack(null);
                    mFt.commit();
                    mDsFragment = null;
                    mFt = null;
                }
                break;
            case R.id.button_share_comic_info:
                break;
            case R.id.textView_author_comic_info:
                //搜索同一作者的作品
                if (mComic == null) return;
                intent = new Intent(this, ComicResultListActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, mComic.getAuthor());
                startActivity(intent);
                break;
            case R.id.textView_details_comic_info:
                if (mComic == null) return;
                View pop_layout = View.inflate(this, R.layout.pop_bubble_descript, null);
                TextView textView = (TextView) pop_layout.findViewById(R.id.textView_pop_bubble);
                textView.setText(mComic.getDescription());
                //开启一个气泡
                new BubblePopup(this, pop_layout)
                        .anchorView(mTv_details)
                        .gravity(Gravity.BOTTOM)
                        .bubbleColor(Constants.COLOR_BLACK)
                        .showAnim(new FadeEnter())
                        .dismissAnim(new FadeExit())
                        .show();
                break;
            default:
                break;
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
    public void sendSelectedCaptures(List<String> selectedCaptures) {
        //开始下载
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
        } else if (mComic == null) {
            Toast.makeText(ComicInfoActivity.this, "还没有加载完成，请耐心等待~", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedCaptures.size() != 0) {
            int position = -1;
            ComicCapture capture = null;
            for (String volName : selectedCaptures) {
                position = mComic.getCaptureName().indexOf(volName);
                capture = new ComicCapture(mComic.getTitle(), mComic.getCaptureName().get(position),
                        mComic.getCaptureUrl().get(position), mComic.getComicUrl());
                Intent intent = new Intent(this, DownloadService.class);
                intent.setAction(DownloadService.ACTION_START);
                intent.putExtra("comicCapture", capture);
                startService(intent);
            }
        }
    }

    @Override
    public void hideFragment() {
        onBackPressed();
        mFt = getSupportFragmentManager().beginTransaction();
    }
}
