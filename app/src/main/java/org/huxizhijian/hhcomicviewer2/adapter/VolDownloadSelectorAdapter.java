package org.huxizhijian.hhcomicviewer2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.huxizhijian.hhcomicviewer2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 下载选择RecyclerView适配器
 * Created by wei on 2016/9/13.
 */
public class VolDownloadSelectorAdapter extends RecyclerView.Adapter<VolDownloadSelectorAdapter.VolViewHolder> {
    private List<String> mVolName;
    private LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;
    private List<String> mComicCaptureList; //已经开始下载的章节
    private List<String> mSelectedCaptureNames; //选择的下载章节

    public VolDownloadSelectorAdapter(Context context, List<String> volName, List<String> comicCaptures) {
        this.mVolName = volName;
        this.mInflater = LayoutInflater.from(context);
        this.mComicCaptureList = comicCaptures;
        mSelectedCaptureNames = new ArrayList<>();
    }

    @Override
    public VolViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_vol_recycler_view, parent, false);
        return new VolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VolViewHolder holder, int position) {
        //未解决更新问题
        holder.tv.setText(mVolName.get(position));
        if (mComicCaptureList != null) {
            //标记下载好的章节
            if (mComicCaptureList.contains(mVolName.get(position))) {
                //进行标记
                holder.itemView.setBackgroundResource(R.drawable.bg_item_vol_downloaded_normal);
            } else {
                if (mSelectedCaptureNames.contains(mVolName.get(position))) {
                    //选择的章节
                    holder.itemView.setBackgroundResource(R.drawable.bg_item_vol_pressed);
                } else {
                    holder.itemView.setBackgroundResource(R.drawable.bg_item_vol_normal);
                }
            }
        } else {
            if (mSelectedCaptureNames.contains(mVolName.get(position))) {
                //选择的章节
                holder.itemView.setBackgroundResource(R.drawable.bg_item_vol_pressed);
            } else {
                holder.itemView.setBackgroundResource(R.drawable.bg_item_vol_normal);
            }
        }
        setUpItemEvent(holder);
    }

    public void captureClick(int position) {
        String captureName = mVolName.get(position);
        if (mComicCaptureList != null && mComicCaptureList.contains(captureName)) return;
        if (!mSelectedCaptureNames.contains(captureName)) {
            mSelectedCaptureNames.add(captureName);
            notifyItemChanged(position);
        } else {
            mSelectedCaptureNames.remove(captureName);
            notifyItemChanged(position);
        }
    }


    public List<String> getSelectedCaptureNames() {
        return mSelectedCaptureNames;
    }

    public void allSelect() {
        mSelectedCaptureNames.clear();
        for (int i = 0; i < mVolName.size(); i++) {
            if (mSelectedCaptureNames != null && mSelectedCaptureNames.contains(mVolName.get(i)))
                continue;
            mSelectedCaptureNames.add(mVolName.get(i));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mVolName.size();
    }

    protected void setUpItemEvent(final VolViewHolder holder) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });

            //longClick
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mOnItemClickListener.onItemLongClick(holder.itemView, holder.getLayoutPosition());
                    return false;
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    class VolViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public VolViewHolder(View itemView) {
            super(itemView);

            tv = (TextView) itemView.findViewById(R.id.tv_vol_name_item);
        }
    }
}
