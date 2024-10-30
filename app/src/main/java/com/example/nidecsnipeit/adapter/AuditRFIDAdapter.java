package com.example.nidecsnipeit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;

import java.util.ArrayList;
import java.util.List;

public class AuditRFIDAdapter extends RecyclerView.Adapter<AuditRFIDAdapter.AuditRFIDViewHolder> {
    private List<String> listitemRFID;
    private List<Integer> itemTextColors;

    public AuditRFIDAdapter(List<String> listitemRFID) {
        this.listitemRFID = listitemRFID;
        itemTextColors =new ArrayList<>();
        for(int i = 0 ;i<listitemRFID.size();i++){
            itemTextColors.add(Color.BLACK);
        }
    }
    @NonNull
    @Override
    public AuditRFIDViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rfid, parent, false);
        return new AuditRFIDViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull AuditRFIDViewHolder holder, int position) {
        holder.txtItemRFID.setText(position+ 1 + "."+ listitemRFID.get(position));
        if (position < itemTextColors.size()) {
            holder.txtItemRFID.setTextColor(itemTextColors.get(position));
        } else {
            holder.txtItemRFID.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return listitemRFID.size();
    }

    class AuditRFIDViewHolder extends RecyclerView.ViewHolder {
        public TextView txtItemRFID;

        public AuditRFIDViewHolder(@NonNull View itemView) {
            super(itemView);
            txtItemRFID = itemView.findViewById(R.id.txtItemRFID);
        }
    }
    public void addItemTop(String item, int color) {
        // Thêm item mới vào đầu danh sách
        listitemRFID.add(0, item);
        itemTextColors.add(0, color);  // Thêm màu tương ứng

        notifyItemInserted(0);
    }

    public void setItemColor(int position, int color) {
        if (position >= 0 && position < itemTextColors.size()) {
            itemTextColors.set(position, color);
            notifyItemChanged(position);
        }
    }
    public void updateData(List<String> listItemUnaudit){
        this.listitemRFID.clear();
        this.listitemRFID.addAll(listItemUnaudit);
        for(int i=0 ;i<listitemRFID.size();i++){
            itemTextColors.add(Color.BLACK);
        }
        notifyDataSetChanged();
    }
}
