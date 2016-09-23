package org.huxizhijian.hhcomicviewer2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.huxizhijian.hhcomicviewer2.R;

import java.util.List;

/**
 * 对应显示vol列表的适配器
 * Created by wei on 2016/8/25.
 */
public class VolRecyclerViewAdapter extends RecyclerView.Adapter<VolRecyclerViewAdapter.VolViewHolder> {

    private List<String> mVolName;
    private LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;
    private int mCapturePosition = 0;
    private boolean mIsReaded = false;
    private List<String> mComicCaptureList;  //下载好的章节

    public VolRecyclerViewAdapter(Context context, List<String> volName) {
        this.mVolName = volName;
        this.mInflater = LayoutInflater.from(context);
    }

    public VolRecyclerViewAdapter(Context context, List<String> volName, List<String> comicCaptures) {
        this.mVolName = volName;
        this.mInflater = LayoutInflater.from(context);
        this.mComicCaptureList = comicCaptures;
    }

    public VolRecyclerViewAdapter(Context context, List<String> volName, int capturePosition, List<String> comicCaptures) {
        this.mVolName = volName;
        this.mInflater = LayoutInflater.from(context);
        this.mCapturePosition = capturePosition;
        this.mIsReaded = true;
        this.mComicCaptureList = comicCaptures;
    }

    @Override
    public VolViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_vol_recycler_view, parent, false);
        return new VolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VolViewHolder holder, int position) {
        holder.tv.setText(mVolName.get(position));
        if (mIsReaded && position == mCapturePosition) {
            //标记上次阅读的章节
            holder.itemView.setBackgroundResource(R.drawable.bg_item_vol_pressed);
        } else {
            if (mComicCaptureList != null) {
                //标记下载好的章节
                if (mComicCaptureList.contains(mVolName.get(position))) {
                    //进行标记
                    holder.itemView.setBackgroundResource(R.drawable.bg_item_vol_downloaded);
                } else {
                    holder.itemView.setBackgroundResource(R.drawable.bg_item_vol);
                }
            } else {
                holder.itemView.setBackgroundResource(R.drawable.bg_item_vol);
            }
        }
        setUpItemEvent(holder);
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

    @Override
    public int getItemCount() {
        return mVolName.size();
    }

    public void setReadCapture(int capturePosition) {
        int prePosition = this.mCapturePosition;
        this.mCapturePosition = capturePosition;
        mIsReaded = true;
        notifyItemChanged(prePosition);
        notifyItemChanged(mCapturePosition);
    }

    class VolViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public VolViewHolder(View itemView) {
            super(itemView);

            tv = (TextView) itemView.findViewById(R.id.tv_vol_name_item);
        }
    }
}
