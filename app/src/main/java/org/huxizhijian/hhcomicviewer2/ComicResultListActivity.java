package org.huxizhijian.hhcomicviewer2;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.huxizhijian.hhcomicviewer2.adapter.CommonAdapter;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.utils.ViewHolder;
import org.huxizhijian.hhcomicviewer2.view.LoadPageListView;
import org.huxizhijian.hhcomicviewer2.vo.Comic;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ComicResultListActivity extends Activity {

    private LinearLayout mLoadingLayout;
    private LoadPageListView mListView;
    private ListViewAdapter mAdapter;
    private String mUrl;
    private int mPosition;
    private int mPageSize;
    private boolean isSearch;

    private ArrayList<Comic> mComicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_result_list);
        initView();
        Bundle bundle = getIntent().getExtras();
        String action = bundle.getString("action", Constants.ACTION_CLASSIFIES);
        if (action.equals(Constants.ACTION_SEARCH)) {
            isSearch = true;
        } else if (action.equals(Constants.ACTION_CLASSIFIES)) {
            isSearch = false;
        }
        mUrl = bundle.getString("url", Constants.HHCOMIC_URL + "hhlist/1/");
        showComicList();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void showComicList() {
        RequestParams params = new RequestParams(mUrl);
        x.http().get(params, new Callback.CommonCallback<byte[]>() {
            @Override
            public void onSuccess(byte[] result) {
                try {
                    String content = new String(result, "gb2312");
                    Document doc = Jsoup.parse(content);
                    mComicList = new ArrayList<>();
                    //数据加载
                    if (!isSearch) {
                        //非搜索
                        //page信息查找
                        Element pageInfo = doc.select("div[class=replz]").first();
                        Elements pages = pageInfo.select("a");
                        for (Element page : pages) {
                            if (page.text().equals("末页")) {
                                String pageSize = page.attr("href").split("\\.")[0];
                                mPageSize = Integer.valueOf(pageSize);
                                mPosition = 1;
//                                System.out.println(mPageSize);
                            }
                        }
                        newPageParse(doc);
                    } else {
                        //如果为搜索界面
                        Element comics = doc.select("div[class=dSHtm]").first();
                        Elements comicUrls = comics.select("div");
                        comicUrls.remove(0);
                        Comic comic = null;
                        for (int i = 0; i < comicUrls.size(); i++) {
                            comic = new Comic();
                            Element comicSrc = comicUrls.get(i).select("a").first();
                            comic.setComicUrl(comicSrc.attr("href"));
                            comic.setTitle(comicSrc.text());
                            Element imgUrl = comicUrls.get(i).select("img").first();
                            comic.setThumbnailUrl(imgUrl.attr("src"));
                            Elements desc = comicUrls.get(i).select("br");
                            comic.setDescription(desc.get(2).text());
                            mComicList.add(comic);
                        }
                    }

                    //ListView设置
                    mAdapter = new ListViewAdapter(ComicResultListActivity.this, mComicList, R.layout.item_list_view);
                    if (!isSearch) {
                        mListView.setLoaderListener(new LoadPageListView.ILoaderListener() {
                            @Override
                            public void onLoad() {
                                //加载下一页
                                if (mPosition + 1 <= mPageSize) {
                                    mPosition++;
                                    loadNextPage();
                                } else {
                                    Toast.makeText(ComicResultListActivity.this, "已经到最后了哦~", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    mListView.setDividerHeight(4);
                    mListView.setAdapter(mAdapter);
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Intent intent = new Intent(ComicResultListActivity.this, ComicInfoActivity.class);
                            intent.setAction(ComicInfoActivity.ACTION_SEARCH);
                            intent.putExtra("url", mComicList.get(position).getComicUrl());
                            startActivity(intent);
                        }
                    });

                    //更新界面
                    mLoadingLayout.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("init ", "onError: ", ex);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e("init ", "onCancelled: ", cex);
            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void loadNextPage() {
        RequestParams params = new RequestParams(mUrl + mPosition + ".htm");
        x.http().get(params, new Callback.CommonCallback<byte[]>() {
            @Override
            public void onSuccess(byte[] result) {
                try {
                    String content = new String(result, "gb2312");
                    Document doc = Jsoup.parse(content);
                    newPageParse(doc);
                    mAdapter.setDatas(mComicList);
                    mListView.loadComplete();
                    mAdapter.notifyDataSetChanged();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void newPageParse(Document doc) {
        Element comics = doc.select("div[class=m_list]").first();
        Elements comicUrls = comics.select("h3");
        Elements comicPicUrls = comics.select("img");
        Comic comic;
        for (int i = 0; i < comicUrls.size(); i++) {
            comic = new Comic();
            Element comicSrc = comicUrls.get(i).select("a").first();
            comic.setComicUrl(Constants.HHCOMIC_URL + comicSrc.attr("href"));
            comic.setTitle(comicSrc.attr("title"));
            comic.setThumbnailUrl(comicPicUrls.get(i).attr("src"));
            comic.setDescription(comicPicUrls.get(i).attr("alt").substring(3));
            mComicList.add(comic);
        }
    }

    private void initView() {
        mLoadingLayout = (LinearLayout) findViewById(R.id.loading_layout_search_result);
        mListView = (LoadPageListView) findViewById(R.id.listView_result);
        //对actionBar进行设置
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            BaseUtils.initActionBar(actionBar, Constants.THEME_COLOR);
        }
    }

    //自定义adapter
    class ListViewAdapter extends CommonAdapter<Comic> {

        public ListViewAdapter(Context context, List<Comic> comic, int layoutResId) {
            super(context, comic, layoutResId);
        }

        @Override
        public void convert(ViewHolder vh, Comic comic) {
            vh.setText(R.id.tv_title_item, comic.getTitle());
            vh.setText(R.id.tv_description_item, comic.getDescription());
            ImageView imageView = vh.getView(R.id.imageView_item);
            Glide.with(ComicResultListActivity.this)
                    .load(comic.getThumbnailUrl())
                    .placeholder(R.mipmap.blank)
                    .error(R.mipmap.blank)
                    .into(imageView);
        }
    }
}
