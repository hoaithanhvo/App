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

        Button submitBtn = findViewById(R.id.submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> allValues = adapter.getAllValuesByTitle();
                Toast.makeText(MaintenanceListActivity.this, "editTextValue", Toast.LENGTH_SHORT).show();
            }
        });
    }
}