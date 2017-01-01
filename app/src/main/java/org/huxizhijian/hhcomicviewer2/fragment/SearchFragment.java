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

package org.huxizhijian.hhcomicviewer2.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.activities.ComicResultListActivity;
import org.huxizhijian.hhcomicviewer2.adapter.VolRecyclerViewAdapter;
import org.huxizhijian.hhcomicviewer2.utils.BaseUtils;
import org.huxizhijian.hhcomicviewer2.utils.Constants;
import org.huxizhijian.hhcomicviewer2.view.InstantAutoCompleteTextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * 搜索功能，分类展示
 */
public class SearchFragment extends Fragment implements View.OnClickListener {

    //控件即adapter
    private LinearLayout mSearchShowBtn, mSearchContainer, mLoadingLayout;
    private InstantAutoCompleteTextView mAtv_key;
    private ImageButton mSearchBtn;
    private RecyclerView mRecyclerView;
    private VolRecyclerViewAdapter mAdapter;

    //分类和链接数据
    private List<String> mClassifies, mLinks;

    //用于搜索历史保存和读取
    private SharedPreferences mSharedPreferences;

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
        outState.putStringArrayList("classifies", (ArrayList<String>) mClassifies);
        outState.putStringArrayList("links", (ArrayList<String>) mLinks);
        super.onSaveInstanceState(outState);
    }

    private void initData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mClassifies = savedInstanceState.getStringArrayList("classifies");
            mLinks = savedInstanceState.getStringArrayList("links");
        }
        //如果进行了状态保存
        if (mClassifies != null && mLinks != null) {
            //省略了jsoup解析操作
            initRecyclerView();
        } else {
            mLoadingLayout.setVisibility(View.VISIBLE);
            Document doc = Jsoup.parse(Constants.CLASSIFIES_CONTENT);
            Elements classifies = doc.select("span").select("a");
            mClassifies = new ArrayList<>();
            mLinks = new ArrayList<>();
            for (int i = 0; i < classifies.size(); i++) {
                mClassifies.add(classifies.get(i).text());
                mLinks.add(classifies.get(i).attr("href"));
            }
            //加载数据到recycler_view里面
            initRecyclerView();
        }

        //初始化配置类
        mSharedPreferences = getActivity().getSharedPreferences("history", Context.MODE_PRIVATE);
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
                intent.putExtra("classified", mClassifies.get(position));
                intent.putExtra("url", Constants.HHCOMIC_URL + mLinks.get(position));
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
        mAtv_key.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = mAtv_key.getText().toString();
                    if (key.equals("")) {
                        Toast.makeText(getActivity(), "搜索内容不能为空", Toast.LENGTH_SHORT).show();
                    }
                    BaseUtils.hideInputMethod(mAtv_key, getActivity());
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
        mAtv_key = (InstantAutoCompleteTextView) view.findViewById(R.id.actv_search_content);
        mSearchBtn = (ImageButton) view.findViewById(R.id.ib_search_btn);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_classifies_search);
        mLoadingLayout = (LinearLayout) view.findViewById(R.id.loading_layout_search);
        mAtv_key.setSingleLine();
    }

    @Override
    public void onResume() {
        super.onResume();
        //读取配置文件
        if (mSharedPreferences != null) {
            String group = mSharedPreferences.getString("keys", "");
            if (!group.equals("")) {
                //如果有历史记录
                String[] searchHistory = group.split(":@");
                String[] arr = getHistorySearchArrays(searchHistory);
                //将其设置到自动完成框中
                setSearchView(arr);
            }
        }
    }

    private void setSearchView(final String[] arr) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr);
        mAtv_key.setAdapter(arrayAdapter);
        mAtv_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            //没有输入任何东西 则显示默认列表，否则调用接口，展示下拉列表
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 1) {
                    showHotSearchKeywords(arr);
                } else {
                    showHotSearchKeywords(arr);
                }
            }
        });

        mAtv_key.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = arr[position];
                mAtv_key.setText(item);
            }
        });

        //点击搜索框时，如果没有输入任何东西 则显示默认列表
        mAtv_key.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (TextUtils.isEmpty(mAtv_key.getText().toString())) {
                    showHotSearchKeywords(arr);
                }
                return false;
            }
        });
    }

    //这里发现很奇怪的事情， 需要每次new一个ArrayAdapter，要不然有时调用showDropDown不会有效果
    private void showHotSearchKeywords(String[] arr) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, arr);
        mAtv_key.setAdapter(arrayAdapter);
        mAtv_key.showDropDown();
    }

    private String[] getHistorySearchArrays(String[] searchHistory) {
        String[] arr;
        StringBuilder newGroup = null;
        if (searchHistory.length > 5) {
            //最多读取5条历史搜索
            arr = new String[5];
            //用于删除多余的搜索历史记录，只保留5个
            newGroup = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                arr[arr.length - i - 1] = searchHistory[searchHistory.length - i - 1];
                if (i < 4) {
                    newGroup.append(searchHistory[searchHistory.length - 5 + i]).append(":@");
                } else {
                    newGroup.append(searchHistory[searchHistory.length - 5 + i]);
                }
            }
            //更新搜索历史记录
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("keys", newGroup.toString());
            editor.apply();
        } else {
            arr = new String[searchHistory.length];
            for (int i = 0; i < searchHistory.length; i++) {
                arr[arr.length - i - 1] = searchHistory[searchHistory.length - i - 1];
            }
        }
        return arr;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_search_btn_container:
                mSearchShowBtn.setVisibility(View.GONE);
                mSearchContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.ib_search_btn:
                //获取搜索框内容
                String key = mAtv_key.getText().toString();
                if (key.equals("")) {
                    Toast.makeText(getActivity(), "搜索内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                //收起输入法
                BaseUtils.hideInputMethod(mAtv_key, getActivity());

                //进行搜索操作
                Intent intent = new Intent(getActivity(), ComicResultListActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putExtra(SearchManager.QUERY, key);
                startActivity(intent);
                break;
        }
    }
}
