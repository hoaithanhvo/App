package com.example.nidecsnipeit.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.CaptureActivityPortrait;
import com.example.nidecsnipeit.MyApplication;
import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.model.ListItemModel;
import com.example.nidecsnipeit.model.SpinnerItemModel;
import com.example.nidecsnipeit.utils.Common;
import com.example.nidecsnipeit.utils.FullNameConvert;
import com.example.nidecsnipeit.utils.QRScannerHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> {

    private final List<ListItemModel> mData;
    private final LayoutInflater mInflater;
    private final RecyclerView recyclerView;
    private int currentPosition;
    private MyApplication MyApp;
    // Data is passed into the constructor
    public CustomRecyclerAdapter(Context context, List<ListItemModel> data, RecyclerView recyclerView) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.recyclerView = recyclerView;
        this.MyApp = (MyApplication) context.getApplicationContext();
    }

//    public CustomRecyclerAdapter(Context context, List<ListItemModel> data, RecyclerView recyclerView, MyApplication MyApp) {
//        this.mInflater = LayoutInflater.from(context);
//        this.mData = data;
//        this.recyclerView = recyclerView;
//        this.MyApp = MyApp;
//    }

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
        holder.titleTextView.setPadding(8, 8, 8, 8);
        holder.titleTextView.setBackgroundColor(mInflater.getContext().getColor(currentItem.getTitleColor()));

        int heightInDp = 32;
        int heightPx = Common.convertDpToPixel(heightInDp, holder.itemView.getContext());
        holder.valueTextView.setVisibility(View.GONE);
        holder.editTextView.setVisibility(View.GONE);
        holder.dropdownView.setVisibility(View.GONE);
        holder.qrScannerView.setVisibility(View.GONE);
        switch (currentItem.getMode()) {
            case TEXT:
                holder.valueTextView.setTextSize(14);
                holder.valueTextView.setPadding(10, 8, 10, 8);
                holder.valueTextView.setMinHeight(heightPx);
                holder.valueTextView.setGravity(Gravity.CENTER_VERTICAL);
                holder.valueTextView.setText(addIconToText(holder.itemView.getContext(), currentItem.getValue(), currentItem.getIcon()));
                holder.valueTextView.setVisibility(View.VISIBLE);
                break;
            case EDIT_TEXT:
                holder.editTextView.setTextSize(16);
                holder.editTextView.setMinHeight(heightPx);
                holder.valueTextView.setPadding(10, 8, 10, 8);
                holder.editTextView.setInputType(InputType.TYPE_CLASS_TEXT);
                holder.editTextView.setGravity(Gravity.CENTER_VERTICAL);
                holder.editTextView.setText(currentItem.getValue());
                holder.editTextView.setVisibility(View.VISIBLE);
                holder.editTextView.addTextChangedListener(new TextWatcher() {
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
                ArrayAdapter<SpinnerItemModel> adapter = new ArrayAdapter<>(holder.itemView.getContext(),
                    android.R.layout.simple_dropdown_item_1line, currentItem.getDropdownItems());
                int selectedIndex = findSelectedIndex(currentItem.getDropdownItems(), currentItem.getValue());
                holder.dropdownView.setGravity(Gravity.CENTER_VERTICAL);
                holder.dropdownView.setAdapter(adapter);
                holder.dropdownView.setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                holder.dropdownView.setSelection(selectedIndex);
                holder.dropdownView.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = holder.dropdownView.getLayoutParams();
                params.height = heightPx;
                holder.dropdownView.setLayoutParams(params);

                holder.dropdownView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        String selectedValue = parentView.getItemAtPosition(position).toString();
                        currentItem.setValue(selectedValue);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                    }
                });

                holder.qrScannerView.setVisibility(View.VISIBLE);
                holder.qrScannerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setCurrentPosition(holder.getAdapterPosition());
                        QRScannerHelper.initiateScan((Activity) v.getContext());
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
        EditText editTextView;
        Spinner dropdownView;
        ImageButton qrScannerView;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_item);
            valueTextView = itemView.findViewById(R.id.text_item);
            editTextView = itemView.findViewById(R.id.edit_text_item);
            dropdownView = itemView.findViewById(R.id.dropdown_item);
            qrScannerView = itemView.findViewById(R.id.qr_scanner_btn);
        }
    }

    public Map<String, String> getAllValuesByTitle() {
        Map<String, String> valuesMap = new HashMap<>();

        for (ListItemModel item : mData) {
            String keyTitle = FullNameConvert.getKeyByFullName(item.getTitle());
            if (item.getMode() == ListItemModel.Mode.DROPDOWN && MyApp.isRequiredIdDropdown(keyTitle)) {
                valuesMap.put(keyTitle, this.getIdSpinnerItemByName(item.getDropdownItems(), item.getValue()));
            } else {
                valuesMap.put(keyTitle, item.getValue());
            }
        }

        return valuesMap;
    }

    private SpannableString addIconToText(Context context, String text, Drawable icon) {
        SpannableString spannableString = new SpannableString(text);
        if (icon != null) {
            spannableString = new SpannableString(" " + text);
            icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(icon, ImageSpan.ALIGN_BASELINE);
            spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    public void updateDropdownSelection(int position, String selectedValue) {
        ListItemModel listItem = mData.get(position);

        if (listItem.getMode() == ListItemModel.Mode.DROPDOWN) {
            List<SpinnerItemModel> dropdownItems = listItem.getDropdownItems();

            // Find index selectedValue in dropdownItems list
            int selectedIndex = findSelectedIndex(dropdownItems, selectedValue);

            if (selectedIndex != -1) {
                listItem.setValue(selectedValue);

                // Find the spinner corresponding with position
                Spinner dropdownView = findDropdownViewForItem(position);

                // Set selection by selectedIndex in Spinner
                if (dropdownView != null) {
                    dropdownView.setSelection(selectedIndex);
                }
            }
        }
    }

    private int findSelectedIndex(List<SpinnerItemModel> itemList, String targetValue) {
        int index = 0;
        for (SpinnerItemModel item : itemList) {
            if (item.getName().equals(targetValue)) {
                return index;
            }
            index ++;
        }
        return -1;
    }

    private Spinner findDropdownViewForItem(int position) {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);

        if (viewHolder instanceof CustomRecyclerAdapter.ViewHolder) {
            return ((CustomRecyclerAdapter.ViewHolder) viewHolder).dropdownView;
        }

        return null;
    }

    public void setCurrentPosition (int position) {
        this.currentPosition = position;
    }

    public int getCurrentPosition () {
        return this.currentPosition;
    }

    public String getIdSpinnerItemByName(List<SpinnerItemModel> spinnerItems, String name) {
        for (SpinnerItemModel item : spinnerItems) {
            if (item.getName().equals(name)) {
                return item.getId();
            }
        }
        return "-1";
    }
}
