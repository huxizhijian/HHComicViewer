/*
 * Copyright 2016-2017 huxizhijian
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

package org.huxizhijian.hhcomicviewer2.ui.search;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.InputType;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.CommonAdapter;
import org.huxizhijian.hhcomicviewer2.adapter.StaggeredComicAdapter;
import org.huxizhijian.hhcomicviewer2.databinding.ActivitySearchBinding;
import org.huxizhijian.hhcomicviewer2.model.Comic;
import org.huxizhijian.hhcomicviewer2.persenter.ISearchPresenter;
import org.huxizhijian.hhcomicviewer2.persenter.implpersenter.SearchPresenterImpl;
import org.huxizhijian.hhcomicviewer2.persenter.viewinterface.ISearchActivity;
import org.huxizhijian.hhcomicviewer2.utils.CommonUtils;
import org.huxizhijian.hhcomicviewer2.utils.ViewHolder;
import org.huxizhijian.sdk.util.ImeUtils;
import org.huxizhijian.sdk.util.TransitionLeakFixUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * in this activity, you can search comic.
 * <p>
 * _______________#########_______________________
 * ______________############_____________________
 * ______________#############____________________
 * _____________##__###########___________________
 * ____________###__######_#####__________________
 * ____________###_#######___####_________________
 * ___________###__##########_####________________
 * __________####__###########_####_______________
 * ________#####___###########__#####_____________
 * _______######___###_########___#####___________
 * _______#####___###___########___######_________
 * ______######___###__###########___######_______
 * _____######___####_##############__######______
 * ____#######__#####################_#######_____
 * ____#######__##############################____
 * ___#######__######_#################_#######___
 * ___#######__######_######_#########___######___
 * ___#######____##__######___######_____######___
 * ___#######________######____#####_____#####____
 * ____######________#####_____#####_____####_____
 * _____#####________####______#####_____###______
 * ______#####______;###________###______#________
 * ________##_______####________####______________
 */

public class SearchActivity extends AppCompatActivity implements ISearchActivity {

    //databinding
    private ActivitySearchBinding mBinding;
    private ISearchPresenter mSearchPresenter = new SearchPresenterImpl(this);

    //resultAdapter
    private StaggeredComicAdapter mAdapter;

    //搜索记录
    private SharedPreferences mSharedPreferences;
    private String mQuery;
    private List<String> mSearchHistory;

