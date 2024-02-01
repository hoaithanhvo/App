package com.example.nidecsnipeit.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.activity.MyApplication;
import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.model.ListItemModel;
import com.example.nidecsnipeit.model.SpinnerItemModel;
import com.example.nidecsnipeit.utility.Common;
import com.example.nidecsnipeit.utility.FullNameConvert;
import com.example.nidecsnipeit.utility.QRScannerHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItemAdapter extends RecyclerView.Adapter<CustomItemAdapter.ViewHolder> {

    private final List<ListItemModel> mData;
    private final LayoutInflater mInflater;
    private final RecyclerView recyclerView;
    private int currentPosition;
    private final MyApplication MyApp;

    // Data is passed into the constructor
    public CustomItemAdapter(Context context, List<ListItemModel> data, RecyclerView recyclerView) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.recyclerView = recyclerView;
        this.MyApp = (MyApplication) context.getApplicationContext();
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
        holder.titleTextView.setBackgroundColor(mInflater.getContext().getColor(currentItem.getTitleColor()));

        int paddingHorizontal = Common.convertDpToPixel(2, holder.itemView.getContext());
        int paddingVertical = Common.convertDpToPixel(8, holder.itemView.getContext());
        holder.valueTextView.setVisibility(View.GONE);
        holder.editTextView.setVisibility(View.GONE);
        holder.dropdownView.setVisibility(View.GONE);
        holder.qrScannerView.setVisibility(View.GONE);
        switch (currentItem.getMode()) {
            case TEXT:
                holder.valueTextView.setGravity(Gravity.CENTER_VERTICAL);
                if (currentItem.getIcon() != null) {
                    holder.textIconView.setImageDrawable(currentItem.getIcon());
                    holder.textIconView.setVisibility(View.VISIBLE);
                }
                holder.valueTextView.setText(currentItem.getValue());
                holder.valueTextView.setVisibility(View.VISIBLE);
                break;
            case EDIT_TEXT:
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
                CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(mInflater.getContext(), android.R.layout.simple_spinner_item, currentItem.getDropdownItems());
                adapter.setDropDownViewResource(R.layout.custom_spinner_item);

                holder.dropdownView.setGravity(Gravity.CENTER_VERTICAL);
                holder.dropdownView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
                holder.dropdownView.setAdapter(adapter);
                holder.dropdownView.setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);

                if (!currentItem.getValue().equals("") && currentItem.getValue() != null) {
                    int selectedIndex = findSelectedIndex(currentItem.getDropdownItems(), currentItem.getValue());
                    holder.dropdownView.setSelection(selectedIndex);
                }
                holder.dropdownView.setVisibility(View.VISIBLE);
                holder.dropdownView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        SpinnerItemModel selectedModel = (SpinnerItemModel) parentView.getItemAtPosition(position);
                        String selectedValue = selectedModel.getName();
                        currentItem.setValue(selectedValue);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                    }
                });

                if (currentItem.isDropdownScanner()) {
                    holder.qrScannerView.setVisibility(View.VISIBLE);
                    holder.qrScannerView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setCurrentPosition(holder.getAdapterPosition());
                            QRScannerHelper.initiateScan((Activity) v.getContext());
                        }
                    });
                }

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
        ImageView textIconView;
        TextView valueTextView;
        EditText editTextView;
        Spinner dropdownView;
        ImageButton qrScannerView;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_item);
            textIconView = itemView.findViewById(R.id.text_icon);
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
            if (item.getMode() == ListItemModel.Mode.DROPDOWN && !keyTitle.equals("asset_maintenance_type")) {
                valuesMap.put(keyTitle, this.getIdSpinnerItemByName(item.getDropdownItems(), item.getValue()));
            } else {
                valuesMap.put(keyTitle, item.getValue());
            }
        }

        return valuesMap;
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

        if (viewHolder instanceof CustomItemAdapter.ViewHolder) {
            return ((CustomItemAdapter.ViewHolder) viewHolder).dropdownView;
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
