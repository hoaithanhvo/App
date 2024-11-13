package com.example.nidecsnipeit.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.network.model.ProductDeliveryModel;

import java.util.ArrayList;
import java.util.List;

public class ProductDeliveryAdapter extends RecyclerView.Adapter<ProductDeliveryAdapter.ProductDelivyViewHolder> {
    private OnItemClickListener onItemClickListener;
    private List<ProductDeliveryModel> listitemProduct = new ArrayList<>();
    private Context context;
    public interface OnItemClickListener {
        void onItemClick(ProductDeliveryModel product);
    }
    public void setOnItemClickListener(OnItemClickListener listener, Context context) {
        this.onItemClickListener = listener;
        this.context = context;
    }
    public ProductDeliveryAdapter(){
        this.listitemProduct   = new ArrayList<>();
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
        holder.txtProductID.setTextColor(ContextCompat.getColor(context,R.color.primary));
        holder.txtUserID.setTextColor(ContextCompat.getColor(context,R.color.primary));
        holder.txtCreateAt.setTextColor(ContextCompat.getColor(context,R.color.primary));
        holder.txtNote.setTextColor(ContextCompat.getColor(context,R.color.primary));
        holder.txt_Status.setText(productDelivery.getStatusMap().get("name"));
        holder.crd_status.setCardBackgroundColor(Color.parseColor(productDelivery.getStatusMap().get("color")));
    }

    @Override
    public int getItemCount() {
        return listitemProduct.size();
    }

    class ProductDelivyViewHolder extends RecyclerView.ViewHolder{
        public TextView txtProductID , txtUserID,txtCreateAt,txtNote,txt_Status;
        public LinearLayout ItemBox;
        public CardView crd_status;

        public ProductDelivyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductID = itemView.findViewById(R.id.txtProductID);
            txtNote=itemView.findViewById(R.id.txtNote);
            txtCreateAt=itemView.findViewById(R.id.txtCreateAt);
            txtUserID=itemView.findViewById(R.id.txtUserID);
            ItemBox= itemView.findViewById(R.id.ItemBox);
            txt_Status=itemView.findViewById(R.id.txt_Status);
            crd_status = itemView.findViewById(R.id.crd_status);
        }
    }

    public void addData(List<ProductDeliveryModel> newListItem) {
        int startPosition = listitemProduct.size();
        listitemProduct.addAll(newListItem);
        notifyItemRangeInserted(startPosition, newListItem.size());
    }
    public List<ProductDeliveryModel> getListItems() {
        return listitemProduct;
    }
    public void searchData(List<ProductDeliveryModel> newListItem) {
        listitemProduct.clear();
        listitemProduct.addAll(newListItem);
        notifyDataSetChanged();
    }
}
