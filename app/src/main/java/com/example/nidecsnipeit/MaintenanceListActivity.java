package com.example.nidecsnipeit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.nidecsnipeit.adapter.CustomRecyclerAdapter;
import com.example.nidecsnipeit.adapter.MaintenanceAdapter;
import com.example.nidecsnipeit.model.ListItemModel;
import com.example.nidecsnipeit.model.MaintenanceItemModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MaintenanceListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MaintenanceAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_list);

        // map item list to view
        List<MaintenanceItemModel> dataList = new ArrayList<>();
        dataList.add(new MaintenanceItemModel("Repair", "2024/01/30"));
        dataList.add(new MaintenanceItemModel("Maintenance", "2024/01/31"));
        dataList.add(new MaintenanceItemModel("Maintenance", "2024/01/31"));
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MaintenanceAdapter(this, dataList, recyclerView);
        recyclerView.setAdapter(adapter);

    }
}