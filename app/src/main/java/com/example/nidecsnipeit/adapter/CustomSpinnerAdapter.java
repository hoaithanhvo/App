package com.example.nidecsnipeit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.example.nidecsnipeit.model.SpinnerItemModel;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<SpinnerItemModel> {

    public CustomSpinnerAdapter(Context context, int resource, List<SpinnerItemModel> items) {
        super(context, resource, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        SpinnerItemModel currentItem = getItem(position);

        if (currentItem != null) {
//            textView.setText(String.valueOf(currentItem.getId()));
             textView.setText(currentItem.getName());
        }

        return convertView;
    }
}
