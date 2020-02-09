package com.laioffer.matrix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class ReportRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Item> mItems;
    private LayoutInflater mInflater;
    private OnClickListener mClickListener;

    public interface OnClickListener {
        public void setItem(String item);
    }

    public void setClickListener(ReportRecyclerViewAdapter.OnClickListener callback) {
        mClickListener = callback;
    }

    public ReportRecyclerViewAdapter(Context context, List<Item> items) {
        this.mInflater = LayoutInflater.from(context);
        this.mItems = items;
    }

    /**
     * Step 2: create holder prepare listview to show
     *
     * @param parent   the listview
     * @param viewType mView type
     * @return created mView holder
     */

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        RecyclerView.ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.mTextView.setText(mItems.get(position).getDrawable_label());
        viewHolder.mImageView.setImageResource(mItems.get(position).getDrawable_id());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.setItem(mItems.get(position).getDrawable_label());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Step1 : declare the mView holder structure
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ImageView mImageView;
        View mView;

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mTextView = itemView.findViewById(R.id.info_text);
            mImageView = itemView.findViewById(R.id.info_img);
        }
    }

}
