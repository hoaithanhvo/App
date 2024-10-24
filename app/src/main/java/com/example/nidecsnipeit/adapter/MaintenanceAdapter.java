package com.example.nidecsnipeit.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.activity.MaintenanceAddActivity;
import com.example.nidecsnipeit.activity.MaintenanceListActivity;
import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.network.model.AlertDialogCallback;
import com.example.nidecsnipeit.network.model.ListItemModel;
import com.example.nidecsnipeit.network.model.MaintenanceItemModel;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utility.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MaintenanceAdapter extends RecyclerView.Adapter<MaintenanceAdapter.ViewHolder> {
    private final List<MaintenanceItemModel> mData;
    private final LayoutInflater mInflater;
    private final RecyclerView recyclerView;
    public MaintenanceAdapter(Context context, List<MaintenanceItemModel> data, RecyclerView recyclerView) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_maintenance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MaintenanceItemModel currentItem = mData.get(position);

        // bind data to fields
        holder.textView.setText(currentItem.getAssetMaintenanceType());
        holder.dateView.setText(currentItem.getStartDate());

        List<ListItemModel> dataList = new ArrayList<>();
        dataList.add(new ListItemModel("Title", currentItem.getTitle()));
        dataList.add(new ListItemModel("Supplier", currentItem.getSupplierName()));
        CustomItemAdapter customAdapter = new CustomItemAdapter(mInflater.getContext(), dataList, holder.contentView);
        holder.contentView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        holder.contentView.setAdapter(customAdapter);

        // handle edit event
        holder.buttonEditView.setOnClickListener(v -> {
            Intent intent = new Intent(mInflater.getContext(), MaintenanceAddActivity.class);
            intent.putExtra("MAINTENANCE_INFO", currentItem);
            mInflater.getContext().startActivity(intent);
        });

        // handle delete event
        holder.buttonDeleteView.setOnClickListener(v -> {
            NetworkManager apiServices = NetworkManager.getInstance();
            Common.showCustomAlertDialog(mInflater.getContext(), "Delete maintenance", "Are you sure you want to delete this maintenance? This operation cannot be undone", true, new AlertDialogCallback() {
                @Override
                public void onPositiveButtonClick() {
                    Common.showProgressDialog(mInflater.getContext(), "Deleting...");
                    apiServices.deleteMaintenanceItem(currentItem.getId(), new NetworkResponseListener<JSONObject>() {
                        @Override
                        public void onResult(JSONObject object) {
                            try {
                                if (object.has("status") && object.get("status").equals("error")) {
                                    Common.showCustomSnackBar(v, object.get("messages").toString(), Common.SnackBarType.ERROR, null);
                                } else {
                                    Intent intent = new Intent(mInflater.getContext(), MaintenanceListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("ASSET_ID", currentItem.getAssetID());
                                    intent.putExtra("DELETED", true);
                                    mInflater.getContext().startActivity(intent);
                                }
                            } catch (JSONException e) {
                                Common.showCustomSnackBar(v, e.getMessage(), Common.SnackBarType.ERROR, null);
                            }
                            Common.hideProgressDialog();
                        }
                    }, new NetworkResponseErrorListener() {
                        @Override
                        public void onErrorResult(Exception error) {
                            Common.hideProgressDialog();
                        }
                    });
                }
                @Override
                public void onNegativeButtonClick() {
                    Common.hideProgressDialog();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView dateView;
        RecyclerView contentView;
        Button buttonEditView;
        Button buttonDeleteView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_title_item);
            dateView = itemView.findViewById(R.id.date_title_item);
            contentView = itemView.findViewById(R.id.content_item);
            buttonEditView = itemView.findViewById(R.id.button_edit_item);
            buttonDeleteView = itemView.findViewById(R.id.button_delete_item);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }
}
