/*
 * Copyright 2016-2018 huxizhijian
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

package org.huxizhijian.hhcomicviewer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.huxizhijian.hhcomicviewer.utils.ViewHolder;

import java.util.List;

/**
 * 通用Adapter
 * Created by wei on 2016/8/16.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    protected int mLayoutResId;

    public CommonAdapter(Context context, List<T> dates, int layoutResId) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mDatas = dates;
        this.mLayoutResId = layoutResId;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = ViewHolder.getInstance(mContext, convertView, parent, position, mLayoutResId);

        convert(vh, getItem(position));

        return vh.getConvertView();
    }

    public abstract void convert(ViewHolder vh, T t);

    public void setDatas(List<T> datas) {
        this.mDatas = datas;
    }
}
