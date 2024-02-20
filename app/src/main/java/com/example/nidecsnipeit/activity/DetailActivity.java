package com.example.nidecsnipeit.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.Config;
import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.CustomItemAdapter;
import com.example.nidecsnipeit.model.AlertDialogCallback;
import com.example.nidecsnipeit.model.DetailFieldModel;
import com.example.nidecsnipeit.model.ListItemModel;
import com.example.nidecsnipeit.model.SnackbarCallback;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utility.Common;
import com.example.nidecsnipeit.utility.FullNameConvert;
import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends BaseActivity {
    CustomItemAdapter adapter;
    private int mode;
    private View rootView;
    private JSONObject deviceInfo = null;

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);
        rootView = findViewById(android.R.id.content);

        // Set up action bar
        setupActionBar("Device details");

        MyApplication myApp = (MyApplication) getApplication();
        List<DetailFieldModel> fields = myApp.getDetailScreenFields();

        // Get detail data
        Intent intent = getIntent();
        String deviceInfoJson = intent.getStringExtra("DEVICE_INFO");
        mode = intent.getIntExtra("MODE", Config.CHECK_IN_MODE);
        List<ListItemModel> dataList = new ArrayList<>();
        try {
            assert deviceInfoJson != null;
            deviceInfo = new JSONObject(deviceInfoJson);
            // get image to show on screen
            String imageUrl = deviceInfo.get("image").toString();

            // get values for all displayed fields
            for (DetailFieldModel field : fields) {
                String valueField = "Not defined";
                String fieldName = field.getName();
                if (deviceInfo.has(fieldName)) {
                    Object fieldObject = deviceInfo.get(fieldName);
                    String typeAssignedTo = "";
                    if (fieldObject instanceof JSONObject) {
                        if (((JSONObject) fieldObject).has("name")) {
                            valueField = StringEscapeUtils.unescapeHtml4(((JSONObject) fieldObject).getString("name"));
                            if (fieldName.equals("assigned_to")) {
                                typeAssignedTo = StringEscapeUtils.unescapeHtml4(((JSONObject) fieldObject).getString("type"));
                            }
                        }
                    } else {
                        if (!fieldObject.toString().equals("") && !fieldObject.toString().equals("null")) {
                            valueField = StringEscapeUtils.unescapeHtml4(fieldObject.toString());
                        } else if (fieldObject.toString().equals("null")) {
                            valueField = "";
                        }
                    }

                    String titleField = FullNameConvert.getFullName(fieldName);
                    ListItemModel.Mode typeField = field.getType();

                    // add new field for dataList
                    if (fieldName.equals("assigned_to")) {
                        if (!valueField.equals("")) {
                            int assignIcon = R.drawable.ic_user;
                            if (!typeAssignedTo.equals("user")) {
                                assignIcon = R.drawable.ic_location;
                            }
                            dataList.add(new ListItemModel(titleField, valueField, typeField, getDrawable(assignIcon)));
                        }
                    } else {
                        dataList.add(new ListItemModel(titleField, valueField, typeField));
                    }
                }

            }

            if (!imageUrl.equals("null")) {
                ImageView detailImage = findViewById(R.id.detail_img);
                Picasso.get().load(imageUrl).into(detailImage);
            }

        } catch (JSONException e) {
            Common.showCustomSnackBar(rootView, e.getMessage(), Common.SnackBarType.ERROR, null);
        }

        // map item list to view
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomItemAdapter(this, dataList, recyclerView);
        recyclerView.setAdapter(adapter);

        Button requestBtn = findViewById(R.id.check_in_detail);
        switch (mode) {
            case Config.CHECK_IN_MODE:
                requestBtn.setText("CHECK-IN");
                break;
            case Config.CHECK_OUT_MODE:
                requestBtn.setText("CHECKOUT (LOCATION)");
                break;
            case Config.MAINTENANCE_MODE:
                requestBtn.setText("MAINTENANCES");
                break;
        }
        requestBtn.setOnClickListener(v -> {
            try {
                handleRequestBtn(deviceInfo);
            } catch (JSONException e) {
                Common.showCustomSnackBar(rootView, e.getMessage(), Common.SnackBarType.ERROR, null);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Hide ProgressDialog after view rendered
        Common.hideProgressDialog();
    }

    private void handleRequestBtn(JSONObject details ) throws JSONException {
        NetworkManager apiServices = NetworkManager.getInstance();
        int id = (int) details.get("id");
        String asset_name = details.getString("name");
        String locationName;
        if (details.getString("location").equals("null")) {
            locationName = "";
        } else {
            locationName = details.getJSONObject("location").getString("name");
        }

        if (mode == Config.CHECK_IN_MODE) {
            // handle logic for check-in mode
            Common.showCustomAlertDialog(DetailActivity.this, "Checkin Asset",
            "Do you want to checkin this asset? It will be available for checkout", true, new AlertDialogCallback() {
                @Override
                public void onPositiveButtonClick() {
                    Common.showProgressDialog(DetailActivity.this, "Checking...");
                    apiServices.checkinItem(id, new NetworkResponseListener<JSONObject>() {
                        @Override
                        public void onResult(JSONObject object) {
                            try {
                                if (object.has("status") && object.get("status").equals("error")) {
                                    Common.hideProgressDialog();
                                    Common.showCustomSnackBar(rootView, object.get("messages").toString(), Common.SnackBarType.ERROR, null);
                                } else {
                                    Common.hideProgressDialog();
                                    Common.showCustomSnackBar(rootView, object.getString("messages"), Common.SnackBarType.SUCCESS, new SnackbarCallback() {
                                        @Override
                                        public void onSnackbar() {
                                            Intent intent = new Intent(DetailActivity.this, MenuActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                Common.hideProgressDialog();
                                Common.showCustomSnackBar(rootView, e.getMessage(), Common.SnackBarType.ERROR, null);
                            }
                        }
                    }, new NetworkResponseErrorListener() {
                        @Override
                        public void onErrorResult(Exception error) {
                            Common.hideProgressDialog();
                            Common.showCustomSnackBar(rootView, error.getMessage(), Common.SnackBarType.ERROR, null);
                        }
                    });
                }

                @Override
                public void onNegativeButtonClick() {

                }
            });
        } else if (mode == Config.CHECK_OUT_MODE) {
            // handle logic for checkout mode
            boolean checkoutAvailable = details.getBoolean("user_can_checkout");
            Intent intent = new Intent(DetailActivity.this, CheckoutActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("ASSET_ID", id);
            intent.putExtra("ASSET_NAME", asset_name);
            intent.putExtra("LOCATION_NAME", locationName);
            intent.putExtra("CHECKOUT_AVAILABLE", checkoutAvailable);
            startActivity(intent);
            finish();
        } else if (mode == Config.MAINTENANCE_MODE) {
            // handle logic for maintenance mode
            Intent intent = new Intent(DetailActivity.this, MaintenanceListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("ASSET_ID", id);
            startActivity(intent);
            finish();
        }
    }
}