package com.example.nidecsnipeit.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.MaintenanceAdapter;
import com.example.nidecsnipeit.model.AlertDialogCallback;
import com.example.nidecsnipeit.model.GetMaintenanceParamItemModel;
import com.example.nidecsnipeit.model.MaintenanceItemModel;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utility.Common;

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
        View view = findViewById(android.R.id.content);
        // Set up action bar
        setupActionBar("Maintenance list");

        Intent intent = getIntent();
        int asset_id = intent.getIntExtra("ASSET_ID", 0);

        NetworkManager apiServices = NetworkManager.getInstance();
        GetMaintenanceParamItemModel paramItem = new GetMaintenanceParamItemModel(asset_id);
        List<MaintenanceItemModel> dataList = new ArrayList<>();
        Common.showProgressDialog(this, "Loading...");

        RecyclerView recyclerView = findViewById(R.id.recycler_maintenance);
        recyclerView.setLayoutManager(new LinearLayoutManager(MaintenanceListActivity.this));
        adapter = new MaintenanceAdapter(MaintenanceListActivity.this, dataList, recyclerView);
        recyclerView.setAdapter(adapter);

        // handle event delete item
        adapter.setOnDeleteItemListener(position -> {
            Common.showCustomAlertDialog(MaintenanceListActivity.this, "Delete maintenance", "Are you sure you want to delete this maintenance? This operation cannot be undone", true, new AlertDialogCallback() {
                @Override
                public void onPositiveButtonClick() {
                    Common.showProgressDialog(MaintenanceListActivity.this, "Deleting...");
                    apiServices.deleteMaintenanceItem(dataList.get(position).getId(), new NetworkResponseListener<JSONObject>() {
                        @Override
                        public void onResult(JSONObject object) {
                            Common.hideProgressDialog();
                            try {
                                if (object.has("status") && object.get("status").equals("error")) {
                                    Common.showCustomSnackBar(view, object.get("messages").toString(), Common.SnackBarType.ERROR, null);
                                } else {
                                    dataList.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    Common.showCustomSnackBar(view, "The asset maintenance was deleted successfully", Common.SnackBarType.SUCCESS, null);

                                    // show text if data list is empty
                                    if (dataList.isEmpty()) {
                                        TextView textAlert = findViewById(R.id.no_maintenance);
                                        textAlert.setVisibility(View.VISIBLE);
                                    }
                                }
                            } catch (JSONException e) {
                                Common.showCustomSnackBar(view, e.getMessage(), Common.SnackBarType.ERROR, null);
                            }
                        }
                    }, new NetworkResponseErrorListener() {
                        @Override
                        public void onErrorResult(Exception error) {
                            Common.hideProgressDialog();
                            Common.showCustomSnackBar(view, error.getMessage(), Common.SnackBarType.ERROR, null);
                        }
                    });
                }
                @Override
                public void onNegativeButtonClick() {
                    Common.hideProgressDialog();
                }
            });

        });

        // get and show maintenance list
        apiServices.getMaintenanceList(paramItem, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                try {
                    if (object.has("rows") && ((JSONArray) object.get("rows")).length() > 0) {
                        JSONArray objectArray = (JSONArray) object.get("rows");

                        for (int i = 0; i < objectArray.length(); i++) {
                            JSONObject item = objectArray.getJSONObject(i);
                            int id = item.getInt("id");
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

                            dataList.add(new MaintenanceItemModel(id, title, asset_id, supplier_id, supplier_name, asset_maintenance_type, start_date));
                        }

                        // map item list to view
                        adapter.notifyItemRangeInserted(0, dataList.size());
                        Common.hideProgressDialog();
                    } else {
                        TextView textAlert = findViewById(R.id.no_maintenance);
                        textAlert.setVisibility(View.VISIBLE);
                        Common.hideProgressDialog();
                    }
                } catch (JSONException e) {
                    Common.hideProgressDialog();
                    Common.showCustomSnackBar(view, e.getMessage(), Common.SnackBarType.ERROR, null);
                }
            }
        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {
                Common.hideProgressDialog();
            }
        });

        ImageButton addButton = findViewById(R.id.add_new_mtn_btn);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaintenanceListActivity.this, MaintenanceAddActivity.class);
                intent.putExtra("ASSET_ID", asset_id);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(MaintenanceListActivity.this, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(MaintenanceListActivity.this, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}