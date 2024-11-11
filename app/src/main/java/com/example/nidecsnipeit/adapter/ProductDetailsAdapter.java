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
import com.example.nidecsnipeit.network.model.ProductDetailsModel;

import java.util.List;

public class ProductDetailsAdapter extends  RecyclerView.Adapter<ProductDetailsAdapter.ProduectDetailsViewHolder>  {
    private List<ProductDetailsModel> listDetails ;
    private ProductDetailsAdapter.OnItemClickListener onItemClickListener;
    private Context context;
    public interface OnItemClickListener {
        void onItemClick(ProductDetailsModel product);
    }
    public void setOnItemClickListener(ProductDetailsAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;

    }
    public ProductDetailsAdapter(List<ProductDetailsModel> productDetailsModels, Context context){
        this.listDetails = productDetailsModels;
        this.context = context;
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
//        holder.itemBox.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(productDetails.getStatusMap().get("color"))));
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(productDetails);
            }
        });
        holder.txtCategory.setTextColor(ContextCompat.getColor(context,R.color.primary));
        holder.txtManufacture.setTextColor(ContextCompat.getColor(context,R.color.primary));
        holder.txtCatalog.setTextColor(ContextCompat.getColor(context,R.color.primary));
        holder.txtVarrial.setTextColor(ContextCompat.getColor(context,R.color.primary));
        holder.txtCreated.setTextColor(ContextCompat.getColor(context,R.color.primary));
        holder.txtStatus.setTextColor(ContextCompat.getColor(context,R.color.primary));
        holder.txtTotal.setTextColor(ContextCompat.getColor(context,R.color.primary));
        holder.crd_status.setCardBackgroundColor(Color.parseColor(productDetails.getStatusMap().get("color")));
        holder.txt_Status.setText(productDetails.getStatusMap().get("name"));
    }
    @Override
    public int getItemCount() {
        return listDetails.size();
    }

    class ProduectDetailsViewHolder extends RecyclerView.ViewHolder{
        private TextView txtCategory,txtManufacture,txtCatalog,txtVarrial,txtCreated,txtStatus,txtTotal,txt_Status;
        private LinearLayout itemBox;
        private CardView crd_status;
        public ProduectDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtManufacture = itemView.findViewById(R.id.txtManufacture);
            txtCatalog = itemView.findViewById(R.id.txtCatalog);
            txtVarrial = itemView.findViewById(R.id.txtVarrial);
            txtCreated = itemView.findViewById(R.id.txtCreated);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            itemBox = itemView.findViewById(R.id.ItemBox);
            txt_Status =itemView.findViewById(R.id.txt_Status);
            crd_status = itemView.findViewById(R.id.crd_status);
        }
    }
}
