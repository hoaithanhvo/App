package com.example.nidecsnipeit.activity;

import static com.example.nidecsnipeit.activity.SearchActivity.PERMISSION_REQUEST_CAMERA;

import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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


public class MenuActivity extends BaseActivity {
    private View rootView;
    private EditText inputSearch;
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
        Button transferBtn = findViewById(R.id.transfer_btn);
        Button checkInBtn = findViewById(R.id.checkin_btn);
        Button checkOutBtn = findViewById(R.id.checkout_btn);
        Button maintenanceBtn = findViewById(R.id.maintenance_btn);
        Button settingBtn = findViewById(R.id.setting_btn);

        // OnClickListener for button
        transferBtn.setOnClickListener(createButtonClickListener(DetailActivity.TRANSFER_MODE));
        checkInBtn.setOnClickListener(createButtonClickListener(DetailActivity.CHECK_IN_MODE));
        checkOutBtn.setOnClickListener(createButtonClickListener(DetailActivity.CHECK_OUT_MODE));
        maintenanceBtn.setOnClickListener(createButtonClickListener(DetailActivity.MAINTENANCE_MODE));
        settingBtn.setOnClickListener(createButtonClickListener(DetailActivity.SETTING_MODE));

        // get reference EditText and Button
        inputSearch = findViewById(R.id.input_search);
        ImageButton searchButton = findViewById(R.id.search_img_btn);
        ImageButton removeTextButton = findViewById(R.id.remove_text);
        ImageButton qrScannerBtn = findViewById(R.id.qr_scanner_btn);

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
                inputSearch.setText("");
                Common.focusCursorToEnd(inputSearch);
            }
        });

        // Set up QR scanner button
        qrScannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MenuActivity.this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                } else {
                    QRScannerHelper.initiateScan(MenuActivity.this);
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
        Intent intent;
        if (mode == DetailActivity.SETTING_MODE) {
            intent = new Intent(this, SettingsActivity.class);
        } else {
            intent = new Intent(this, SearchActivity.class);
            intent.putExtra("mode", mode);
        }
        startActivity(intent);
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
    public void onScanDataReceived(String qrContent) {
        if (qrContent != null) {
            // Set content to EditText
            inputSearch.setText(qrContent);
            Common.hideKeyboard(MenuActivity.this, rootView);
            Common.focusCursorToEnd(inputSearch);
            redirectToDetailScreen(qrContent);
        }
    }

}