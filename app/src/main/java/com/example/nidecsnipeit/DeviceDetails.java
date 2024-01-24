package com.example.nidecsnipeit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.adapter.CustomRecyclerAdapter;
import com.example.nidecsnipeit.model.ListItemModel;
import com.example.nidecsnipeit.utils.QRScannerHelper;

import java.util.ArrayList;
import java.util.List;

public class DeviceDetails extends BaseActivity {
    CustomRecyclerAdapter adapter;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);

        // Set up action bar
        setupActionBar("Device details");

        List<ListItemModel> dataList = new ArrayList<>();
        String[] dropdownItems = {"Option 1", "Option 2", "Option 3"};
        dataList.add(new ListItemModel("Title 3", ListItemModel.Mode.DROPDOWN, dropdownItems));
        dataList.add(new ListItemModel("Model", "Macbook Pro 16''", ListItemModel.Mode.TEXT));
        dataList.add(new ListItemModel("Serial Number", "d0bf8673-19dc-33cd-8658-e88006f38156d0bf8673-19dc-33cd-8658", ListItemModel.Mode.TEXT));
        dataList.add(new ListItemModel("Asset Name", "Not defined", ListItemModel.Mode.TEXT));
        dataList.add(new ListItemModel("Location", "New York", ListItemModel.Mode.TEXT, getDrawable(R.drawable.ic_location)));
        dataList.add(new ListItemModel("Current Value", "USD 225.389,33", ListItemModel.Mode.TEXT));
        dataList.add(new ListItemModel("Notes", "Nidec", ListItemModel.Mode.TEXT));

        dataList.add(new ListItemModel("Title 2", "Value 2", ListItemModel.Mode.EDIT_TEXT));
        dataList.add(new ListItemModel("Title 2.1", "Value 3", ListItemModel.Mode.EDIT_TEXT));
        dataList.add(new ListItemModel("Title 3", ListItemModel.Mode.DROPDOWN, dropdownItems));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomRecyclerAdapter(this, dataList);
        recyclerView.setAdapter(adapter);
    }
}