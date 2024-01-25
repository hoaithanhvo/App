package com.example.nidecsnipeit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utils.AlertDialogUtil;
import com.example.nidecsnipeit.utils.ProgressDialogUtil;
import com.example.nidecsnipeit.utils.QRScannerHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchActivity extends BaseActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 1;
    private int searchMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        searchMode = intent.getIntExtra("mode", Config.CHECK_IN_MODE);
        switch (searchMode) {
            case Config.CHECK_IN_MODE:
                setupActionBar("Check-in search");
                break;
            case Config.CHECK_OUT_MODE:
                setupActionBar("Checkout search");
                break;
            case Config.MAINTENANCE_MODE:
                setupActionBar("Maintenance search");
                break;
            default:
                setupActionBar("Search");
                break;
        }

        // Set up QR scanner button
        ImageButton qrScannerBtn = findViewById(R.id.qr_scanner_btn);
        qrScannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SearchActivity.this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                } else {
                    QRScannerHelper.initiateScan(SearchActivity.this);
                }
            }
        });

        // get reference EditText and Button
        EditText inputSearch = findViewById(R.id.input_search);
        Button searchButton = findViewById(R.id.search_btn);

        // disable searchButton if inputSearch is Empty
        if (inputSearch.getText().toString().trim().isEmpty()) {
            searchButton.setEnabled(false);
            searchButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.disabled_bg_color));
            searchButton.setTextColor(getColor(R.color.disabled_text_color));
        } else {
            searchButton.setEnabled(true);
            searchButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.secondary));
            searchButton.setTextColor(getColor(R.color.white));
        }

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
                       searchButton.setEnabled(false);
                       searchButton.setBackgroundTintList(ContextCompat.getColorStateList(SearchActivity.this, R.color.disabled_bg_color));
                       searchButton.setTextColor(getColor(R.color.disabled_text_color));
                   } else {
                       searchButton.setEnabled(true);
                       searchButton.setBackgroundTintList(ContextCompat.getColorStateList(SearchActivity.this, R.color.secondary));
                       searchButton.setTextColor(getColor(R.color.white));
                   }
               }
            }
        );

        inputSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String assetTag = v.getText().toString();
                if (!assetTag.trim().isEmpty()) {
                    redirectToDetailScreen(assetTag);
                    return true;
                }
            }
            return false;
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String assetTag = inputSearch.getText().toString();
                if (!assetTag.trim().isEmpty()) {
                    redirectToDetailScreen(assetTag);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String qrContent = QRScannerHelper.processScanResult(requestCode, resultCode, data);
        if (qrContent != null) {
            // Set content to EditText
            EditText editText = findViewById(R.id.input_search);
            if (editText != null) {
                editText.setText(qrContent);
            }

            // Redirect to Detail screen if QR scan is successful
            redirectToDetailScreen(qrContent);
        } else {
            Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void redirectToDetailScreen(String qrResult) {
        NetworkManager apiServices = NetworkManager.getInstance(SearchActivity.this);

        ProgressDialogUtil.showProgressDialog(this, "Searching...");
        apiServices.getItemRequestByAssetTag(qrResult, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                try {
                    if (object.has("status") && object.get("status").equals("error")) {
                        ProgressDialogUtil.hideProgressDialog();
                        Toast.makeText(SearchActivity.this, object.get("messages").toString(), Toast.LENGTH_LONG).show();
                    } else {
                        String statusDeploy = ((JSONObject)object.get("status_label")).get("status_meta").toString();
                        if (statusDeploy.equals("deployed") && searchMode == Config.CHECK_IN_MODE) {
                            ProgressDialogUtil.hideProgressDialog();
                            AlertDialogUtil.showAlertDialog(SearchActivity.this, null, "This asset is already checked in");
                        } else if (statusDeploy.equals("deployable") && searchMode == Config.CHECK_OUT_MODE) {
                            ProgressDialogUtil.hideProgressDialog();
                            AlertDialogUtil.showAlertDialog(SearchActivity.this, null, "This asset is already checked out");
                        } else {
                            Intent intent = new Intent(SearchActivity.this, DeviceDetails.class);
                            intent.putExtra("DEVICE_INFO", object.toString());
                            intent.putExtra("MODE", searchMode);
                            startActivity(intent);
                        }
                    }
                } catch (JSONException e) {
                    ProgressDialogUtil.hideProgressDialog();
                    throw new RuntimeException(e);
                }
            }
        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {
                ProgressDialogUtil.hideProgressDialog();
                Toast.makeText(SearchActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}