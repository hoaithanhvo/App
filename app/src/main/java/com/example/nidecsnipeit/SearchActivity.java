package com.example.nidecsnipeit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class SearchActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        Intent intent = getIntent();
        int searchMode = intent.getIntExtra("mode", Config.CHECK_IN_MODE);
        if (actionBar != null) {
            switch (searchMode) {
                case Config.CHECK_IN_MODE:
                    actionBar.setTitle("Check-in search");
                    break;
                case Config.CHECK_OUT_MODE:
                    actionBar.setTitle("Checkout search");
                    break;
                case Config.MAINTENANCE_MODE:
                    actionBar.setTitle("Maintenance search");
                    break;
                default:
                    actionBar.setTitle("Search");
                    break;
            }
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set up QR scanner button
        ImageButton qrScannerBtn = findViewById(R.id.qr_scanner_btn);
        qrScannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SearchActivity.this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                } else {
                    initQRCodeScanner();
                }
            }
        });

        // get reference EditText and Button
        EditText inputSearch = findViewById(R.id.input_search);
        Button searchButton = findViewById(R.id.search_btn);

        // disable searchButton if inputSearch is Empty
        if (inputSearch.getText().toString().trim().isEmpty()) {
            searchButton.setEnabled(false);
            searchButton.setBackgroundTintList(ContextCompat.getColorStateList(SearchActivity.this, R.color.disabled_bg_color));
            searchButton.setTextColor(getColor(R.color.disabled_text_color));
        } else {
            searchButton.setEnabled(true);
            searchButton.setBackgroundTintList(ContextCompat.getColorStateList(SearchActivity.this, R.color.secondary));
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

    // Init QR code scanner
    private void initQRCodeScanner() {
        // Initialize QR code scanner here
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setCameraId(0);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initQRCodeScanner();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show();
            }
        }
    }

    // get content from qr code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show();
            } else {
                // Get content from QR code
                String qrContent = result.getContents();

                // set content to EditText
                EditText editText = findViewById(R.id.input_search);
                if (editText != null) {
                    editText.setText(qrContent);
                }

                // redirect to Detail screen if qr scan successful
                this.redirectToDetailScreen(qrContent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void redirectToDetailScreen(String qrResult) {
        Toast.makeText(this, "Scanned: " + qrResult, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MaintenanceListActivity.class);
        startActivity(intent);
    }

    // this event will enable the back
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}