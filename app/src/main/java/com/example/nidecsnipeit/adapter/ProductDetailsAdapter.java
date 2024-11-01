package com.example.nidecsnipeit.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.network.model.ProductDeliveryModel;
import com.example.nidecsnipeit.network.model.ProductDetailsModel;

import java.util.List;

public class ProductDetailsAdapter extends  RecyclerView.Adapter<ProductDetailsAdapter.ProduectDetailsViewHolder>  {
    private List<ProductDetailsModel> listDetails ;
    private ProductDetailsAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(ProductDetailsModel product);
    }
    public void setOnItemClickListener(ProductDetailsAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    public ProductDetailsAdapter(List<ProductDetailsModel> productDetailsModels){
        this.listDetails = productDetailsModels;
    }

    @NonNull
    @Override
    public ProduectDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request,parent,false);
        return new ProduectDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProduectDetailsViewHolder holder, int position) {
        ProductDetailsModel productDetails = listDetails.get(position);
        holder.txtCategory.setText(String.valueOf(productDetails.getCategory()));
        holder.txtManufacture.setText(productDetails.getManufactory());
        holder.txtCatalog.setText(productDetails.getCatalog());
        holder.txtVarrial.setText(productDetails.getVarrial());
        holder.txtCreated.setText(productDetails.getCreated());
        holder.txtStatus.setText(productDetails.getStatusMap().get("name"));
        holder.txtTotal.setText(productDetails.getTotal());
        LinearLayout itemBox = holder.itemView.findViewById(R.id.ItemBox);

        if ("Ready".equals(productDetails.getStatusMap().get("name"))) {
            itemBox.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(productDetails.getStatusMap().get("color"))));
        } else {
            itemBox.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(productDetails.getStatusMap().get("color"))));
        }
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(productDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listDetails.size();
    }

    class ProduectDetailsViewHolder extends RecyclerView.ViewHolder{
        private TextView txtCategory,txtManufacture,txtCatalog,txtVarrial,txtCreated,txtStatus,txtTotal;
        public ProduectDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtManufacture = itemView.findViewById(R.id.txtManufacture);
            txtCatalog = itemView.findViewById(R.id.txtCatalog);
            txtVarrial = itemView.findViewById(R.id.txtVarrial);
            txtCreated = itemView.findViewById(R.id.txtCreated);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtTotal = itemView.findViewById(R.id.txtTotal);
        }
    }
}
