package com.example.nidecsnipeit;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.nidecsnipeit.adapter.MaintenanceAdapter;
import com.example.nidecsnipeit.model.GetMaintenanceParamItemModel;
import com.example.nidecsnipeit.model.MaintenanceItemModel;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utils.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MaintenanceListActivity extends BaseActivity {
    private MaintenanceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_list);
        // Set up action bar
        setupActionBar("Device details");

        Intent intent = getIntent();
        int asset_id = intent.getIntExtra("ASSET_ID", 0);
        NetworkManager apiServices = NetworkManager.getInstance();
        GetMaintenanceParamItemModel paramItem = new GetMaintenanceParamItemModel(asset_id);
        List<MaintenanceItemModel> dataList = new ArrayList<>();
        Common.showProgressDialog(this, "Loading...");

        apiServices.getMaintenanceList(paramItem, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                try {
                    if (object.has("rows") && ((JSONArray) object.get("rows")).length() > 0) {
                        JSONArray objectArray = (JSONArray) object.get("rows");

                        for (int i = 0; i < objectArray.length(); i++) {
                            JSONObject item = objectArray.getJSONObject(i);
                            String asset_maintenance_type = item.getString("asset_maintenance_type");
                            String start_date;
                            if (((JSONObject) item.get("start_date")).has("date")) {
                                start_date = ((JSONObject) item.get("start_date")).getString("date");
                            } else {
                                start_date = ((JSONObject) item.get("start_date")).getString("datetime");
                            }

                            String title = item.get("title").toString();
                            int supplier_id = ((JSONObject) item.get("supplier")).getInt("id");
                            String supplier_name = ((JSONObject) item.get("supplier")).getString("name");
                            Float cost = item.getString("cost").equals("null") ? null : Float.parseFloat(item.getString("cost"));
                            String notes = item.getString("notes").equals("null") ? null : item.getString("notes");
                            String completion_date = item.getString("completion_date").equals("null") ? null : item.getString("completion_date");

                            dataList.add(new MaintenanceItemModel(title, asset_id, supplier_id, supplier_name, null, cost, notes, asset_maintenance_type, start_date, completion_date));
                        }

                        // map item list to view
                        RecyclerView recyclerView = findViewById(R.id.recycler_maintenance);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MaintenanceListActivity.this));
                        adapter = new MaintenanceAdapter(MaintenanceListActivity.this, dataList, recyclerView);
                        recyclerView.setAdapter(adapter);
                        Common.hideProgressDialog();
                    }
                } catch (JSONException e) {
                    Common.hideProgressDialog();
                    throw new RuntimeException(e);
                }
            }
        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {
                Common.hideProgressDialog();
            }
        });
    }
}