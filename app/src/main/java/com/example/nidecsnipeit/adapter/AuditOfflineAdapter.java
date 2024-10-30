package com.example.nidecsnipeit.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.network.model.AuditOfflineModel;

import java.util.ArrayList;

public class AuditOfflineAdapter extends ArrayAdapter<AuditOfflineModel> {

    Activity context;
    int Idlayout;
    ArrayList<AuditOfflineModel> listdata;
    //Tạo contructor


    public AuditOfflineAdapter(Activity context, int idlayout, ArrayList<AuditOfflineModel> listdata) {
        super(context, idlayout,listdata);
        this.context = context;
        Idlayout = idlayout;
        this.listdata = listdata;
    }

    // Gọi hàm getView để sắp xếp;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater myflacter = context.getLayoutInflater();
        //Đặt id layout lên đế
        convertView = myflacter.inflate(Idlayout,null);
        AuditOfflineModel mydata = listdata.get(position);
        TextView txtname = convertView.findViewById(R.id.txtname);
        txtname.setText(mydata.getName());
        TextView txtvalue = convertView.findViewById(R.id.txtvalue);
        txtvalue.setText(mydata.getValue());
        return convertView;
    }
}
