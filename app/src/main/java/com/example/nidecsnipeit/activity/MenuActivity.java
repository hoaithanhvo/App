package com.example.nidecsnipeit.activity;

import static com.example.nidecsnipeit.activity.SearchActivity.PERMISSION_REQUEST_CAMERA;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.service.TokenValidationService;
import com.example.nidecsnipeit.utility.Common;
import com.example.nidecsnipeit.utility.QRScannerHelper;

import org.json.JSONException;
import org.json.JSONObject;


public class MenuActivity extends AppCompatActivity  {
    private View rootView;
    private EditText inputSearch;
    private LinearLayout checkOutBtn , checkInBtn,maintenanceBtn,settingBtn,auditBtn,auditRFID,importAssetBtn,transferBtn,productDeliveryBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication MyApp = (MyApplication) getApplication();
        if (MyApp.getDisplayedFieldsJsonString().equals("")) {
            Common.tokenInvalid(this);
        }
        if (MyApp.isFirstRun()) {
            // if app first run, redirect to login screen
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            Intent serviceIntent = new Intent(this, TokenValidationService.class);
            startService(serviceIntent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        rootView = findViewById(android.R.id.content);
        // Get button
        transferBtn = findViewById(R.id.transfer_btn);
        checkOutBtn = findViewById(R.id.checkout_btn);
        checkInBtn = findViewById(R.id.checkin_btn);
        maintenanceBtn = findViewById(R.id.maintenance_btn);
        settingBtn = findViewById(R.id.setting_btn);
        auditBtn = findViewById(R.id.audit_btn);
        auditRFID = findViewById(R.id.auditRFID);
        importAssetBtn = findViewById(R.id.importAsset_btn);
        inputSearch = findViewById(R.id.input_search);
        productDeliveryBtn = findViewById(R.id.product_delivery_btn);
        ImageButton searchButton = findViewById(R.id.search_img_btn);
        ImageButton removeTextButton = findViewById(R.id.remove_text);
        ImageButton qrScannerBtn = findViewById(R.id.qr_scanner_btn);
        transferBtn.setOnClickListener(createButtonClickListener(DetailActivity.TRANSFER_MODE));
        checkInBtn.setOnClickListener(createButtonClickListener(DetailActivity.CHECK_IN_MODE));
        checkOutBtn.setOnClickListener(createButtonClickListener(DetailActivity.CHECK_OUT_MODE));
        maintenanceBtn.setOnClickListener(createButtonClickListener(DetailActivity.MAINTENANCE_MODE));
        settingBtn.setOnClickListener(createButtonClickListener(DetailActivity.SETTING_MODE));
        auditBtn.setOnClickListener(createButtonClickListener((DetailActivity.AUDIT_MODE)));
        auditRFID.setOnClickListener(createButtonClickListener((DetailActivity.AUDIT_RFID_MODE)));
        importAssetBtn.setOnClickListener(createButtonClickListener(DetailActivity.IMPORT_ASSET_MODE));
        productDeliveryBtn.setOnClickListener(createButtonClickListener(DetailActivity.PRODUCT_DELIVERY));
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                // disable searchButton if inputSearch is Empty
                if (s.toString().trim().isEmpty()) {
                    removeTextButton.setEnabled(false);
                    removeTextButton.setVisibility(View.GONE);
                    qrScannerBtn.setEnabled(true);
                    qrScannerBtn.setVisibility(View.VISIBLE);
                } else {
                    removeTextButton.setEnabled(true);
                    removeTextButton.setVisibility(View.VISIBLE);
                    qrScannerBtn.setEnabled(false);
                    qrScannerBtn.setVisibility(View.GONE);
                }
            }
        });
        // Set up remove text button
        removeTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isHardScanButtonPressed) {
                    String assetTag = inputSearch.getText().toString();
                    Common.hideKeyboard(MenuActivity.this, v);
                    Common.focusCursorToEnd(inputSearch);
                    redirectToDetailScreen(assetTag);
                } else {
                    inputSearch.setText("");
                    Common.focusCursorToEnd(inputSearch);
                }
            }
        });
        // Set up QR scanner button
        qrScannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isHardScanButtonPressed) {
                    String assetTag = inputSearch.getText().toString();
                    Common.hideKeyboard(MenuActivity.this, v);
                    Common.focusCursorToEnd(inputSearch);
                    redirectToDetailScreen(assetTag);
                } else {
                    if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MenuActivity.this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                    } else {
                        QRScannerHelper.initiateScan(MenuActivity.this);
                    }
                }
            }
        });
        // handle search button on keyboard
        inputSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String assetTag = v.getText().toString();
                if (!assetTag.trim().isEmpty()) {
                    Common.hideKeyboard(this, v);
                    redirectToDetailScreen(assetTag);
                    return true;
                }
            }
            return false;
        });
        // handle search button click event
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String assetTag = inputSearch.getText().toString();
                if (!assetTag.trim().isEmpty()) {
                    Common.hideKeyboard(MenuActivity.this, v);
                    redirectToDetailScreen(assetTag);
                }
            }
        });
    }
    private View.OnClickListener createButtonClickListener(final Integer mode) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick(mode);
            }
        };
    }
    private void handleButtonClick(Integer mode) {
        if (Common.isHardScanButtonPressed) {
            String assetTag = inputSearch.getText().toString();
            Common.hideKeyboard(MenuActivity.this, rootView);
            Common.focusCursorToEnd(inputSearch);
            redirectToDetailScreen(assetTag);
        } else {
            Intent intent;
            if (mode == DetailActivity.SETTING_MODE) {
                intent = new Intent(this, SettingsActivity.class);
            } else if(mode == DetailActivity.AUDIT_RFID_MODE){
                intent = new Intent(this,RFID_Activity.class);
            } else if (mode == DetailActivity.IMPORT_ASSET_MODE) {
                intent = new Intent(this, Import_AssetActivity.class);
            }else if (mode == DetailActivity.PRODUCT_DELIVERY) {
                intent = new Intent(this, Product_Delivery_Activity.class);
            }
            else {
                intent = new Intent(this, SearchActivity.class);
                intent.putExtra("mode", mode);
            }
            startActivity(intent);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String qrContent = QRScannerHelper.processScanResult(requestCode, resultCode, data);
        if (qrContent != null) {
            // Set content to EditText
            inputSearch.setText(qrContent);
            // Redirect to Detail screen if QR scan is successful
            redirectToDetailScreen(qrContent);
        } else {
            Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
        // focus to edit text
        Common.focusCursorToEnd(inputSearch);
    }
    public void redirectToDetailScreen(String qrResult) {
        NetworkManager apiServices = NetworkManager.getInstance(MenuActivity.this);
        if (qrResult.trim().isEmpty()) {
            qrResult = inputSearch.getText().toString();
        }
        Common.showProgressDialog(this, "Searching...");
        apiServices.getItemRequestByAssetTag(qrResult, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                try {
                    if (object.has("status") && object.get("status").equals("error")) {
                        Common.hideProgressDialog();
                        Common.showCustomSnackBar(rootView, object.get("messages").toString(), Common.SnackBarType.ERROR, null);
                        // focus to edit text
                        Common.focusCursorToEnd(inputSearch);
                    } else {
                        Intent intent = new Intent(MenuActivity.this, DetailActivity.class);
                        intent.putExtra("DEVICE_INFO", object.toString());
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    Common.hideProgressDialog();
                    Common.showCustomSnackBar(rootView, e.toString(), Common.SnackBarType.ERROR, null);
                    // focus to edit text
                    Common.focusCursorToEnd(inputSearch);
                }
            }
        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {

                Common.hideProgressDialog();

                if (error.getMessage() == null) {
                    Common.showCustomSnackBar(rootView, "No permission", Common.SnackBarType.ERROR, null);
                } else {
                    Common.tokenInvalid(MenuActivity.this);
                }
                // focus to edit text
                Common.focusCursorToEnd(inputSearch);
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int KEYCODE_SCAN = 10036;
        EditText editText = findViewById(R.id.input_search);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        } else if (keyCode == KEYCODE_SCAN || keyCode == KeyEvent.KEYCODE_BUTTON_R1) {
            Common.focusCursorToEnd(editText);
            Common.setHardScanButtonPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}


