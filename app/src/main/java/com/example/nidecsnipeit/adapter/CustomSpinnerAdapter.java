package com.example.nidecsnipeit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.example.nidecsnipeit.model.BasicItemModel;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<BasicItemModel> {

    private final LayoutInflater inflater;
    private final List<BasicItemModel> items;

    public CustomSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<BasicItemModel> items) {
        super(context, resource, items);
        this.inflater = LayoutInflater.from(context);
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, android.R.layout.simple_spinner_item);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, android.R.layout.simple_spinner_dropdown_item);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
        View view = convertView != null ? convertView : inflater.inflate(resource, parent, false);

        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(items.get(position).getName());

        return view;
    }
}
