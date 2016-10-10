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

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.activities.DownloadManagerActivity;
import org.huxizhijian.hhcomicviewer2.activities.PreferenceActivity;
import org.huxizhijian.hhcomicviewer2.adapter.CommonAdapter;
import org.huxizhijian.hhcomicviewer2.utils.ViewHolder;

import java.util.ArrayList;
import java.util.List;


public class ConfigFragment extends Fragment {

    private List<Config> mConfigs;

    public ConfigFragment() {
    }

    class Config {
        Config(int iconResID, String text) {
            this.iconResID = iconResID;
            this.text = text;
        }

        int iconResID;
        String text;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config, container, false);
        ListView listView = (ListView) view.findViewById(R.id.listView_config);
        initData();

        listView.setAdapter(new CommonAdapter<Config>(getActivity(), mConfigs, R.layout.item_config_list) {
            @Override
            public void convert(ViewHolder vh, Config config) {
                ((ImageView) vh.getView(R.id.imageView_config_fragment)).setImageResource(config.iconResID);
                vh.setText(R.id.textView_config_fragment, config.text);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch (position) {
                    case 0:
                        //阅读
                        intent = new Intent(getActivity(), PreferenceActivity.class);
                        intent.setAction(PreferenceActivity.ACTION_READING);
                        startActivity(intent);
                        break;
                    case 1:
                        //打开下载列表
                        intent = new Intent(getActivity(), DownloadManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        //高级
                        intent = new Intent(getActivity(), PreferenceActivity.class);
                        intent.setAction(PreferenceActivity.ACTION_ADVANCE);
                        startActivity(intent);
                        break;
                    case 3:
                        //历史
                        intent = new Intent(getActivity(), PreferenceActivity.class);
                        intent.setAction(PreferenceActivity.ACTION_HISTORY);
                        startActivity(intent);
                        break;
                    case 4:
                        //清除在线阅读缓存
                        new AsyncTask<Context, Void, Void>() {
                            @Override
                            protected Void doInBackground(Context... params) {
                                Context context = params[0];
                                Glide.get(context).clearDiskCache();
                                return null;
                            }
                        }.execute(getActivity());
                        Toast.makeText(getActivity(), "清除缓存成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        //关于
                        intent = new Intent(getActivity(), PreferenceActivity.class);
                        intent.setAction(PreferenceActivity.ACTION_ABOUT);
                        startActivity(intent);
                        break;
                }
            }
        });
        return view;
    }

    private void initData() {
        mConfigs = new ArrayList<>();
        Config read = new Config(R.mipmap.read, "阅读");
        Config download = new Config(R.mipmap.download_manager, "下载");
        Config advance = new Config(R.mipmap.advance, "高级");
        Config history = new Config(R.mipmap.history, "历史");
        Config clear = new Config(R.mipmap.delete, "清除在线阅读缓存");
        Config about = new Config(R.mipmap.about, "关于");

        mConfigs.add(read);
        mConfigs.add(download);
        mConfigs.add(advance);
        mConfigs.add(history);
        mConfigs.add(clear);
        mConfigs.add(about);
    }
}
