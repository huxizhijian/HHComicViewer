package org.huxizhijian.hhcomicviewer2.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer2.ComicResultListActivity;
import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.VolRecyclerViewAdapter;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * A simple {@link Fragment} subclass.
 * 搜索功能，分类展示
 */
public class SearchFragment extends Fragment implements View.OnClickListener {

    private LinearLayout mSearchShowBtn, mSearchContainer, mLoadingLayout;
    private EditText mEt_key;
    private ImageButton mSearchBtn;
    private RecyclerView mRecyclerView;
    private VolRecyclerViewAdapter mAdapter;
    private String[] mClassifies, mLinks;

    public SearchFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initView(view);
        initEvent();
        initData(savedInstanceState);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArray("classifies", mClassifies);
        outState.putStringArray("links", mLinks);
        super.onSaveInstanceState(outState);
    }

    private void initData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mClassifies = savedInstanceState.getStringArray("classifies");
            mLinks = savedInstanceState.getStringArray("links");
        }
        if (mClassifies != null && mLinks != null) {
//            System.out.println("restore");
            initRecyclerView();
        } else {
            mLoadingLayout.setVisibility(View.VISIBLE);
            RequestParams params = new RequestParams(Constants.HHCOMIC_URL);
            x.http().get(params, new Callback.CommonCallback<byte[]>() {
                @Override
                public void onSuccess(byte[] result) {
                    try {
                        String content = new String(result, "gb2312");
                        Document doc = Jsoup.parse(content);
                        Element menu = doc.select("div[id=menu]").first();
                        Elements classifies = menu.select("a[class=linkb]");
                        mClassifies = new String[classifies.size()];
                        mLinks = new String[classifies.size()];
                        for (int i = 0; i < classifies.size(); i++) {
                            mClassifies[i] = classifies.get(i).text();
                            mLinks[i] = classifies.get(i).attr("href");
                        }
                        //加载数据到recycler_view里面
                        initRecyclerView();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.e("getWebContent", "onError: " + ex.toString());
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    Log.e("getWebContent", "onCancelled: " + cex.toString());
                }

                @Override
                public void onFinished() {

                }
            });
        }
    }

    //将数据加载到recycler_view中
    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mAdapter = new VolRecyclerViewAdapter(getActivity(), mClassifies);
        mAdapter.setOnItemClickListener(new VolRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //开启分类
                Intent intent = new Intent(getActivity(), ComicResultListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("action", Constants.ACTION_CLASSIFIES);
                bundle.putString("url", Constants.HHCOMIC_URL + mLinks[position]);
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mLoadingLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void initEvent() {
        mSearchShowBtn.setOnClickListener(this);
        mSearchBtn.setOnClickListener(this);
    }

    private void initView(View view) {
        mSearchShowBtn = (LinearLayout) view.findViewById(R.id.ll_search_btn_container);
        mSearchContainer = (LinearLayout) view.findViewById(R.id.ll_search_container);
        mEt_key = (EditText) view.findViewById(R.id.et_search_content);
        mSearchBtn = (ImageButton) view.findViewById(R.id.ib_search_btn);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_classifies_search);
        mLoadingLayout = (LinearLayout) view.findViewById(R.id.loading_layout_search);
        mEt_key.setSingleLine();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_search_btn_container:
                mSearchShowBtn.setVisibility(View.GONE);
                mSearchContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.ib_search_btn:
                String key = mEt_key.getText().toString();
                if (key.equals("")) {
                    Toast.makeText(getActivity(), "搜索内容不能为空", Toast.LENGTH_SHORT).show();
                }
                BaseUtils.hideInputMethod(mEt_key, getActivity());
                //进行搜索操作
                Intent intent = new Intent(getActivity(), ComicResultListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("action", Constants.ACTION_SEARCH);
                String getKey = null;
                try {
                    getKey = "?key=" + URLEncoder.encode(key, "GB2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                getKey += "&button=%CB%D1%CB%F7%C2%FE%BB%AD";
                bundle.putString("url", Constants.SEARCH_URL + getKey);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
