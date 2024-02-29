package com.example.nidecsnipeit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.model.CategoryFieldModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class CategoryFieldAdapter extends RecyclerView.Adapter<CategoryFieldAdapter.ViewHolder> {
    private final List<CategoryFieldModel> mData;
    private final LayoutInflater mInflater;
    private final Context context;

    // Data is passed into the constructor
    public CategoryFieldAdapter(Context context, List<CategoryFieldModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = data;
    }

    // Inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.field_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryFieldModel currentItem = mData.get(position);

        holder.textView.setText(currentItem.getName());
        holder.switchView.setChecked(currentItem.isDisplayed() == 1);

        // Handle switch state change
        holder.switchView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mData.get(holder.getAdapterPosition()).setDisplayed(holder.switchView.isChecked());
        });

        // Handle switch state change
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.switchView.setChecked(!holder.switchView.isChecked());
                mData.get(holder.getAdapterPosition()).setDisplayed(holder.switchView.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout parentView;
        TextView textView;
        SwitchMaterial switchView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentView = itemView.findViewById(R.id.parent_field_item);
            textView = itemView.findViewById(R.id.field_text);
            switchView = itemView.findViewById(R.id.field_switch);
       }
    }

    public List<String> getColumnAllowedDisplay() {
        List<String> dataList = new ArrayList<>();

        for (CategoryFieldModel item : mData) {
            if (item.isDisplayed() == 1) {
                dataList.add(item.getColumnName());
            }
        }

        return dataList;
    }

}
