package com.example.nidecsnipeit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.model.ListItemModel;
import com.example.nidecsnipeit.utils.Common;

import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> {

    private final List<ListItemModel> mData;
    private final LayoutInflater mInflater;
    private List<EditText> editTextItems;
    private List<Spinner> dropdownItems;

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
//                editTextItems.add(holder.editText);
                break;
            case DROPDOWN:
                // Add logic to set up data for the dropdown
                ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.itemView.getContext(),
                    android.R.layout.simple_dropdown_item_1line, currentItem.getDropdownItems());
                holder.dropdown.setGravity(Gravity.CENTER_VERTICAL);
                holder.dropdown.setAdapter(adapter);
                holder.dropdown.setVisibility(View.VISIBLE);
//                dropdownItems.add(holder.dropdown);
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
//    public List<String> getAllEditTextValues() {
//        List<String> editTextValues = new ArrayList<>();
//        for (EditText editText : editTextItems) {
//            editTextValues.add(editText.getText().toString());
//        }
//        return editTextValues;
//    }
//
//    public List<String> getAllDropdownValues() {
//        List<String> dropdownValues = new ArrayList<>();
//        for (Spinner dropdown : dropdownItems) {
//            dropdownValues.add(dropdown.getSelectedItem().toString());
//        }
//        return dropdownValues;
//    }



}