    private View mNoResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        CommonUtils.setStatusBarTint(this, getResources().getColor(R.color.colorPrimaryDark));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupEnterAnimations();
            setupExitAnimations();
        }
        setupSearchView();
        setupSearchHistory();
        onNewIntent(getIntent());
    }

    private void setupSearchHistory() {
        if (mSharedPreferences == null) {
            mSharedPreferences = getSharedPreferences("history", Context.MODE_PRIVATE);
        }
        String group = mSharedPreferences.getString("keys", "");
        if (!TextUtils.isEmpty(group)) {
            //如果有历史记录
            String[] history = group.split(":@");
            mSearchHistory = new ArrayList<>();
            for (int i = history.length - 1; i >= 0; i--) {
                mSearchHistory.add(history[i]);
            }
            if (mBinding.searchHistory.getFooterViewsCount() == 0 && mSearchHistory.size() != 0) {
                final View footerView = LayoutInflater.from(this).inflate(R.layout.item_search_history_footview,
                        mBinding.searchHistory, false);
                footerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString("keys", "");
                        editor.apply();
                        Toast.makeText(SearchActivity.this, R.string.clear_successful, Toast.LENGTH_SHORT).show();
                        mBinding.searchHistory.setVisibility(View.GONE);
                    }
                });
                mBinding.searchHistory.addFooterView(footerView);
            }
            mBinding.searchHistory.setAdapter(new CommonAdapter<String>(this, mSearchHistory,
                    R.layout.item_search_history) {
                @Override
                public void convert(ViewHolder vh, String s) {
                    final TextView tv = vh.getView(R.id.tv_search_history);
                    tv.setText(s);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String query = (String) tv.getText();
                            if (!TextUtils.isEmpty(query)) {
                                mBinding.searchView.setQuery(query, false);
                                searchFor(query);
                            }
                        }
                    });
                    vh.getView(R.id.btn_search_text_set).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBinding.searchView.setQuery(tv.getText(), false);
                        }
                    });
                }
            });
            mBinding.searchHistory.setDividerHeight(0);
            mBinding.searchHistory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra(SearchManager.QUERY)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (!TextUtils.isEmpty(query)) {
                mBinding.searchView.setQuery(query, false);
                searchFor(query);
                mBinding.searchView.clearFocus();
            }
        } else {
            //获取焦点，打开ime
            mBinding.searchView.requestFocus();
            ImeUtils.showIme(mBinding.searchView);
        }
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        mBinding.searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // hint, inputType & ime options seem to be ignored from XML! Set in code
        mBinding.searchView.setQueryHint(getString(R.string.search_hint));
        mBinding.searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        mBinding.searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH |
                EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        //设置搜索数据监听接口
        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                clearResults();
                searchFor(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (TextUtils.isEmpty(query)) {
                    clearResults();
                }
                return true;
            }
        });

        //回退事件
        mBinding.ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
            }
        });
    }

    //main method for search action
    void searchFor(String query) {
        mBinding.searchHistory.setVisibility(View.GONE);
        mBinding.searchResults.setVisibility(View.GONE);
        mSearchPresenter.getResult(query);
        mBinding.searchingProgress.setVisibility(View.VISIBLE);
        mQuery = query;
        if (mBinding.searchView.hasFocus()) {
            mBinding.searchView.clearFocus();
        }
    }

    //clear for start a new search
    void clearResults() {
        if (mSearchPresenter.isSearching()) {
            mSearchPresenter.cancel(mQuery);
        }
        mBinding.searchResults.setVisibility(View.GONE);
        mBinding.searchingProgress.setVisibility(View.GONE);
        setupSearchHistory();
        if (mBinding.stubNoSearchResults.isInflated() && mNoResults.getVisibility() == View.VISIBLE) {
            mNoResults.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSuccess(List<Comic> comics) {
        if (comics == null || comics.size() == 0) {
            onException(new Throwable());
            return;
        }
        if (mAdapter == null) {
            mAdapter = new StaggeredComicAdapter(this, null, comics);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBinding.searchingProgress.setVisibility(View.GONE);
                    mBinding.searchResults.setLayoutManager(new StaggeredGridLayoutManager(3,
                            StaggeredGridLayoutManager.VERTICAL));
                    mBinding.searchResults.setItemAnimator(new DefaultItemAnimator());
                    mBinding.searchResults.setAdapter(mAdapter);
                    mBinding.searchResults.setVisibility(View.VISIBLE);
                }
            });
        } else {
            mAdapter.updateComicList(comics);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBinding.searchingProgress.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                    mBinding.searchResults.setVisibility(View.VISIBLE);
                }
            });
        }
        saveSearchHistory();
    }

    @Override
    public void onException(Throwable e) {
        e.printStackTrace();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showNoResults();
            }
        });
        saveSearchHistory();
    }

    @Override
    public void onFailure(int errorCode, String errorMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showNoResults();
            }
        });
        saveSearchHistory();
    }

    private void showNoResults() {
        if (mBinding.stubNoSearchResults.isInflated()) {
            mNoResults.setVisibility(View.VISIBLE);
        } else {
            mNoResults = mBinding.stubNoSearchResults.getViewStub().inflate();
            //绑定重试方法
            View view = mNoResults.findViewById(R.id.btn_retry);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            clearResults();
                            searchFor(mQuery);
                        }
                    });
                }
            });
            mNoResults.setVisibility(View.VISIBLE);
        }
        mBinding.searchingProgress.setVisibility(View.GONE);
    }

    private void saveSearchHistory() {
        //保存搜索记录
        String group = mSharedPreferences.getString("keys", "");
        String newGroup = null;
        if (group.equals("")) {
            newGroup = mQuery;
        } else {
            newGroup = group + ":@" + mQuery;
        }
        if (mSearchHistory == null || !mSearchHistory.contains(mQuery)) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("keys", newGroup);
            editor.apply();
        }
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
                animateRevealShow(mBinding.searchToolbar);//toolbar的缩放动画
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除回调防止内存泄露
        if (mSearchPresenter != null)
            mSearchPresenter.removeListener();
        //防止transition内存泄露
        TransitionLeakFixUtil.removeActivityFromTransitionManager(this);
    }
}
