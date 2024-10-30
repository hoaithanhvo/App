package com.example.nidecsnipeit.activity;

import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.AuditOfflineAdapter;
import com.example.nidecsnipeit.network.model.AuditModel;
import com.example.nidecsnipeit.network.model.AuditOfflineModel;
import com.example.nidecsnipeit.utility.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class LogAuditActivity extends AppCompatActivity {
    ListView lv;
    ArrayAdapter myapdapter;
    ArrayList listData;
    ImageButton imgback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_audit);
        getSupportActionBar().hide();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        lv = findViewById(R.id.lv);
        imgback= findViewById(R.id.imgback);
        listData = new ArrayList();
        myapdapter = new ArrayAdapter(LogAuditActivity.this, android.R.layout.simple_list_item_1, listData);
        lv.setAdapter(myapdapter);
        DatabaseManager dbManager = DatabaseManager.getInstance(LogAuditActivity.this);
        Cursor c = dbManager.getData();
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (c != null && c.moveToFirst()) {
            int _snipeit_inventory_number_32 = c.getColumnIndex("_snipeit_inventory_number_32");
            int asset_status = c.getColumnIndex("asset_status");
            int label_status = c.getColumnIndex("label_status");
            int location = c.getColumnIndex("location");
            int last_used = c.getColumnIndex("last_used");
            String label_status_content = "";
            if(c.getString(label_status).equals("false")){
                label_status_content = "No use";
            }
            else{label_status_content = "Good";}
            do {
                String data = c.getString(_snipeit_inventory_number_32) + "_" + label_status_content+ "_" + c.getString(asset_status) + "_" + c.getString(location)+ "_" + c.getString(last_used);
                listData.add(data);
            } while (c.moveToNext());
            c.close();
        }
        myapdapter.notifyDataSetChanged();
    }
}
