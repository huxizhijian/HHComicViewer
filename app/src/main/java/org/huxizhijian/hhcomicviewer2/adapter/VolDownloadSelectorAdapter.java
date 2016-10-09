package org.huxizhijian.hhcomicviewer2.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private List<String> mComicCaptureList; //已经开始下载的章节
    private List<String> mFinishedComicCaptureList;  //下载好的章节
    private List<String> mSelectedCaptureNames; //选择的下载章节

    private Bitmap mBitmap_ok = null;
    private Bitmap mBitmap_downloading = null;

    public VolDownloadSelectorAdapter(Context context, List<String> volName,
                                      List<String> comicCaptures, List<String> finishedComicCaptureList) {
        this.mVolName = volName;
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mComicCaptureList = comicCaptures;
        this.mFinishedComicCaptureList = finishedComicCaptureList;
        mSelectedCaptureNames = new ArrayList<>();
    }

    @Override
    public VolViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_vol_recycler_view, parent, false);
        return new VolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VolViewHolder holder, int position) {
        position = holder.getLayoutPosition();
        holder.tv.setText(mVolName.get(position));
        holder.iv.setVisibility(View.GONE);
        //标记下载好的章节
        if (mComicCaptureList != null && mComicCaptureList.contains(mVolName.get(position))) {
            //进行标记
            if (mFinishedComicCaptureList != null &&
                    mFinishedComicCaptureList.contains(mVolName.get(position))) {
                //如果是下载完成的章节
                if (mBitmap_ok == null) {
                    //进行图片的初始化
                    mBitmap_ok = BitmapFactory.decodeResource(mContext.getResources(),
                            R.mipmap.ic_check_green_18dp);
                }
                holder.iv.setImageBitmap(mBitmap_ok);
                holder.iv.setVisibility(View.VISIBLE);
            } else {
                //如果是未下载完成的章节
                if (mBitmap_downloading == null) {
                    //进行图片的初始化
                    mBitmap_downloading = BitmapFactory.decodeResource(mContext.getResources(),
                            R.mipmap.ic_file_download_grey600_18dp);
                }
                holder.iv.setImageBitmap(mBitmap_downloading);
                holder.iv.setVisibility(View.VISIBLE);
            }
        }
        if (mSelectedCaptureNames.contains(mVolName.get(position))) {
            //选择的章节
            holder.cv.setCardBackgroundColor(mContext.getResources().getColor(R.color.green_color_download));
        } else {
            holder.cv.setCardBackgroundColor(mContext.getResources().getColor(R.color.white));
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
            if (!(mComicCaptureList != null && mComicCaptureList.contains(mVolName.get(i)))) {
                mSelectedCaptureNames.add(mVolName.get(i));
            }
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
        CardView cv;
        ImageView iv;

        public VolViewHolder(View itemView) {
            super(itemView);

            tv = (TextView) itemView.findViewById(R.id.tv_vol_name_item);
            cv = (CardView) itemView.findViewById(R.id.cardView_vol_item);
            iv = (ImageView) itemView.findViewById(R.id.imageView_vol_item);
        }
    }
}
