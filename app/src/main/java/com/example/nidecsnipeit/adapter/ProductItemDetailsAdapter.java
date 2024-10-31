package com.example.nidecsnipeit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.network.model.ProductItemDetailsModel;

import java.util.List;

public class ProductItemDetailsAdapter extends RecyclerView.Adapter<ProductItemDetailsAdapter.ProductItemDetailsViewHolder> {
    List<ProductItemDetailsModel> listItemDetails;
    public  ProductItemDetailsAdapter(List<ProductItemDetailsModel> productItemDetailsModels)
    {
        this.listItemDetails = productItemDetailsModels;
    }

    @NonNull
    @Override
    public ProductItemDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_details,parent,false);
        return new ProductItemDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductItemDetailsViewHolder holder, int position) {
        ProductItemDetailsModel productItemDetailsModel = listItemDetails.get(position);
        holder.txtAssetID.setText(productItemDetailsModel.getAssetID());
        holder.txtSerial.setText(productItemDetailsModel.getSerial());
        holder.txtName.setText(productItemDetailsModel.getName());
        holder.txtAssetTag.setText(productItemDetailsModel.getAssetTag());
    }

    @Override
    public int getItemCount() {
        return listItemDetails.size();
    }

    class ProductItemDetailsViewHolder extends RecyclerView.ViewHolder {

        private TextView txtAssetID,txtAssetTag,txtSerial,txtName;
        public ProductItemDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.findViewById(R.id.txtAssetID);
            itemView.findViewById(R.id.txtAssetTag);
            itemView.findViewById(R.id.txtSerial);
            itemView.findViewById(R.id.txtName);
        }
    }
}
