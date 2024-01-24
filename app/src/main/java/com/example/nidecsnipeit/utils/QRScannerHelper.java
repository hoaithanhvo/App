package com.example.nidecsnipeit.utils;

import android.app.Activity;
import android.content.Intent;


import com.example.nidecsnipeit.CaptureActivityPortrait;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRScannerHelper {

    public static final int QR_SCAN_REQUEST_CODE = 2;

    public static void initiateScan(Activity activity) {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setCameraId(0);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setPrompt("Scan a QR code");
        integrator.setRequestCode(QR_SCAN_REQUEST_CODE);
        integrator.initiateScan();
    }

    public static String processScanResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == QR_SCAN_REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
            if (result.getContents() != null) {
                return result.getContents();
            }
        }
        return null;
    }
}
