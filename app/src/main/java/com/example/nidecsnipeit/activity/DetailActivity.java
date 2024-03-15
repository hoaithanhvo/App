package com.example.nidecsnipeit.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.CustomItemAdapter;
import com.example.nidecsnipeit.model.ListItemModel;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.utility.Common;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetailActivity extends BaseActivity {
    public static final int CHECK_IN_MODE = 1;
    public static final int CHECK_OUT_MODE = 2;
    public static final int MAINTENANCE_MODE = 3;
    public static final int SETTING_MODE = 4;
    public static final int TRANSFER_MODE = 5;
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
//        List<DetailFieldModel> fields = myApp.getDetailScreenFields();

        // Get detail data
        Intent intent = getIntent();
        String deviceInfoJson = intent.getStringExtra("DEVICE_INFO");
        mode = intent.getIntExtra("MODE", -1);
        List<ListItemModel> dataList = new ArrayList<>();
        try {
            assert deviceInfoJson != null;
            deviceInfo = new JSONObject(deviceInfoJson);
            JSONObject customFields = deviceInfo.getJSONObject("custom_fields");
            // get image to show on screen
            String imageUrl = deviceInfo.get("image").toString();
            String categoryId = deviceInfo.getJSONObject("category").getString("id");

            // get displayed fields by category id
            String displayedFieldsString = myApp.getDisplayedFieldsJsonString();
            JSONObject displayedFieldObject = new JSONObject(displayedFieldsString);

            JSONArray displayedFields = displayedFieldObject.getJSONArray(categoryId);

            // get values for all displayed fields
            for (int i = 0; i < displayedFields.length(); i++) {
                JSONObject field = displayedFields.getJSONObject(i);
                String valueField = "Not defined";
                String fieldName = field.getString("db_column");
                if (deviceInfo.has(fieldName)) {
                    Object fieldObject = deviceInfo.get(fieldName);
                    String typeAssignedTo = "";
                    if (fieldObject instanceof JSONObject) {
                        if (((JSONObject) fieldObject).has("name")) {
                            valueField = StringEscapeUtils.unescapeHtml4(((JSONObject) fieldObject).getString("name"));
                            if (fieldName.equals("assigned_to")) {
                                typeAssignedTo = StringEscapeUtils.unescapeHtml4(((JSONObject) fieldObject).getString("type"));
                            }
                        } else if (((JSONObject) fieldObject).has("datetime")) {
                            valueField = ((JSONObject) fieldObject).getString("datetime");
                        }
                    } else {
                        if (!fieldObject.toString().equals("") && !fieldObject.toString().equals("null")) {
                            valueField = StringEscapeUtils.unescapeHtml4(fieldObject.toString());
                        } else {
                            valueField = "";
                        }
                    }

                    String titleFullName = field.getString("name");

                    // add new field for dataList
                    if (fieldName.equals("assigned_to")) {
                        if (!valueField.equals("")) {
                            int assignIcon = R.drawable.ic_user;
                            if (!typeAssignedTo.equals("user")) {
                                assignIcon = R.drawable.ic_location;
                            }
                            dataList.add(new ListItemModel(titleFullName, valueField, ListItemModel.Mode.TEXT, getDrawable(assignIcon)));
                        }
                    } else {
                        dataList.add(new ListItemModel(titleFullName, valueField, ListItemModel.Mode.TEXT));
                    }
                } else if (customFields.has(field.getString("name"))) {
                    String fieldCustomName = field.getString("name");
                    JSONObject customField = customFields.getJSONObject(fieldCustomName);
                    dataList.add(new ListItemModel(fieldCustomName, customField.getString("value"), ListItemModel.Mode.TEXT));
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
        requestBtn.setVisibility(View.VISIBLE);

        switch (mode) {
            case TRANSFER_MODE:
                requestBtn.setText(R.string.transfer);
                break;
            case CHECK_IN_MODE:
                requestBtn.setText(R.string.check_in);
                break;
            case CHECK_OUT_MODE:
                requestBtn.setText(R.string.check_out);
                break;
            case MAINTENANCE_MODE:
                requestBtn.setText(R.string.maintenance);
                break;
            default:
                requestBtn.setVisibility(View.GONE);
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

        if (mode == CHECK_OUT_MODE || mode == CHECK_IN_MODE || mode == TRANSFER_MODE) {
            // handle logic for checkout mode
            boolean checkoutAvailable = details.getBoolean("user_can_checkout");
            Intent intent = new Intent(DetailActivity.this, CheckoutActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (mode == CHECK_IN_MODE) {
                intent.putExtra("CHECKOUT_MODE", CheckoutActivity.CHECK_IN);
            } else if (mode == CHECK_OUT_MODE) {
                intent.putExtra("CHECKOUT_MODE", CheckoutActivity.CHECK_OUT);
            } else {
                intent.putExtra("CHECKOUT_MODE", CheckoutActivity.TRANSFER);
            }

            intent.putExtra("ASSET_ID", id);
            intent.putExtra("ASSET_NAME", asset_name);
            intent.putExtra("LOCATION_NAME", locationName);
            intent.putExtra("CHECKOUT_AVAILABLE", checkoutAvailable);
            startActivity(intent);
            finish();
        } else if (mode == MAINTENANCE_MODE) {
            // handle logic for maintenance mode
            Intent intent = new Intent(DetailActivity.this, MaintenanceListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("ASSET_ID", id);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }
}