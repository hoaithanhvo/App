package com.example.nidecsnipeit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.network.model.ProductItemDetailsModel;

import java.util.ArrayList;
import java.util.List;

public class ExportGoodsAdapter extends RecyclerView.Adapter<ExportGoodsAdapter.ExportAssetViewHolder> {

    ArrayList<String> productItemDetailsModelList;
    public ExportGoodsAdapter(ArrayList<String> productItemDetailsModelList){
        this.productItemDetailsModelList = productItemDetailsModelList;
    }

    @NonNull
    @Override
    public ExportAssetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_spinner_item,parent,false);
        return new ExportAssetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExportAssetViewHolder holder, int position) {
        String productDetailsModel = productItemDetailsModelList.get(position);
        holder.txt_TextView.setText(position+1+". "+ productDetailsModel);
    }

    @Override
    public int getItemCount() {
        return productItemDetailsModelList.size();
    }

    class ExportAssetViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_TextView;
        public ExportAssetViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_TextView = itemView.findViewById(R.id.txt_TextView);
        }
    }
}
