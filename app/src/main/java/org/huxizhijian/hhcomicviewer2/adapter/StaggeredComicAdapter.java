package org.huxizhijian.hhcomicviewer2.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.huxizhijian.hhcomicviewer2.R;
import org.huxizhijian.hhcomicviewer2.enities.Comic;

import java.util.List;

/**
 * 瀑布流版RecyclerView适配器
 * Created by wei on 2016/9/7.
 */
public class StaggeredComicAdapter extends RecyclerView.Adapter<StaggeredComicAdapter.StaggeredViewHolder> {

    private List<Comic> mComicList;
    private OnItemClickListener mOnItemClickListener;
    private LayoutInflater mInflater;
    private Context mContext;

    public StaggeredComicAdapter(Context context, List<Comic> comicList) {
        this.mInflater = LayoutInflater.from(context);
        this.mComicList = comicList;
        this.mContext = context;
    }

    @Override
    public StaggeredViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_staggered_recycler_view, parent, false);
        return new StaggeredViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StaggeredViewHolder holder, int position) {
        holder.tv.setText(mComicList.get(holder.getLayoutPosition()).getTitle());
        Glide.with(mContext)
                .load(mComicList.get(holder.getLayoutPosition()).getThumbnailUrl())
                .asBitmap()
                .placeholder(R.mipmap.blank)
                .dontAnimate()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        holder.iv.setImageBitmap(bitmap);
                    }
                });
        setUpItemEvent(holder);
    }

    @Override
    public int getItemCount() {
        return mComicList.size();
    }

    protected void setUpItemEvent(final StaggeredViewHolder holder) {
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

    public void setComicList(List<Comic> comicList) {
        this.mComicList = comicList;
    }

    public void removeItem(int position) {
        mComicList.remove(position);
        notifyItemRemoved(position);
    }

    class StaggeredViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView tv;

        public StaggeredViewHolder(View itemView) {
            super(itemView);
            //绑定控件
            iv = (ImageView) itemView.findViewById(R.id.imageView_staggered);
            tv = (TextView) itemView.findViewById(R.id.textView_staggered);
        }
    }
}
