package com.example.nidecsnipeit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.nidecsnipeit.adapter.CustomRecyclerAdapter;
import com.example.nidecsnipeit.adapter.MaintenanceAdapter;
import com.example.nidecsnipeit.model.DetailFieldModel;
import com.example.nidecsnipeit.model.ListItemModel;
import com.example.nidecsnipeit.model.MaintenanceItemModel;
import com.example.nidecsnipeit.utils.Common;
import com.example.nidecsnipeit.utils.FullNameConvert;
import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MaintenanceAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_add);

        MyApplication myApp = (MyApplication) getApplication();
        List<DetailFieldModel> fields = myApp.getDetailScreenFields();

        String[] maintenanceTypes = {"Maintenance", "Repair", "PAT Test", "Upgrade", "Hardware Support", "Software Support"};
        List<ListItemModel> dataList = new ArrayList<>();

        // Get detail data
        Intent intent = getIntent();
        String deviceInfoJson = intent.getStringExtra("MAINTENANCE_INFO");
        JSONObject deviceInfo;
        try {
            assert deviceInfoJson != null;
            deviceInfo = new JSONObject(deviceInfoJson);

            // get values for all displayed fields
//            for (DetailFieldModel field : fields) {
//                String valueField = "Not defined";
//                if (deviceInfo.has(field.getName())) {
//                    Object fieldValue = deviceInfo.get(field.getName());
//                    if (fieldValue instanceof JSONObject) {
//                        if (((JSONObject) fieldValue).has("name")) {
//                            valueField = StringEscapeUtils.unescapeHtml4(((JSONObject) fieldValue).getString("name"));
//                        }
//                    } else {
//                        if (!fieldValue.toString().equals("")) {
//                            valueField = StringEscapeUtils.unescapeHtml4(fieldValue.toString());
//                        }
//                    }
//                }
//                String titleField = FullNameConvert.getFullName(field.getName());
//                ListItemModel.Mode typeField = field.getType();
//
//                // add new field for dataList
//                dataList.add(new ListItemModel(titleField, valueField, typeField));
//            }
//            ImageView detailImage = findViewById(R.id.detail_img);
//            Picasso.get().load(imageUrl).into(detailImage);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // map item list to view
        CustomRecyclerAdapter adapter;
        RecyclerView recyclerView = findViewById(R.id.recycler_maintenance_add);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomRecyclerAdapter(this, dataList, recyclerView);
        recyclerView.setAdapter(adapter);
        Common.hideProgressDialog();
    }
}