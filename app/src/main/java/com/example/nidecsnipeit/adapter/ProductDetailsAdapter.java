package com.example.nidecsnipeit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.network.model.ProductDeliveryModel;
import com.example.nidecsnipeit.network.model.ProductDetailsModel;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsAdapter extends  RecyclerView.Adapter<ProductDetailsAdapter.ProduectDetailsViewHolder>  {
    private List<ProductDetailsModel> listDetails ;

    public ProductDetailsAdapter(List<ProductDetailsModel> productDetailsModels){
        this.listDetails = productDetailsModels;
    }

    @NonNull
    @Override
    public ProduectDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemproductdetails,parent,false);
        return new ProduectDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProduectDetailsViewHolder holder, int position) {
        ProductDetailsModel productDetails = listDetails.get(position);
        holder.txtAssetID.setText(String.valueOf(productDetails.getAssetID()));
        holder.txtAssetTag.setText(productDetails.getAssetTag());
        holder.txtSerial.setText(productDetails.getSerial());
        holder.txtName.setText(productDetails.getName());
        holder.txtCreatedDate.setText(productDetails.getCreatedAt());
        holder.txtStatus.setText(productDetails.getStatus());


    }

    @Override
    public int getItemCount() {
        return listDetails.size();
    }

    class ProduectDetailsViewHolder extends RecyclerView.ViewHolder{

        private TextView txtAssetID,txtAssetTag,txtSerial,txtName,txtCreatedDate,txtStatus;


        public ProduectDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAssetID = itemView.findViewById(R.id.txtAssetID);
            txtAssetTag = itemView.findViewById(R.id.txtAssetTag);
            txtSerial = itemView.findViewById(R.id.txtSerial);
            txtName = itemView.findViewById(R.id.txtName);
            txtCreatedDate = itemView.findViewById(R.id.txtCreatedDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }
}
