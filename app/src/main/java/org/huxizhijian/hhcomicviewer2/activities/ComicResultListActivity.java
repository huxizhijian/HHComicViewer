package org.huxizhijian.hhcomicviewer2.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
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

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.CommonAdapter;
import org.huxizhijian.hhcomicviewer2.enities.Comic;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.utils.ViewHolder;
import org.huxizhijian.hhcomicviewer2.view.LoadPageListView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ComicResultListActivity extends Activity {

    private LinearLayout mLoadingLayout;
    private LoadPageListView mListView;
    private ListViewAdapter mAdapter;
    private String mUrl;
    private int mPosition;
    private int mPageSize;
    private boolean mIsSearch;

    private ArrayList<Comic> mComicList;

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
        }
        showComicList();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        doAction(intent);
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
                    if (!mIsSearch) {
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
                            Elements desc = comicUrls.get(i).getElementsByTag("br");
                            comic.setDescription(desc.get(2).text());
                            mComicList.add(comic);
                        }
                    }

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
                if (BaseUtils.getAPNType(ComicResultListActivity.this) == BaseUtils.NONEWTWORK) {
                    Toast.makeText(ComicResultListActivity.this, Constants.NO_NETWORK, Toast.LENGTH_SHORT).show();
                }
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
                if (BaseUtils.getAPNType(ComicResultListActivity.this) == BaseUtils.NONEWTWORK) {
                    Toast.makeText(ComicResultListActivity.this, Constants.NO_NETWORK, Toast.LENGTH_SHORT).show();
                }
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
                    .fitCenter()
                    .placeholder(R.mipmap.blank)
                    .error(R.mipmap.blank)
                    .crossFade()
                    .into(imageView);
        }
    }
}
