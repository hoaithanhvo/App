package com.example.nidecsnipeit.activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.utility.Common;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;

public class BaseActivity extends AppCompatActivity implements EMDKManager.EMDKListener, Scanner.StatusListener, Scanner.DataListener {
    private EMDKManager emdkManager = null;
    private BarcodeManager barcodeManager = null;
    private Scanner scanner = null;
    private boolean isEMDKInitialized = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar(getString(R.string.app_name));
        initializeEmdkManager();
    }

    protected void onBackButtonPressed() {
        finish();
    }

    protected void setupActionBar(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);

            // showing the back button in action bar
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackButtonPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackButtonPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        Log.d("BaseActivity", "onResume() called in " + this.getClass().getSimpleName()); // Log name Activity
        if (!this.isEMDKInitialized) {
            initializeEmdkManager();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        deInitScanner();
        releaseEmdkManager();
        Log.d("BaseActivity", "onPause() called in " + this.getClass().getSimpleName()); // Log tên Activity
        super.onPause();
    }

    private void initBarcodeManager() {
        // Get the feature object such as BarcodeManager object for accessing the feature.
        barcodeManager =  (BarcodeManager)emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
        // Add external scanner connection listener.
        if (barcodeManager == null) {
            Toast.makeText(this, "Barcode scanning is not supported.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initScanner() {
        if (scanner == null) {
            // Get default scanner defined on the device
            scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);
            if(scanner != null) {
                // Implement the DataListener interface and pass the pointer of this object to get the data callbacks.
                scanner.addDataListener(this);

                // Implement the StatusListener interface and pass the pointer of this object to get the status callbacks.
                scanner.addStatusListener(this);

                // Hard trigger. When this mode is set, the user has to manually
                // press the trigger on the device after issuing the read call.
                // NOTE: For devices without a hard trigger, use TriggerType.SOFT_ALWAYS.
                scanner.triggerType =  Scanner.TriggerType.HARD;

                try{
                    // Enable the scanner
                    // NOTE: After calling enable(), wait for IDLE status before calling other scanner APIs
                    // such as setConfig() or read().
                    if (!scanner.isEnabled()) {
                        scanner.enable();
                    }
                } catch (ScannerException e) {
                    showMessageHardScan(e.getMessage());
                    deInitScanner();
                }
            } else {
                showMessageHardScan("Failed to initialize the scanner device.");
            }
        }
    }

    private void releaseEmdkManager() {
        this.isEMDKInitialized = false;
        if (emdkManager != null) {
            emdkManager.release();
            emdkManager = null;
        }
    }

    private void deInitScanner() {
        if (scanner != null) {
            try {
                // Release the scanner
                scanner.release();
            } catch (Exception e)   {
                showMessageHardScan(e.getMessage());
            }
            scanner = null;
        }
    }

    // Init EMDKManager
    private void initializeEmdkManager() {
        try {
            this.isEMDKInitialized = true;
            EMDKManager.getEMDKManager(getApplicationContext(), this);
            Log.d("BaseActivity", "initializeEmdkManager " + this.getClass().getSimpleName()); // Log tên Activity
        } catch(Exception e) {
            Log.d("BaseActivity", "initializeEmdkManager failed: " + e.getMessage() + " in " + this.getClass().getSimpleName()); // Log tên Activity
        }
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        // Get a reference to EMDKManager
        this.emdkManager =  emdkManager;
        Log.d("BaseActivity", "onOpened: Get a reference to EMDKManager" + " in " + this.getClass().getSimpleName()); // Log tên Activity

        // Get a  reference to the BarcodeManager feature object
        initBarcodeManager();

        // Initialize the scanner
        initScanner();
    }

    @Override
    public void onClosed() {
        if (emdkManager != null) {
            // Release all the resources
            emdkManager.release();
            emdkManager = null;
        }
    }

    @Override
    public void onStatus(StatusData statusData) {
        StatusData.ScannerStates state = statusData.getState();
        Log.d("BaseActivity", "onStatus() " + state + " called in " + this.getClass().getSimpleName()); // Log tên Activity
        switch (state) {
            case IDLE:
                try {
                    scanner.read();
                } catch (ScannerException e) {
                    showMessageHardScan(e.getMessage());
                }
                break;
            case SCANNING:
                Common.setHardScanButtonPressed(true);
                break;
            case WAITING:
                runOnUiThread(() -> {
                    Handler handler = new Handler();
                    handler.postDelayed(() -> Common.setHardScanButtonPressed(false), 1000);
                });
                break;
//                showMessageHardScan("Ready!");
            case DISABLED:
            case ERROR:
                break;
        }
    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        if (scanDataCollection != null && scanDataCollection.getResult() == ScannerResults.SUCCESS) {
            for (ScanDataCollection.ScanData data : scanDataCollection.getScanData()) {
                String qrContent = data.getData();
                runOnUiThread(() -> {
                    onScanDataReceived(qrContent);

                    Handler handler = new Handler();
                    handler.postDelayed(() -> Common.setHardScanButtonPressed(false), 1000);
                });
            }
        }
    }

    private void showMessageHardScan(String msg) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(),"Hard scan: " + msg, Toast.LENGTH_SHORT).show());
    }

    protected void onScanDataReceived(String qrContent) {
    }

}
