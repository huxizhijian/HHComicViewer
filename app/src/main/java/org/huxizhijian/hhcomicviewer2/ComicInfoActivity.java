package org.huxizhijian.hhcomicviewer2;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.huxizhijian.hhcomicviewer2.adapter.VolRecyclerViewAdapter;
import org.huxizhijian.hhcomicviewer2.db.ComicCaptureDBHelper;
import org.huxizhijian.hhcomicviewer2.db.ComicDBHelper;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.vo.Comic;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ComicInfoActivity extends Activity implements OnClickListener {

    public static final String ACTION_MARKED = "ACTION_MARKED";
    public static final String ACTION_HISTORY = "ACTION_HISTORY";
    public static final String ACTION_SEARCH = "ACTION_SEARCH";

    private TextView mTv_author, mTv_title, mTv_details;
    private ImageView mImageView;
    private RecyclerView mRecyclerView;
    private VolRecyclerViewAdapter mVolAdapter;
    private ComicDBHelper comicDBHelper;
    private ComicCaptureDBHelper comicCaptureDBHelper;
    private Comic comic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_info);
        initView();
        comicDBHelper = ComicDBHelper.getInstance(this);
        comicCaptureDBHelper = ComicCaptureDBHelper.getInstance(this);
        Intent intent = getIntent();
        if (intent.getAction().equals(ACTION_SEARCH)) {
            initData(getIntent().getStringExtra("url"));
        } else if (intent.getAction().equals(ACTION_MARKED)) {
            initData(getIntent().getStringExtra("url"));
            //如果没有网络，也可以看已经下载的章节
        } else if (intent.getAction().equals(ACTION_HISTORY)) {
            initData(getIntent().getStringExtra("url"));
        }
    }

    private void initView() {
        mTv_title = (TextView) findViewById(R.id.textView_comic_title_comic_info);
        mTv_author = (TextView) findViewById(R.id.textView_author_comic_info);
        mTv_details = (TextView) findViewById(R.id.textView_details_comic_info);
        mImageView = (ImageView) findViewById(R.id.imageView_comic_info);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_comic_info);
        Button btn_mark = (Button) findViewById(R.id.button_mark_comic_info);
        Button btn_read = (Button) findViewById(R.id.button_read_comic_info);
        Button btn_download = (Button) findViewById(R.id.button_download_comic_info);
        Button btn_share = (Button) findViewById(R.id.button_share_comic_info);

        //注册单击事件
        mTv_author.setOnClickListener(this);
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
        comic = comicDBHelper.findByUrl(url);
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<byte[]>() {
                    @Override
                    public void onSuccess(byte[] result) {
                        try {
                            final String content = new String(result, "gb2312");
                            if (comic == null) {
                                comic = new Comic(url, content);
                            } else {
                                comic.checkUpdate(content);
                            }
                            mTv_title.setText(comic.getTitle());
                            mTv_author.setText(comic.getAuthor());
                            mTv_details.setText(comic.getDescription());
                            Glide.with(ComicInfoActivity.this)
                                    .load(comic.getThumbnailUrl())
                                    .placeholder(R.mipmap.blank)
                                    .error(R.mipmap.blank)
                                    .crossFade()
                                    .fitCenter()
                                    .into(mImageView);
                            //初始化RecyclerView
                            mRecyclerView.setLayoutManager(new GridLayoutManager(ComicInfoActivity.this, 4));
                            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            mVolAdapter = new VolRecyclerViewAdapter(ComicInfoActivity.this, comic.getCaptureName());
                            mVolAdapter.setOnItemClickListener(new VolRecyclerViewAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    Intent intent = new Intent(ComicInfoActivity.this, GalleryActivity.class);
                                    intent.putExtra("comic", comic);
                                    intent.putExtra("position", position);
                                    startActivityForResult(intent, 0);
                                }

                                @Override
                                public void onItemLongClick(View view, int position) {

                                }
                            });
                            mRecyclerView.setAdapter(mVolAdapter);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        System.out.println("onError: " + ex.toString());
                        if (BaseUtils.getAPNType(ComicInfoActivity.this) == BaseUtils.NONEWTWORK) {
                            if (comic != null && comic.getCaptureName() != null) {
                                mTv_title.setText(comic.getTitle());
                                mTv_author.setText(comic.getAuthor());
                                mTv_details.setText(comic.getDescription());
                                Glide.with(ComicInfoActivity.this)
                                        .load(comic.getThumbnailUrl())
                                        .placeholder(R.mipmap.blank)
                                        .error(R.mipmap.blank)
                                        .crossFade()
                                        .fitCenter()
                                        .into(mImageView);
                                //初始化RecyclerView
                                mRecyclerView.setLayoutManager(new GridLayoutManager(ComicInfoActivity.this, 4));
                                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                mVolAdapter = new VolRecyclerViewAdapter(ComicInfoActivity.this, comic.getCaptureName());
                                mVolAdapter.setOnItemClickListener(new VolRecyclerViewAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Intent intent = new Intent(ComicInfoActivity.this, GalleryActivity.class);
                                        intent.putExtra("comic", comic);
                                        intent.putExtra("position", position);
                                        startActivityForResult(intent, 0);
                                    }

                                    @Override
                                    public void onItemLongClick(View view, int position) {

                                    }
                                });
                                mRecyclerView.setAdapter(mVolAdapter);
                                mRecyclerView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(ComicInfoActivity.this, "访问网络出错！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        System.out.println("onCancel: " + cex.toString());
                    }

                    @Override
                    public void onFinished() {
                    }
                }

        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        comic = (Comic) data.getSerializableExtra("comic");
        comic.setLastReadTime(System.currentTimeMillis());
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.button_mark_comic_info:
                //收藏
                Comic findComic = comicDBHelper.findByUrl(comic.getComicUrl());
                if (findComic != null) {
                    if (findComic.isMark()) {
                        Toast.makeText(ComicInfoActivity.this, "已经收藏过了哦~", Toast.LENGTH_SHORT).show();
                    } else {
                        comic.setMark(true);
                        comic.setLastReadTime(System.currentTimeMillis());
                        comicDBHelper.update(comic);
                        Toast.makeText(ComicInfoActivity.this, "收藏成功!", Toast.LENGTH_SHORT).show();
                    }
                } else if (comic != null) {
                    comic.setMark(true);
                    comic.setLastReadTime(System.currentTimeMillis());
                    comicDBHelper.add(comic);
                    Toast.makeText(ComicInfoActivity.this, "收藏成功!", Toast.LENGTH_SHORT).show();
                } else if (comic == null) {
                    Toast.makeText(ComicInfoActivity.this, "还没有加载完成，请耐心等待~", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_read_comic_info:
                //阅读
                intent = new Intent(ComicInfoActivity.this, GalleryActivity.class);
                intent.putExtra("comic", comic);
                intent.putExtra("position", comic.getReadCapture());
                startActivityForResult(intent, 0);
                break;
            case R.id.button_download_comic_info:
                break;
            case R.id.button_share_comic_info:
                break;
            case R.id.textView_author_comic_info:
                //搜索同一作者的作品
                intent = new Intent(this, ComicResultListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("action", Constants.ACTION_SEARCH);
                String getKey = null;
                try {
                    getKey = "?key=" + URLEncoder.encode(comic.getAuthor(), "GB2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                getKey += "&button=%CB%D1%CB%F7%C2%FE%BB%AD";
                bundle.putString("url", Constants.SEARCH_URL + getKey);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (comic.getLastReadTime() != 0) {
            comic.setLastReadTime(System.currentTimeMillis());
            if (comicDBHelper.findByUrl(comic.getComicUrl()) != null) {
                comicDBHelper.update(comic);
            } else {
                comicDBHelper.add(comic);
            }
        }
    }
}
