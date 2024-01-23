package com.example.nidecsnipeit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.model.ListItemModel;
import com.example.nidecsnipeit.utils.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> {

    private final List<ListItemModel> mData;
    private final LayoutInflater mInflater;

    // Data is passed into the constructor
    public CustomRecyclerAdapter(Context context, List<ListItemModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // Inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    // Binds the data to the TextView in each row
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListItemModel currentItem = mData.get(position);

        holder.titleTextView.setText(currentItem.getTitle());
        holder.titleTextView.setPadding(4, 8, 4, 8);
        holder.titleTextView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.rounded_title_bg));

        int heightInDp = 48;
        int heightPx = Common.convertDpToPixel(heightInDp, holder.itemView.getContext());
        switch (currentItem.getMode()) {
            case TEXT:
                holder.valueTextView.setTextSize(16);
                holder.valueTextView.setHeight(heightPx);
                holder.valueTextView.setGravity(Gravity.CENTER_VERTICAL);
                holder.valueTextView.setText(currentItem.getValue());
                holder.valueTextView.setVisibility(View.VISIBLE);
                break;
            case EDIT_TEXT:
                holder.editText.setTextSize(16);
                holder.editText.setHeight(heightPx);
                holder.editText.setInputType(InputType.TYPE_CLASS_TEXT);
                holder.editText.setGravity(Gravity.CENTER_VERTICAL);
                holder.editText.setText(currentItem.getValue());
                holder.editText.setVisibility(View.VISIBLE);
                holder.editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }
                    @Override
                    public void afterTextChanged(Editable editable) {
                        currentItem.setValue(editable.toString());
                    }
                });
                break;
            case DROPDOWN:
                // Add logic to set up data for the dropdown
                ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.itemView.getContext(),
                    android.R.layout.simple_dropdown_item_1line, currentItem.getDropdownItems());
                holder.dropdown.setGravity(Gravity.CENTER_VERTICAL);
                holder.dropdown.setAdapter(adapter);
                holder.dropdown.setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                holder.dropdown.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = holder.dropdown.getLayoutParams();
                params.height = heightPx;
                holder.dropdown.setLayoutParams(params);

                holder.dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        String selectedValue = parentView.getItemAtPosition(position).toString();
                        currentItem.setValue(selectedValue);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                    }
                });
                break;
        }
    }

    // Total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // Stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView valueTextView;
        EditText editText;
        Spinner dropdown;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            valueTextView = itemView.findViewById(R.id.valueTextView);
            editText = itemView.findViewById(R.id.editText);
            dropdown = itemView.findViewById(R.id.dropdown);
        }
    }

    public Map<String, String> getAllValuesByTitle() {
        Map<String, String> valuesMap = new HashMap<>();

        for (ListItemModel item : mData) {
            valuesMap.put(item.getTitle(), item.getValue());
        }

        return valuesMap;
    }
}
