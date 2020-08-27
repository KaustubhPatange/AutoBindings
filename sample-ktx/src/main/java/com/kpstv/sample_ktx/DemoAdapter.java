package com.kpstv.sample_ktx;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.lang.Override;

public class DemoAdapter extends ListAdapter<Data, com.kpstv.sample_ktx.DemoAdapter.TestAdapterHolder> {
    private TestAdapter className;

    public DemoAdapter(final TestAdapter className) {
        super(new androidx.recyclerview.widget.DiffUtil.ItemCallback<com.kpstv.sample_ktx.Data>() {
            @java.lang.Override
            public boolean areItemsTheSame(com.kpstv.sample_ktx.Data oldItem,
                                           com.kpstv.sample_ktx.Data newItem) {
                return className.diffItemSame(oldItem, newItem);
            }

            @java.lang.Override
            public boolean areContentsTheSame(com.kpstv.sample_ktx.Data oldItem,
                                              com.kpstv.sample_ktx.Data newItem) {
                return className.diffContentSame(oldItem, newItem);
            }
        });
        this.className = className;
    }

    @NonNull
    @Override
    public TestAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            final TestAdapterHolder holder = new TestAdapterHolder(android.view.LayoutInflater.from(parent.getContext()).inflate(2131427373, parent, false));



       /* holder.itemView.findViewById(2131230873).setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View view) {
                className.onClick(holder.itemView.getContext(), getItem(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });*/
            return holder;
        }else {
            final TestAdapterHolder holder = new TestAdapterHolder(android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress_layout, parent, false));
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(final TestAdapterHolder holder, final int position) {
        if (holder.itemView.findViewById(R.id.item_title) != null)
            ((TextView)holder.itemView.findViewById(R.id.item_title)).setText(getItem(position).getName());
        //className.bind(holder.itemView, getItem(position), position);
        // com.bumptech.glide.Glide.with(holder.itemView.getContext()).load(getItem(position).getName()).diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL).circleCrop().into((android.widget.ImageView) holder.itemView.findViewById(2131230860));
       /* holder.itemView.findViewById(2131230873).setOnLongClickListener(new android.view.View.OnLongClickListener() {
            public boolean onLongClick(android.view.View view) {
                className.onLongClick(holder.itemView.getContext(), getItem(holder.getAdapterPosition()), holder.getAdapterPosition());
                return true;
            }
        });*/
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 * 2;
    }

    static class TestAdapterHolder extends ViewHolder {
        public TestAdapterHolder(View view) {
            super(view);
        }
    }
}
