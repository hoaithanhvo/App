package com.example.nidecsnipeit.activity;

import static com.example.nidecsnipeit.utility.Common.KEYCODE_SCAN;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.densowave.scannersdk.Common.CommManager;
import com.densowave.scannersdk.Listener.ScannerAcceptStatusListener;
import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.CustomItemAdapter;
import com.example.nidecsnipeit.network.model.AuditModel;
import com.example.nidecsnipeit.network.model.BasicItemModel;
import com.example.nidecsnipeit.network.model.GetLocationParamItemModel;
import com.example.nidecsnipeit.network.model.ListItemModel;
import com.example.nidecsnipeit.network.model.SnackbarCallback;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utility.Common;
import com.example.nidecsnipeit.utility.CustomDatePicker;
import com.example.nidecsnipeit.utility.QRScannerHelper;
import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.text.SimpleDateFormat;

public class AuditActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    public static final int CHECK_OUT_MODE = 2;
    public static final int MAINTENANCE_MODE = 3;
    public static final int SETTING_MODE = 4;
    public static final int TRANSFER_MODE = 5;
    public static final int AUDIT_MODE = 6;
    private CustomItemAdapter adapter;
    private int mode;
    private View rootView;
    private JSONObject deviceInfo = null;
    Button requestBtn , bntdatePickerButton;
    private NetworkManager apiServices;
    private RadioGroup radioGroupLabel;
    private Spinner spinnerGroupAsset;
    private String labelStatus;
    private String assetStatus;
    private EditText locationInput;
    private AutoCompleteTextView autoCompleteTextView;
    private List<BasicItemModel> locationList ;
    LinearLayout lnDatePicker;
    private  String companyId;
    private  Date lastAuditDate;
    private  Date startAuditDate;
    private  Date endAuditDate;

    ArrayAdapter<String> adapterlocation;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_audit);
        rootView = findViewById(android.R.id.content);
        requestBtn = findViewById(R.id.bntAudit);
        radioGroupLabel = findViewById(R.id.radioGroupLabel);
        spinnerGroupAsset = findViewById(R.id.spinnerAssetStatus);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        bntdatePickerButton = findViewById(R.id.bntdatePickerButton);
        lnDatePicker=findViewById(R.id.lnDatePicker);
        bntdatePickerButton.setText(CustomDatePicker.getTodaysDate());
        apiServices = NetworkManager.getInstance(this);
        setupActionBar(R.string.device_details);
        MyApplication myApp = (MyApplication) getApplication();
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(
                this, R.array.asset_status_array,
                R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroupAsset.setAdapter(arrayAdapter);
        // Register event to spinner
        spinnerGroupAsset.setOnItemSelectedListener(this);
        spinnerGroupAsset.setSelection(0);
        // Get detail data
        Intent intent = getIntent();
        String deviceInfoJson = intent.getStringExtra("DEVICE_INFO");
        mode = intent.getIntExtra("MODE", -1);
        List<ListItemModel> dataList = new ArrayList<>();
        try {
            assert deviceInfoJson != null;
            deviceInfo = new JSONObject(deviceInfoJson);
            JSONObject customFields = deviceInfo.getJSONObject("custom_fields");
            String imageUrl = deviceInfo.get("image").toString();
            String categoryId = deviceInfo.getJSONObject("category").getString("id");
            companyId = deviceInfo.getJSONObject("company").getString("id");
            String start = myApp.getSTART_AUDIT();
            String end = myApp.getEND_AUDIT();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try{
                if(!deviceInfo.isNull("last_audit_date"))
                {
                    String getLastUsed = deviceInfo.getJSONObject("last_audit_date").getString("datetime");
                    lastAuditDate=dateFormat.parse(getLastUsed);
                    startAuditDate = dateFormat.parse("2024-08-10 23:59:59");
                    endAuditDate = dateFormat.parse("2024-11-30 23:59:59");
                    if(!lastAuditDate.before(startAuditDate) && !lastAuditDate.after(endAuditDate)){
                        Toast.makeText(AuditActivity.this, "Tài sản đã được kiểm kê trong đợt này", Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (Exception ex) {
                Toast.makeText(AuditActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
            }

            String displayedFieldsString = myApp.getDisplayedFieldsJsonString();
            JSONObject displayedFieldObject = new JSONObject(displayedFieldsString);
            JSONArray displayedFields = displayedFieldObject.getJSONArray(categoryId);
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
        requestBtn.setText("AUDIT");

        if (radioGroupLabel.getCheckedRadioButtonId() == R.id.rdoLabelGood) {
            lnDatePicker.setVisibility(View.GONE);
        } else if (radioGroupLabel.getCheckedRadioButtonId() == R.id.rdoLabelNo) {
            lnDatePicker.setVisibility(View.VISIBLE);
        }
        radioGroupLabel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.rdoLabelNo){
                    lnDatePicker.setVisibility(View.VISIBLE);
                }else{
                    lnDatePicker.setVisibility(View.GONE);
                }
            }
        });
        bntdatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDatePicker.showDatePickerDialog(AuditActivity.this, new CustomDatePicker.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(int day, int month, int year) {
                        String selectedDate = String.format("%02d-%02d-%d",day,month,year);
                        bntdatePickerButton.setText(selectedDate);
                    }
                });
            }
        });

        // Thanh add filter location
        GetLocationParamItemModel locationItems = new GetLocationParamItemModel(companyId);
        RecyclerView recyclerView = findViewById(R.id.lv);
        recyclerView.setLayoutManager(new LinearLayoutManager(AuditActivity.this));
        adapter = new CustomItemAdapter(AuditActivity.this, dataList, recyclerView);
        recyclerView.setAdapter(adapter);
        autoCompleteTextView.setEnabled(false);
        apiServices.getLocationsList(locationItems, new NetworkResponseListener<JSONObject>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResult(JSONObject object) {
                try {
                    if (object.has("status") && object.get("status").equals("error")) {
                        Common.showCustomSnackBar(rootView, object.getString("messages"), Common.SnackBarType.ERROR, null);
                    } else {
                         locationList = Common.convertArrayJsonToListIdName(object.getJSONArray("payload"));
                        autoCompleteTextView.setEnabled(true);
                    }
                    Common.hideProgressDialog();
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
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Inventory_Number = "";
                String Asset_tag = "";
                int selectedIdLabel = radioGroupLabel.getCheckedRadioButtonId();
                RadioButton radselectLabel = findViewById(selectedIdLabel);
                String LabelStatus = radselectLabel.getText().toString();
                String AssetStatus = spinnerGroupAsset.getSelectedItem().toString();
                String LocationSelected = autoCompleteTextView.getText().toString();
                String lastUsed = (String) bntdatePickerButton.getText();
                String Location_Id = getIdByName(LocationSelected);

                if(Location_Id == null || Location_Id.isEmpty()){
                    Toast.makeText(AuditActivity.this, "Vị trí không tồn tại. Vui lòng chọn lại", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (LabelStatus.equals("Good")) {
                    labelStatus = "true";
                    lastUsed="";
                } else {
                    labelStatus = "false";
                    String receive = lastUsed;
                }
                if (AssetStatus.equals("A: Available to use")) {
                    assetStatus = "A";
                } else if (AssetStatus.equals("B: Available to use after repairing")) {
                    assetStatus = "B";
                } else if (AssetStatus.equals("C: Unable to use or out-of-date")) {
                    assetStatus = "C";
                } else {
                    Toast.makeText(AuditActivity.this, "Please choose asset status", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (ListItemModel item : dataList) {
                    Inventory_Number = item.getValueByTitle(dataList, "Inventory Number");
                    Asset_tag = item.getValueByTitle(dataList, "Asset Tag");
                }

                AuditModel auditModel = new AuditModel(Inventory_Number, Asset_tag, labelStatus, assetStatus, Location_Id, lastUsed);
                Common.showProgressDialog(AuditActivity.this, "Audit...");
                handleAudit(auditModel);
            }
        });
        try{
            autoCompleteTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterData(s.toString());
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }catch (Exception ex){
            Toast.makeText(AuditActivity.this, "Chưa Load Location hoàn tất", Toast.LENGTH_SHORT).show();
        }
    }
    //    Handle spinner items
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
    @Override
    protected void onResume() {
        super.onResume();
        Common.hideProgressDialog();
    }
    public void handleAudit(AuditModel params) {
        apiServices.createAudit(params, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                try {
                    if (object.has("status") && object.get("status").equals("error")) {
                        Common.hideProgressDialog();
                        Common.showCustomSnackBar(rootView, object.getString("messages"), Common.SnackBarType.ERROR, null);
                    } else {
                        Common.hideProgressDialog();
                        String messageSuccessful = "Asset audit out successfully.";
                        if (mode == AUDIT_MODE) {
                                    messageSuccessful = "Asset audit in successfully.";
                                }
                                Common.showCustomSnackBar(rootView, messageSuccessful, Common.SnackBarType.SUCCESS, new SnackbarCallback() {
                                    @Override
                                    public void onSnackbar() {
                                        Intent resultIntent = new Intent();
                                        resultIntent.putExtra("updateTextView", true);
                                        setResult(RESULT_OK, resultIntent);
                                        finish();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            Common.showCustomSnackBar(rootView, e.getMessage(), Common.SnackBarType.ERROR, null);
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
    private void filterData(String query) {
        ArrayList<String> filteredList = new ArrayList<>();
        for (BasicItemModel item : locationList) {
            String itemLocation = item.getName().toLowerCase();
            if (itemLocation.contains(query.toLowerCase())) {
                filteredList.add(item.getName());
            }
        }
        if (filteredList != null && !filteredList.isEmpty()) {
            adapterlocation = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, filteredList);
            autoCompleteTextView.setAdapter(adapterlocation);
            autoCompleteTextView.showDropDown();
        }
    }
    public String getIdByName(String name) {
        for (BasicItemModel item : locationList) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item.getId();
            }
        }
        return null; 
    }
}