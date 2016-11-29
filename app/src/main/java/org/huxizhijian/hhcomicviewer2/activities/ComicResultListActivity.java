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

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.CommonAdapter;
import org.huxizhijian.hhcomicviewer2.app.HHApplication;
import org.huxizhijian.hhcomicviewer2.enities.Comic;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.utils.ViewHolder;
import org.huxizhijian.hhcomicviewer2.view.LoadPageListView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ComicResultListActivity extends AppCompatActivity {

    //控件及适配器
    private LinearLayout mLoadingLayout;
    private LoadPageListView mListView;
    private ListViewAdapter mAdapter;

    //数据
    private String mUrl; //url
    private int mPosition; //非搜索操作时，当前页
    private int mPageSize; //非搜索操作时，最大页数
    private boolean mIsSearch; //是否为搜索操作

    private ArrayList<Comic> mComicList; //解析出来的comic数据，不能直接保存到数据库

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_result_list);
        initView();
        Intent intent = getIntent();
        doAction(intent);

    }

    private void doAction(Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            mIsSearch = true;
            String key = intent.getStringExtra(SearchManager.QUERY);
            setTitle("搜索结果 - " + "\"" + key + "\"");
            String getKey = null;
            try {
                getKey = "?key=" + URLEncoder.encode(key, "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            getKey += "&button=%CB%D1%CB%F7%C2%FE%BB%AD";
            mUrl = Constants.SEARCH_URL + getKey;
        } else if (intent.getAction().equals(Constants.ACTION_CLASSIFIES)) {
            mIsSearch = false;
            String classified = intent.getStringExtra("classified");
            setTitle("分类 - " + classified);
            mUrl = intent.getStringExtra("url");
            mUrl = mUrl.substring(0, mUrl.length() - 5); //去掉网址后面的.html
        }
        showComicList();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        doAction(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showComicList() {
        Request request = new Request.Builder().url(mUrl + "/" + 1 + ".html").build();
        OkHttpClient client = ((HHApplication) getApplication()).getClient();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("init ", "onError: ", e);
                if (BaseUtils.getAPNType(ComicResultListActivity.this) == BaseUtils.NONEWTWORK) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ComicResultListActivity.this,
                                    Constants.NO_NETWORK, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    byte[] result = response.body().bytes();
                    mComicList = new ArrayList<>();
                    //数据加载
                    if (!mIsSearch) {
                        //非搜索
                        String content = new String(result, "utf-8");
                        Document doc = Jsoup.parse(content);
                        //page信息查找
                        Element pageInfo = doc.select("div[class=cComicPageChange]").first();
                        Elements pages = pageInfo.select("a");
                        for (Element page : pages) {
                            if (page.text().equals("尾页")) {
                                String pageSize = page.attr("href").split("\\.")[0].split("/")[3];
                                mPageSize = Integer.valueOf(pageSize);
                                mPosition = 1;
//                                System.out.println(mPageSize);
                            }
                        }
                        newPageParse(doc);
                    } else {
                        //如果为搜索界面
                        String content = new String(result, "gb2312");
                        Document doc = Jsoup.parse(content);
                        Element comics = doc.select("div[class=dSHtm]").first();
                        Elements comicUrls = comics.select("div");
                        comicUrls.remove(0);
                        Comic comic = null;
                        for (int i = 0; i < comicUrls.size(); i++) {
                            comic = new Comic();
                            Element comicSrc = comicUrls.get(i).select("a").first();
                            String url = comicSrc.attr("href");
                            comic.setComicUrl(url);
                            comic.setTitle(comicSrc.text());
                            Element imgUrl = comicUrls.get(i).select("img").first();
                            comic.setThumbnailUrl(imgUrl.attr("src"));
                            Elements desc = comicUrls.get(i).getElementsByTag("br");
                            comic.setDescription(desc.get(2).text());
                            mComicList.add(comic);
                        }
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
    }

    private void updateViews() {
        //ListView设置
        mAdapter = new ListViewAdapter(ComicResultListActivity.this, mComicList, R.layout.item_list_view);
        if (!mIsSearch) {
            mListView.addFootView(true);
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
        } else {
            mListView.addFootView(false);
        }
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(ComicResultListActivity.this, ComicDetailsActivity.class);
                intent.putExtra("url", mComicList.get(position).getComicUrl());
                intent.putExtra("thumbnailUrl", mComicList.get(position).getThumbnailUrl());
                intent.putExtra("title", mComicList.get(position).getTitle());

                ImageView sharedView = (ImageView) view.findViewById(R.id.imageView_item);

                if (sharedView.getDrawable() != null) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        //如果是android5.0及以上，开启shareElement动画
                        String transitionName = getString(R.string.image_transition_name);

                        ActivityOptions transitionActivityOptions = ActivityOptions
                                .makeSceneTransitionAnimation(ComicResultListActivity.this, sharedView, transitionName);
                        startActivity(intent, transitionActivityOptions.toBundle());
                    } else {
                        startActivity(intent);
                    }
                } else {
                    startActivity(intent);
                }
            }
        });

        //更新界面
        mLoadingLayout.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
    }

    private void loadNextPage() {
        Request request = new Request.Builder().url(mUrl + "/" + mPosition + ".html").build();
        OkHttpClient client = ((HHApplication) getApplication()).getClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (BaseUtils.getAPNType(ComicResultListActivity.this) == BaseUtils.NONEWTWORK) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ComicResultListActivity.this,
                                    Constants.NO_NETWORK, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] result = response.body().bytes();
                String content = new String(result, "utf-8");
                Document doc = Jsoup.parse(content);
                newPageParse(doc);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setDatas(mComicList);
                        mListView.loadComplete();
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void newPageParse(Document doc) {
        Element comics = doc.select("div[class=cComicList]").first();
        Elements comicUrls = comics.select("a");
        Elements comicPicUrls = comics.select("img");
        Comic comic;
        for (int i = 0; i < comicUrls.size(); i++) {
            comic = new Comic();
            Element comicSrc = comicUrls.get(i);
            comic.setComicUrl(Constants.HHCOMIC_URL + comicSrc.attr("href"));
            comic.setTitle(comicSrc.attr("title"));
            comic.setThumbnailUrl(comicPicUrls.get(i).select("img").first().attr("src"));
            mComicList.add(comic);
        }
    }

    private void initView() {
        mLoadingLayout = (LinearLayout) findViewById(R.id.loading_layout_search_result);
        mListView = (LoadPageListView) findViewById(R.id.listView_result);

        //toolbar的设置
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_black_24dp);
        //将其当成actionbar
        setSupportActionBar(toolbar);
        BaseUtils.setStatusBarTint(this, getResources().getColor(R.color.colorPrimaryDark));
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
                    .fitCenter()
                    .placeholder(R.mipmap.blank)
                    .error(R.mipmap.blank)
                    .crossFade()
                    .into(imageView);
        }
    }
}
