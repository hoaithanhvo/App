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
import com.example.nidecsnipeit.model.ListItemModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MaintenanceListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CustomRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_list);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Maintenance list");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        List<ListItemModel> dataList = new ArrayList<>();
        String[] dropdownItems = {"Option 1", "Option 2", "Option 3"};
        dataList.add(new ListItemModel("Title 1", "Value 1", ListItemModel.Mode.TEXT));
        dataList.add(new ListItemModel("Title 2", "Value 2", ListItemModel.Mode.EDIT_TEXT));
        dataList.add(new ListItemModel("Title 2.1", "Value 3", ListItemModel.Mode.EDIT_TEXT));
        dataList.add(new ListItemModel("Title 3", "", ListItemModel.Mode.DROPDOWN, dropdownItems));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomRecyclerAdapter(this, dataList);
        recyclerView.setAdapter(adapter);

        Button submitBtn = findViewById(R.id.submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> allValues = adapter.getAllValuesByTitle();
                Toast.makeText(MaintenanceListActivity.this, "editTextValue", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // this event will enable the back
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}