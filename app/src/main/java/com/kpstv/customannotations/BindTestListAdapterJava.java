package com.kpstv.customannotations;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BindTestListAdapterJava extends ListAdapter<TestClass, BindTestListAdapterJava.TestListAdapterHolder> {
    private BindTestListAdapterJava className;

    public BindTestListAdapterJava(BindTestListAdapterJava className) {
        super(diffCallback);
        this.className = className;
    }

    @NonNull
    @Override
    public TestListAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TestListAdapterHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private static DiffUtil.ItemCallback<TestClass> diffCallback = new DiffUtil.ItemCallback<TestClass>() {
        @Override
        public boolean areItemsTheSame(@NonNull TestClass oldItem, @NonNull TestClass newItem) {
            // Are item same... will be inserted here
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull TestClass oldItem, @NonNull TestClass newItem) {
            // Are content same method... will be inserted here
            return false;
        }
    };

    static class TestListAdapterHolder extends RecyclerView.ViewHolder {
        public TestListAdapterHolder(View view) {
            super(view);
        }
    }
}
