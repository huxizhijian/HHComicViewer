package org.huxizhijian.hhcomicviewer2.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.huxizhijian.hhcomicviewer2.R;


public class ConfigFragment extends Fragment {

    public ConfigFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conifg, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listView_config);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //设置
                        break;
                    case 1:
                        //清除在线阅读缓存
                        new AsyncTask<Context, Void, Void>() {
                            @Override
                            protected Void doInBackground(Context... params) {
                                Context context = params[0];
                                //File file = new File(context.getCacheDir().toString() + "/picasso-cache/");
                                //Picasso.with(context).invalidate(file);
                                Glide.get(context).clearDiskCache();
                                return null;
                            }
                        }.execute(getActivity());
                        Toast.makeText(getActivity(), "清除缓存成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        //关于
                        break;
                }
            }
        });
        return view;
    }

}
