package com.example.nidecsnipeit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.adapter.CustomRecyclerAdapter;
import com.example.nidecsnipeit.model.CheckinItemModel;
import com.example.nidecsnipeit.model.DetailFieldModel;
import com.example.nidecsnipeit.model.ListItemModel;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utils.FullNameConvert;
import com.example.nidecsnipeit.utils.ProgressDialogUtil;
import com.example.nidecsnipeit.utils.QRScannerHelper;
import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DeviceDetails extends BaseActivity {
    CustomRecyclerAdapter adapter;
    private int mode;

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);
        // Set up action bar
        setupActionBar("Device details");

        MyApplication myApp = (MyApplication) getApplication();
        List<DetailFieldModel> fields = myApp.getDetailScreenFields();

        // Get detail data
        Intent intent = getIntent();
        String deviceInfoJson = intent.getStringExtra("DEVICE_INFO");
        mode = intent.getIntExtra("MODE", Config.CHECK_IN_MODE);
        List<ListItemModel> dataList = new ArrayList<>();
        JSONObject deviceInfo;
        try {
            assert deviceInfoJson != null;
            deviceInfo = new JSONObject(deviceInfoJson);
            // get image to show on screen
            String imageUrl = deviceInfo.get("image").toString();

            // get values for all displayed fields
            for (DetailFieldModel field : fields) {
                String valueField = "Not defined";
                if (deviceInfo.has(field.getName())) {
                    Object fieldValue = deviceInfo.get(field.getName());
                    if (fieldValue instanceof JSONObject) {
                        if (((JSONObject) fieldValue).has("name")) {
                            valueField = StringEscapeUtils.unescapeHtml4(((JSONObject) fieldValue).getString("name"));
                        }
                    } else {
                        if (!fieldValue.toString().equals("")) {
                            valueField = StringEscapeUtils.unescapeHtml4(fieldValue.toString());
                        }
                    }
                }
                String titleField = FullNameConvert.getFullName(field.getName());
                ListItemModel.Mode typeField = field.getType();

                // add new field for dataList
                if (field.getName().equals("location")) {
                    dataList.add(new ListItemModel(titleField, valueField, typeField, getDrawable(R.drawable.ic_location)));
                } else {
                    dataList.add(new ListItemModel(titleField, valueField, typeField));
                }
            }
            ImageView detailImage = findViewById(R.id.detail_img);
            Picasso.get().load(imageUrl).into(detailImage);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // map item list to view
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomRecyclerAdapter(this, dataList, recyclerView);
        recyclerView.setAdapter(adapter);

        // handle request button
        Button requestBtn = findViewById(R.id.check_in_detail);
        switch (mode) {
            case Config.CHECK_IN_MODE:
                requestBtn.setText("CHECK-IN");
                break;
            case Config.CHECK_OUT_MODE:
                requestBtn.setText("CHECKOUT");
                break;
            case Config.MAINTENANCE_MODE:
                requestBtn.setText("MAINTENANCES");
                break;
            default:
                setupActionBar("CHECK-IN");
                break;
        }
        requestBtn.setOnClickListener(v -> {
            try {
                handleRequestBtn(deviceInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Hide ProgressDialog after view rendered
        ProgressDialogUtil.hideProgressDialog();
    }

    private void handleRequestBtn(JSONObject details ) throws JSONException {
        NetworkManager apiServices = NetworkManager.getInstance();
        int id = (int) details.get("id");
        int status_id = (int) ((JSONObject)details.get("status_label")).get("id");
        String name = details.get("name").toString();
        String notes = details.get("notes").toString();
        String location_id = ((JSONObject)details.get("location")).get("id").toString();

        if (mode == Config.CHECK_IN_MODE) {
            apiServices.checkinItem(id, new CheckinItemModel(status_id, name, notes, location_id), new NetworkResponseListener<JSONObject>() {
                @Override
                public void onResult(JSONObject object) {
                    Toast.makeText(DeviceDetails.this, "Check-in successful", Toast.LENGTH_SHORT).show();
                    try {
                        if (object.has("status") && object.get("status").equals("error")) {
                            Toast.makeText(DeviceDetails.this, object.get("messages").toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Intent intent = new Intent(DeviceDetails.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }, new NetworkResponseErrorListener() {
                @Override
                public void onErrorResult(Exception error) {
                    Toast.makeText(DeviceDetails.this, "Check-in failed", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}