package com.example.nidecsnipeit.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.CustomItemAdapter;
import com.example.nidecsnipeit.model.AlertDialogCallback;
import com.example.nidecsnipeit.model.DetailFieldModel;
import com.example.nidecsnipeit.model.ListItemModel;
import com.example.nidecsnipeit.model.MaintenanceItemModel;
import com.example.nidecsnipeit.model.SnackbarCallback;
import com.example.nidecsnipeit.model.SpinnerItemModel;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utility.Common;
import com.example.nidecsnipeit.utility.QRScannerHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MaintenanceAddActivity extends BaseActivity {
    private CustomItemAdapter adapter;
    private boolean isNewMaintenance;
    private View rootView;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_add);
        this.rootView = findViewById(android.R.id.content);

        Common.showProgressDialog(this, "Loading...");

        MyApplication MyApp = (MyApplication) getApplication();
        NetworkManager apiServices = NetworkManager.getInstance(this);

        List<SpinnerItemModel> maintenanceTypes = new ArrayList<>(); // {"Maintenance", "Repair", "PAT Test", "Upgrade", "Hardware Support", "Software Support"};
        maintenanceTypes.add(new SpinnerItemModel("0", "Maintenance"));
        maintenanceTypes.add(new SpinnerItemModel("1", "Repair"));
        maintenanceTypes.add(new SpinnerItemModel("2", "PAT Test"));
        maintenanceTypes.add(new SpinnerItemModel("3", "Upgrade"));
        maintenanceTypes.add(new SpinnerItemModel("4", "Hardware Support"));
        maintenanceTypes.add(new SpinnerItemModel("5", "Software Support"));

        List<ListItemModel> dataList = new ArrayList<>();

        // Get detail data
        Intent intent = getIntent();
        int asset_id = intent.getIntExtra("ASSET_ID", 0);
        MaintenanceItemModel maintenanceInfo = (MaintenanceItemModel) intent.getSerializableExtra("MAINTENANCE_INFO");
        Button maintenanceButton = findViewById(R.id.maintenance_add_btn);
        DatePicker datePicker = findViewById(R.id.date_picker);

        if (maintenanceInfo != null) {
            // Set up action bar
            setupActionBar("Update maintenance");
            maintenanceButton.setText("UPDATE MAINTENANCE");
            isNewMaintenance = false;

            // init date
            String[] parts = maintenanceInfo.getStartDate().split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) - 1;
            int day = Integer.parseInt(parts[2]);
            datePicker.init(year, month, day, null);
        } else {
            setupActionBar("Add maintenance");
            maintenanceButton.setText("SAVE MAINTENANCE");
            isNewMaintenance = true;
        }

        apiServices.getSupplierList(new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                try {
                    List<SpinnerItemModel> supplierList = Common.convertArrayJsonToListIdName(object.getJSONArray("rows"));
                    if (maintenanceInfo != null) {
                        // UPDATE event
                        dataList.add(new ListItemModel("Maintenance type", maintenanceInfo.getAssetMaintenanceType(), ListItemModel.Mode.DROPDOWN, maintenanceTypes));
                        dataList.add(new ListItemModel("Supplier", maintenanceInfo.getSupplierName(), ListItemModel.Mode.DROPDOWN, supplierList));
                        dataList.add(new ListItemModel("Title", maintenanceInfo.getTitle(), ListItemModel.Mode.EDIT_TEXT));
                    } else {
                        // NEW event
                        dataList.add(new ListItemModel("Maintenance type", "", ListItemModel.Mode.DROPDOWN, maintenanceTypes));
                        dataList.add(new ListItemModel("Supplier", "", ListItemModel.Mode.DROPDOWN, supplierList));
                        dataList.add(new ListItemModel("Title", "", ListItemModel.Mode.EDIT_TEXT));
                    }

                    // map item list to view
                    RecyclerView recyclerView = findViewById(R.id.recycler_maintenance_add);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MaintenanceAddActivity.this));
                    adapter = new CustomItemAdapter(MaintenanceAddActivity.this, dataList, recyclerView);
                    recyclerView.setAdapter(adapter);
                    Common.hideProgressDialog();
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

        // handle logic for update or add button
        maintenanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> valuesMap = adapter.getAllValuesByTitle();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                String selectedDate = year + "-" + (month + 1) + "-" + day;
                String maintenanceType = valuesMap.get("asset_maintenance_type");
                int supplierId = Integer.parseInt(Objects.requireNonNull(valuesMap.get("supplier")));
                String title = valuesMap.get("title");

                if (title == null || title.equals("")) {
                    Common.showCustomSnackBar(rootView, "The title is mandatory", Common.SnackBarType.ERROR, null);
                } else {
                    // set data for param
                    if (!isNewMaintenance) {
                        // call API to update an existing maintenance
                        assert maintenanceInfo != null;
                        int finalMaintenanceId = maintenanceInfo.getId();
                        int asset_id = maintenanceInfo.getAssetID();
                        maintenanceInfo.setStartDate(selectedDate);
                        maintenanceInfo.setAssetMaintenanceType(maintenanceType);
                        maintenanceInfo.setSupplierID(supplierId);
                        maintenanceInfo.setTitle(title);

                        // show alert dialog to confirm update process
                        Common.showCustomAlertDialog(MaintenanceAddActivity.this, "Update maintenance",
                            "Are you sure you want to update this maintenance?", true, new AlertDialogCallback() {
                                @Override
                                public void onPositiveButtonClick() {
                                    Common.showProgressDialog(MaintenanceAddActivity.this, "Updating...");
                                    apiServices.updateMaintenanceItem(finalMaintenanceId, maintenanceInfo,
                                        new NetworkResponseListener<JSONObject>() {
                                            @Override
                                            public void onResult(JSONObject object) throws JSONException {
                                                if (object.has("status") && object.get("status").equals("error")) {
                                                    Common.showCustomSnackBar(rootView, object.getString("messages"), Common.SnackBarType.ERROR, null);
                                                    Common.hideProgressDialog();
                                                } else {
                                                    Common.hideProgressDialog();
                                                    Common.showCustomSnackBar(rootView, "Asset Maintenance edited successfully", Common.SnackBarType.SUCCESS, new SnackbarCallback() {
                                                        @Override
                                                        public void onSnackbar() {
                                                            Intent intent = new Intent(MaintenanceAddActivity.this, MaintenanceListActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            intent.putExtra("ASSET_ID", asset_id);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                }
                                            }
                                        }, new NetworkResponseErrorListener() {
                                            @Override
                                            public void onErrorResult(Exception error) {
                                                Common.hideProgressDialog();
                                                Common.showCustomSnackBar(rootView, error.getMessage(), Common.SnackBarType.ERROR, null);
                                            }
                                        }
                                    );
                                }

                                @Override
                                public void onNegativeButtonClick() {

                                }
                            });

                    }
                    else {
                        // call API to add new a maintenance
                        MaintenanceItemModel params = new MaintenanceItemModel();
                        params.setAssetID(asset_id);
                        params.setStartDate(selectedDate);
                        params.setAssetMaintenanceType(maintenanceType);
                        params.setSupplierID(supplierId);
                        params.setTitle(title);

                        // show alert dialog to confirm add process
                        Common.showCustomAlertDialog(MaintenanceAddActivity.this, "Add maintenance",
                            "Are you sure you want to add this maintenance?", true, new AlertDialogCallback() {
                                @Override
                                public void onPositiveButtonClick() {
                                    Common.showProgressDialog(MaintenanceAddActivity.this, "Saving...");
                                    apiServices.createMaintenanceItem(params,
                                        new NetworkResponseListener<JSONObject>() {
                                            @Override
                                            public void onResult(JSONObject object) throws JSONException {
                                                if (object.has("status") && object.get("status").equals("error")) {
                                                    Common.showCustomSnackBar(rootView, object.getString("messages"), Common.SnackBarType.ERROR, null);
                                                    Common.hideProgressDialog();
                                                } else {
                                                    Common.hideProgressDialog();
                                                    Common.showCustomSnackBar(rootView, "Asset Maintenance edited successfully", Common.SnackBarType.SUCCESS, new SnackbarCallback() {
                                                        @Override
                                                        public void onSnackbar() {
                                                            Intent intent = new Intent(MaintenanceAddActivity.this, MaintenanceListActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            intent.putExtra("ASSET_ID", asset_id);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                }
                                            }
                                        }, new NetworkResponseErrorListener() {
                                            @Override
                                            public void onErrorResult(Exception error) {
                                                Common.hideProgressDialog();
                                                Common.showCustomSnackBar(rootView, error.getMessage(), Common.SnackBarType.ERROR, null);
                                            }
                                        }
                                    );
                                }

                                @Override
                                public void onNegativeButtonClick() {

                                }
                            });
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Get QR scanned value
        String scannedValue = QRScannerHelper.processScanResult(requestCode, resultCode, data);

        if (scannedValue != null) {
            // Update dropdown selection following position
            adapter.updateDropdownSelection(adapter.getCurrentPosition(), scannedValue);
        }
    }
}