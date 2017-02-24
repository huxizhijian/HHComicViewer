package org.huxizhijian.hhcomicviewer2.ui.recommend;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.adapter.RecommendDetailsAdapter;
import org.huxizhijian.hhcomicviewer2.adapter.entity.ComicTabList;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecommendDetailsFragment extends Fragment {

    public RecommendDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recommend_details, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        Bundle bundle = getArguments();
        ComicTabList comicTabList = (ComicTabList) bundle.getSerializable("tab_list");
        if (comicTabList != null) {
            RecommendDetailsAdapter adapter = new RecommendDetailsAdapter(getActivity(), comicTabList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

}
