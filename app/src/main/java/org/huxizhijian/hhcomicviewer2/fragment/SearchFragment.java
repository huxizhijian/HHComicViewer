package org.huxizhijian.hhcomicviewer2.fragment;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.activities.ComicResultListActivity;
import org.huxizhijian.hhcomicviewer2.adapter.VolRecyclerViewAdapter;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
            Document doc = Jsoup.parse(Constants.CLASSIFIES_CONTENT);
            Elements classifies = doc.select("a[class=linkb]");
            mClassifies = new String[classifies.size()];
            mLinks = new String[classifies.size()];
            for (int i = 0; i < classifies.size(); i++) {
                mClassifies[i] = classifies.get(i).text();
                mLinks[i] = classifies.get(i).attr("href");
            }
            //加载数据到recycler_view里面
            initRecyclerView();
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
                intent.setAction(Constants.ACTION_CLASSIFIES);
                intent.putExtra("url", Constants.HHCOMIC_URL + mLinks[position]);
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
        mEt_key.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = mEt_key.getText().toString();
                    if (key.equals("")) {
                        Toast.makeText(getActivity(), "搜索内容不能为空", Toast.LENGTH_SHORT).show();
                    }
                    BaseUtils.hideInputMethod(mEt_key, getActivity());
                    //进行搜索操作
                    Intent intent = new Intent(getActivity(), ComicResultListActivity.class);
                    intent.setAction(Intent.ACTION_SEARCH);
                    intent.putExtra(SearchManager.QUERY, key);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
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
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, key);
                startActivity(intent);
                break;
        }
    }
}
