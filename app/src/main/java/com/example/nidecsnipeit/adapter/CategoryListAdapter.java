package com.example.nidecsnipeit.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.activity.SetupFieldActivity;
import com.example.nidecsnipeit.network.model.BasicItemModel;

import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {
    private final List<BasicItemModel> mData;
    private final LayoutInflater mInflater;
    private final Context context;

    // Data is passed into the constructor
    public CategoryListAdapter(Context context, List<BasicItemModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = data;
    }

    // Inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.basic_item_layout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BasicItemModel currentItem = mData.get(position);
        holder.textView.setText(currentItem.toString());

        // handle click event
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SetupFieldActivity.class);
                intent.putExtra("CATEGORY_ID", currentItem.getId());
                intent.putExtra("CATEGORY_NAME", currentItem.toString());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.basic_item_name);
        }
    }
}
