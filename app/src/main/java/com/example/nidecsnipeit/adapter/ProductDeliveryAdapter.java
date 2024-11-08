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

import java.util.List;

public class ProductDeliveryAdapter extends RecyclerView.Adapter<ProductDeliveryAdapter.ProductDelivyViewHolder> {
    private OnItemClickListener onItemClickListener;
    private List<ProductDeliveryModel> listitemProduct;
    public interface OnItemClickListener {
        void onItemClick(ProductDeliveryModel product);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    public ProductDeliveryAdapter(List<ProductDeliveryModel> model){
        this.listitemProduct = model;
    }

    @NonNull
    @Override
    public ProductDelivyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product,parent,false);
        return new ProductDelivyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductDelivyViewHolder holder, int position) {
        ProductDeliveryModel productDelivery = listitemProduct.get(position);
        holder.txtProductID.setText(productDelivery.getProductID());
        holder.txtUserID.setText(productDelivery.getUserID());
        holder.txtCreateAt.setText(productDelivery.getCreateAt());
        holder.txtNote.setText(productDelivery.getNote());
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(productDelivery);
            }
        });
        holder.txtProductID.setTextColor(Color.BLACK);
        holder.txtUserID.setTextColor(Color.BLACK);
        holder.txtCreateAt.setTextColor(Color.BLACK);
        holder.txtNote.setTextColor(Color.BLACK);
        holder.ItemBox.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(productDelivery.getStatusMap().get("color"))));
    }

    @Override
    public int getItemCount() {
        return listitemProduct.size();
    }

    class ProductDelivyViewHolder extends RecyclerView.ViewHolder{
        public TextView txtProductID , txtUserID,txtCreateAt,txtNote;
        public LinearLayout ItemBox;
        public ProductDelivyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductID = itemView.findViewById(R.id.txtProductID);
            txtNote=itemView.findViewById(R.id.txtNote);
            txtCreateAt=itemView.findViewById(R.id.txtCreateAt);
            txtUserID=itemView.findViewById(R.id.txtUserID);
            ItemBox= itemView.findViewById(R.id.ItemBox);
        }
    }
}
