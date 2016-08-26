package org.huxizhijian.hhcomicviewer2;

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

import com.bumptech.glide.Glide;

import org.huxizhijian.hhcomicviewer2.adapter.VolRecyclerViewAdapter;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.vo.Comic;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;

public class ComicInfoActivity extends Activity implements OnClickListener {

    private TextView mTv_author, mTv_title, mTv_details;
    private ImageView mImageView;
    private RecyclerView mRecyclerView;
    private VolRecyclerViewAdapter mVolAdapter;
    private Button mBtn_mark, mBtn_read, mBtn_download, mBtn_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_info);
        BaseUtils.initActionBar(getActionBar(), Constants.THEME_COLOR);
        initView();
        initData();
    }

    private void initView() {
        mTv_title = (TextView) findViewById(R.id.textView_comic_title_comic_info);
        mTv_author = (TextView) findViewById(R.id.textView_author_comic_info);
        mTv_details = (TextView) findViewById(R.id.textView_details_comic_info);
        mImageView = (ImageView) findViewById(R.id.imageView_comic_info);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_comic_info);
        mBtn_mark = (Button) findViewById(R.id.button_mark_comic_info);
        mBtn_read = (Button) findViewById(R.id.button_read_comic_info);
        mBtn_download = (Button) findViewById(R.id.button_download_comic_info);
        mBtn_share = (Button) findViewById(R.id.button_share_comic_info);

        //注册单击事件
        mTv_author.setOnClickListener(this);
        mBtn_mark.setOnClickListener(this);
        mBtn_read.setOnClickListener(this);
        mBtn_download.setOnClickListener(this);
        mBtn_share.setOnClickListener(this);
    }

    private void initData() {
        RequestParams params = new RequestParams(Constants.URL_HHCOMIC + "comic/188436/");
        x.http().get(params, new Callback.CommonCallback<byte[]>() {
                    @Override
                    public void onSuccess(byte[] result) {
                        try {
                            final String content = new String(result, "gb2312");
                            final Comic comic = new Comic(content);
                            mTv_title.setText(comic.getTitle());
                            mTv_author.setText(comic.getAuthor());
                            mTv_details.setText(comic.getDescription());
                            Glide.with(ComicInfoActivity.this)
                                    .load(comic.getThumbnailUrl())
                                    .placeholder(R.mipmap.blank)
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
                                    intent.putExtra("url", Constants.URL_HHCOMIC + comic.getCaptureUrl()[position]);
                                    intent.putExtra("comic", comic);
                                    intent.putExtra("position", position);
                                    startActivity(intent);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_mark_comic_info:
                break;
            case R.id.button_read_comic_info:
                break;
            case R.id.button_download_comic_info:
                break;
            case R.id.button_share_comic_info:
                break;
            case R.id.textView_author_comic_info:
                break;
            default:
                break;
        }
    }
}
