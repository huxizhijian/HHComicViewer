package org.huxizhijian.hhcomicviewer2.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.activities.ComicInfoActivity;
import org.huxizhijian.hhcomicviewer2.adapter.VolDownloadSelectorAdapter;
import org.huxizhijian.hhcomicviewer2.enities.Comic;

import java.util.List;

/**
 * 下载内容选择Fragment
 */
public class DownloadSelectorFragment extends Fragment implements View.OnClickListener {

    private SelectorDataBinder mDataBinder;
    private RecyclerView mRecyclerView;
    private VolDownloadSelectorAdapter mAdapter;
    private List<String> mDownloadedComicCaptures;
    private Comic mComic;

    public DownloadSelectorFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mDataBinder = (ComicInfoActivity) context;
        mComic = mDataBinder.getComic();
        mDownloadedComicCaptures = mDataBinder.getDownloadedComicCaptures();
    }

    public void initData() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new VolDownloadSelectorAdapter(getActivity(),
                mComic.getCaptureName(), mDownloadedComicCaptures);
        mAdapter.setOnItemClickListener(new VolDownloadSelectorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mAdapter.captureClick(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download_selector, container, false);
        Button startDownload = (Button) view.findViewById(R.id.button_start_download_ds_fragment);
        Button select_all = (Button) view.findViewById(R.id.button_select_all_ds_fragment);
        ImageView iv_cancel = (ImageView) view.findViewById(R.id.image_cancel_ds_fragment);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_download_selector);

        //绑定事件
        startDownload.setOnClickListener(this);
        select_all.setOnClickListener(this);
        iv_cancel.setOnClickListener(this);
        initData();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start_download_ds_fragment:
                //开始下载
                if (mAdapter.getSelectedCaptureNames().size() == 0) {
                    Toast.makeText(getActivity(), "没有选择下载章节", Toast.LENGTH_SHORT).show();
                } else {
                    //将下载章节列表传送到Activity中
                    mDataBinder.sendSelectedCaptures(mAdapter.getSelectedCaptureNames());
                    //关闭
                    mDataBinder.hideFragment();
                }
                break;
            case R.id.button_select_all_ds_fragment:
                //全选
                mAdapter.allSelect();
                break;
            case R.id.image_cancel_ds_fragment:
                //关闭
                mDataBinder.hideFragment();
                break;
        }
    }

    public interface SelectorDataBinder {
        Comic getComic();

        List<String> getDownloadedComicCaptures();

        void sendSelectedCaptures(List<String> selectedCaptures);

        void hideFragment();
    }
}
