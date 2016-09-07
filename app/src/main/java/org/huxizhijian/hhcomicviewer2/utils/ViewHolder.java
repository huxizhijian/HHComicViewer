package org.huxizhijian.hhcomicviewer2.utils;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 万能ViewHolder
 * Created by wei on 2016/8/16.
 */
public class ViewHolder {
    private SparseArray<View> mViews;
    private View mConvertView;
    private int mPosition;

    public int getPosition() {
        return mPosition;
    }

    public ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mPosition = position;
        mViews = new SparseArray<>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    public static ViewHolder getInstance(Context context, View convertView
            , ViewGroup parent, int position, int layoutId) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        } else {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mPosition = position;
            return holder;
        }
    }

    /**
     * 通过viewId获取控件
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 设置TextView的值
     * @param resId
     * @param text
     * @return
     */
    public ViewHolder setText(int resId, String text) {
        ((TextView) getView(resId)).setText(text);
        return this;
    }
}
