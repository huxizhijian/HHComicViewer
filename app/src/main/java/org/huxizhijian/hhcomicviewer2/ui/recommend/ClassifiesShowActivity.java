package org.huxizhijian.hhcomicviewer2.ui.recommend;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.StaggeredComicAdapter;
import org.huxizhijian.hhcomicviewer2.databinding.ActivityClassifiesShowBinding;
import org.huxizhijian.hhcomicviewer2.model.Comic;
import org.huxizhijian.hhcomicviewer2.persenter.IClassifiesShowPresenter;
import org.huxizhijian.hhcomicviewer2.persenter.implpersenter.ClassifiesShowPresenterImpl;
import org.huxizhijian.hhcomicviewer2.persenter.viewinterface.IClassifiesShowActivity;
import org.huxizhijian.hhcomicviewer2.utils.CommonUtils;
import org.huxizhijian.hhcomicviewer2.view.MyStaggerLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class ClassifiesShowActivity extends AppCompatActivity implements IClassifiesShowActivity {

    private ActivityClassifiesShowBinding mBinding;

    private String mClassifiesName;
    private String mUrl;
    private int mMaxPage;
    private int mPage = 1;
    private int mFirstPage = 1;

    private IClassifiesShowPresenter mPresenter = new ClassifiesShowPresenterImpl(this);
    private List<Comic> mComicList = new ArrayList<>();
    private StaggeredComicAdapter mAdapter;

    private View mNoResult;

    private View mJumpDialogView;
    private EditText mEditText;
    private SimpleDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_classifies_show);
        initUrl();
        setToolBar();
        initFAB();
        initRecyclerView();
    }

    private void initFAB() {
        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mComicList.size() == 0) return;
                if (mJumpDialogView == null) {
                    mJumpDialogView = LayoutInflater.from(ClassifiesShowActivity.this)
                            .inflate(R.layout.dialog_jump_page, null, false);
                    mEditText = (EditText) mJumpDialogView.findViewById(R.id.editText);
                }
                if (mDialog == null) {
                    mDialog = new SimpleDialog(ClassifiesShowActivity.this);
                    mDialog.title("跳转")
                            .positiveAction("确定")
                            .negativeAction("取消")
                            .contentView(mJumpDialogView)
                            .cancelable(true)
                            .negativeActionClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDialog.dismiss();
                                }
                            })
                            .positiveActionClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String pageStr = String.valueOf(mEditText.getText());
                                    if (TextUtils.isEmpty(pageStr)) {
                                        Toast.makeText(ClassifiesShowActivity.this, "输入为空！",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    int page = Integer.parseInt(pageStr);
                                    mAdapter = null;
                                    mComicList = new ArrayList<>();
                                    mFirstPage = page;
                                    mPage = mFirstPage;
                                    mBinding.progressBar.setVisibility(View.VISIBLE);
                                    mBinding.recyclerView.setVisibility(View.GONE);
                                    mPresenter.getComicList(mUrl, mFirstPage);
                                    mDialog.dismiss();
                                }
                            });
                }
                mEditText.setText("");
                mEditText.clearFocus();
                mEditText.setHint("第" + mPage + "页，共" + mMaxPage + "页");
                mDialog.show();
            }
        });
    }

    private void setToolBar() {
        //将其当成actionbar
        setSupportActionBar(mBinding.toolbar);
        CommonUtils.setStatusBarTint(this, getResources().getColor(R.color.colorPrimaryDark));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("分类 - " + mClassifiesName);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.finishAfterTransition();
                } else {
                    this.finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initUrl() {
        mClassifiesName = getIntent().getStringExtra("classifies_name");
        mUrl = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(mUrl)) {
            Log.e("ClassifiesShow", "error, url no found in intent.");
        }
    }

    private void initRecyclerView() {
        //首先加载第一页
        mPresenter.getComicList(mUrl, mFirstPage);
    }

    @Override
    public void onSuccess(final int maxPage, List<Comic> comics) {
        mMaxPage = maxPage;
        mComicList.addAll(comics);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAdapter == null) {
                    MyStaggerLayoutManager layoutManager = new MyStaggerLayoutManager(3,
                            StaggeredGridLayoutManager.VERTICAL);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    mBinding.recyclerView.setLayoutManager(layoutManager);
                    mBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
                    mBinding.recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
                    mBinding.recyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
                    mBinding.recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
                        @Override
                        public void onRefresh() {
                        }

                        @Override
                        public void onLoadMore() {
                            if (mPage < mMaxPage) {
                                mPage++;
                                mPresenter.getComicList(mUrl, mPage);
                            }
                        }
                    });
                    mBinding.recyclerView.setPullRefreshEnabled(false);
                    mAdapter = new StaggeredComicAdapter(ClassifiesShowActivity.this, null, mComicList);
                    mBinding.recyclerView.setAdapter(mAdapter);
                    mBinding.recyclerView.setVisibility(View.VISIBLE);
                    mBinding.progressBar.setVisibility(View.GONE);
                } else {
                    mAdapter.updateComicList(mComicList);
                    if (mMaxPage == mPage) {
                        mBinding.recyclerView.setNoMore(true);
                    } else {
                        mBinding.recyclerView.loadMoreComplete();
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onException(Throwable e) {
        e.printStackTrace();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBinding.recyclerView.setVisibility(View.GONE);
                mBinding.progressBar.setVisibility(View.GONE);
                if (!mBinding.stubNoResults.isInflated()) {
                    mNoResult = mBinding.stubNoResults.getViewStub().inflate();
                }
                mNoResult.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onFailure(int errorCode, String errorMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBinding.recyclerView.setVisibility(View.GONE);
                mBinding.progressBar.setVisibility(View.GONE);
                if (!mBinding.stubNoResults.isInflated()) {
                    mNoResult = mBinding.stubNoResults.getViewStub().inflate();
                }
                mNoResult.setVisibility(View.VISIBLE);
            }
        });
    }
}
