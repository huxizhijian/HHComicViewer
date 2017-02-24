package org.huxizhijian.hhcomicviewer2.ui.recommend;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.ClassifiesAdapter;
import org.huxizhijian.hhcomicviewer2.adapter.RankAdapter;
import org.huxizhijian.hhcomicviewer2.adapter.entity.ClassifiesEntity;
import org.huxizhijian.hhcomicviewer2.adapter.entity.RankTitleEntity;
import org.huxizhijian.hhcomicviewer2.ui.common.RefreshBaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankAndClassifiesFragment extends RefreshBaseFragment {

    public final static int MODE_RANK = 0x0;
    public final static int MODE_CLASSIFIES = 0x1;

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private int mode = MODE_RANK;

    public RankAndClassifiesFragment() {
    }

    public static RankAndClassifiesFragment newInstance(int mode) {
        RankAndClassifiesFragment fragment = new RankAndClassifiesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("mode", mode);
        fragment.setArguments(bundle);
        return fragment;
    }

    public int getMode() {
        return mode;
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank_and_classifies, container, false);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.pink_500,
                R.color.purple_500, R.color.blue_500);
        if (getArguments() != null) {
            mode = getArguments().getInt("mode", MODE_RANK);
        }
        return view;
    }

    @Override
    public void initData() {
        mRefreshLayout.setRefreshing(true);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mode == MODE_CLASSIFIES) {
                    initClassifiesData();
                } else if (mode == MODE_RANK) {
                    initRankData();
                }
                mRefreshLayout.setRefreshing(false);
            }
        });
        if (mode == MODE_CLASSIFIES) {
            initClassifiesData();
        } else if (mode == MODE_RANK) {
            initRankData();
        }
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void refreshData() {

    }

    private void initRankData() {
        List<RankTitleEntity> rankTitleEntities = new ArrayList<>();
        rankTitleEntities.add(new RankTitleEntity(R.drawable.ic_new_releases_black_24dp,
                "最近刷新的漫画 TOP100", "/top/newrating.aspx"));
        rankTitleEntities.add(new RankTitleEntity(R.drawable.ic_whatshot_black_24dp,
                "最多人看的漫画 TOP100", "/top/hotrating.aspx"));
        rankTitleEntities.add(new RankTitleEntity(R.drawable.ic_thumb_up_black_24dp,
                "评分最高的漫画 TOP100", "/top/toprating.aspx"));
        rankTitleEntities.add(new RankTitleEntity(R.drawable.ic_textsms_black_24dp,
                "最多人评论的画 TOP100", "/top/hoorating.aspx"));
        rankTitleEntities.add(new RankTitleEntity(R.drawable.ic_stars_black_24dp,
                "汗妹推荐的漫画", "/comic/best_1.html"));
        rankTitleEntities.add(new RankTitleEntity(R.drawable.ic_view_module_black_24dp,
                "漫画目录", "/comic/"));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RankAdapter adapter = new RankAdapter(getActivity(), rankTitleEntities);
        mRecyclerView.setAdapter(adapter);
    }

    private void initClassifiesData() {
        List<ClassifiesEntity> classifiesEntities = new ArrayList<>();
        classifiesEntities.add(getClassifiedEntity("萌系", 1, "http://pic.huo80.com/comicui/21301.JPG"));
        classifiesEntities.add(getClassifiedEntity("搞笑", 2, "http://pic.huo80.com/comicui/28545.JPG"));
        classifiesEntities.add(getClassifiedEntity("格斗", 3, "http://pic.huo80.com/comicui/28906.JPG"));
        classifiesEntities.add(getClassifiedEntity("科幻", 4, "http://pic.huo80.com/comicui/30298.JPG"));
        classifiesEntities.add(getClassifiedEntity("剧情", 5, "http://pic.huo80.com/comicui/30589.JPG"));
        classifiesEntities.add(getClassifiedEntity("侦探", 6, "http://pic.huo80.com/upload/up200802/a104.jpg"));
        classifiesEntities.add(getClassifiedEntity("竞技", 7, "http://pic.huo80.com/upload/up200806/a198.jpg"));
        classifiesEntities.add(getClassifiedEntity("魔法", 8, "http://pic.huo80.com/upload/up200912/a130.jpg"));
        classifiesEntities.add(getClassifiedEntity("神鬼", 9, "http://pic.huo80.com/upload/up201003/a164.jpg"));
        classifiesEntities.add(getClassifiedEntity("校园", 10, "http://pic.huo80.com/comicui2/7688a.JPG"));
        classifiesEntities.add(getClassifiedEntity("惊栗", 11, "http://pic.huo80.com/comicui/7556.JPG"));
        classifiesEntities.add(getClassifiedEntity("厨艺", 12, "http://pic.huo80.com/comicui/27782.JPG"));
        classifiesEntities.add(getClassifiedEntity("伪娘", 13, "http://img.99mh.com/comicui/23584.JPG"));
        classifiesEntities.add(getClassifiedEntity("冒险", 15, "http://pic.huo80.com/comicui/20512.JPG"));
        classifiesEntities.add(getClassifiedEntity("小说", 19, "http://pic.huo80.com/comicui/31149.JPG"));
        classifiesEntities.add(getClassifiedEntity("港漫", 20, "http://pic.huo80.com/upload/up200804/a195.jpg"));
        classifiesEntities.add(getClassifiedEntity("耽美", 21, "http://pic.huo80.com/comicui/11616.JPG"));
        classifiesEntities.add(getClassifiedEntity("经典", 22, "http://img.99mh.com/upload/up200801/x372.jpg"));
        classifiesEntities.add(getClassifiedEntity("欧美", 23, "http://pic.huo80.com/comicui/30267.JPG"));
        classifiesEntities.add(getClassifiedEntity("日文", 24, "http://pic.huo80.com/comicui/24318.JPG"));
        classifiesEntities.add(getClassifiedEntity("亲情", 25, "http://pic.huo80.com/comicui/29176.JPG"));
        ClassifiesAdapter adapter = new ClassifiesAdapter(getActivity(), classifiesEntities);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setAdapter(adapter);
    }

    private ClassifiesEntity getClassifiedEntity(String name, int page, String picUrl) {
        return new ClassifiesEntity(name, "/comic/class_" + page + "/", picUrl);
    }

    @Override
    public void onInVisible() {

    }
}
