package com.example.nidecsnipeit.activity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.widget.Toast;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utility.Common;
import com.example.nidecsnipeit.utility.QRScannerHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchActivity extends BaseActivity {
    static final int PERMISSION_REQUEST_CAMERA = 1;
    private int searchMode;
    private View rootView;
    private static final int REQUEST_CODE = 1;
    ImageButton imgClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        imgClear = findViewById(R.id.imgClear);
        rootView = findViewById(android.R.id.content);

        Intent intent = getIntent();
        searchMode = intent.getIntExtra("mode", DetailActivity.CHECK_IN_MODE);
        switch (searchMode) {
            case DetailActivity.TRANSFER_MODE:
                setupActionBar("Transfer search");
                break;
            case DetailActivity.CHECK_IN_MODE:
                setupActionBar("Check-in search");
                break;
            case DetailActivity.CHECK_OUT_MODE:
                setupActionBar("Checkout search");
                break;
            case DetailActivity.MAINTENANCE_MODE:
                setupActionBar("Maintenance search");
                break;
            case DetailActivity.AUDIT_MODE:
                setupActionBar("Audit search");
                break;
            case DetailActivity.AUDIT_OFFLINE_MODE:
                setupActionBar("Audit-Offline search");
                break;
            default:
                setupActionBar("Search");
                break;
        }
        // get reference EditText and Button
        EditText inputSearch = findViewById(R.id.input_search);
        inputSearch.requestFocus(); // focus to edit text
        Button searchButton = findViewById(R.id.search_btn);
        // Set up QR scanner button
        ImageButton qrScannerBtn = findViewById(R.id.qr_scanner_btn);
        qrScannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isHardScanButtonPressed) {
                    String assetTag = inputSearch.getText().toString();
                    if(assetTag.contains("**")){
                        int doubleStarIndex = assetTag.indexOf("**");
                        // Tìm vị trí của dấu * liền trước dấu **
                        int previousStarIndex = assetTag.lastIndexOf("*", doubleStarIndex - 1);

                        if (previousStarIndex != -1) {
                            // Lấy đoạn chuỗi từ dấu * trước đó cho đến dấu ** (không bao gồm dấu **)
                            assetTag = assetTag.substring(previousStarIndex + 1, doubleStarIndex);
                        }
                    }
                    if (assetTag.contains("*")) {
                        String regex = "\\*(.*?)\\*";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(assetTag);
                        if (matcher.find()) {
                            assetTag = matcher.group(1);
                        }
                    }
                    if (!assetTag.trim().isEmpty()) {
                        Common.hideKeyboard(SearchActivity.this, v);
                        redirectToDetailScreen(assetTag);
                    }
                } else {
                    if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SearchActivity.this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                    } else {
                        QRScannerHelper.initiateScan(SearchActivity.this);
                    }
                }
            }
        });
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSearch.setText("");
            }
        });
        // AUTO SEARCH WHEN HAVE *
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
                                                       String assetTag = s.toString();
                                                       if(assetTag.contains("**")){
                                                           int doubleStarIndex = assetTag.indexOf("**");
                                                           int previousStarIndex = assetTag.lastIndexOf("*", doubleStarIndex - 1);
                                                           if (previousStarIndex != -1) {
                                                               assetTag = assetTag.substring(previousStarIndex + 1, doubleStarIndex);
                                                           }
                                                           if (!assetTag.trim().isEmpty()) {
                                                               redirectToDetailScreen(assetTag);
                                                           }
                                                       }
                                                       if (assetTag.contains("*")){
                                                           String regex = "\\*(.*?)\\*";
                                                           Pattern pattern = Pattern.compile(regex);
                                                           Matcher matcher = pattern.matcher(assetTag);
                                                           if (matcher.find()){
                                                               assetTag= matcher.group(1);
                                                           }
                                                           if (!assetTag.trim().isEmpty()) {
                                                               redirectToDetailScreen(assetTag);
                                                           }
                                                       }
                                                       searchButton.setEnabled(true);
                                                       searchButton.setBackgroundTintList(ContextCompat.getColorStateList(SearchActivity.this, R.color.secondary));
                                                       searchButton.setTextColor(getColor(R.color.white));
                                                   }
                                               }
                                           }
        );
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

//         handle search button click event
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String assetTag = inputSearch.getText().toString();
                if(assetTag.contains("**")){
                    int doubleStarIndex = assetTag.indexOf("**");
                    // Tìm vị trí của dấu * liền trước dấu **
                    int previousStarIndex = assetTag.lastIndexOf("*", doubleStarIndex - 1);

                    if (previousStarIndex != -1) {
                        // Lấy đoạn chuỗi từ dấu * trước đó cho đến dấu ** (không bao gồm dấu **)
                        assetTag = assetTag.substring(previousStarIndex + 1, doubleStarIndex);
                    }
                }
                if (assetTag.contains("*")) {
                    String regex = "\\*(.*?)\\*";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(assetTag);
                    if (matcher.find()) {
                        assetTag = matcher.group(1);
                    }
                }
                if (!assetTag.trim().isEmpty()) {
                    Common.hideKeyboard(SearchActivity.this, v);
                    redirectToDetailScreen(assetTag);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        String qrContent = QRScannerHelper.processScanResult(requestCode, resultCode, data);
        EditText editText = findViewById(R.id.input_search);
        if (qrContent != null) {
            // Set content to EditText
            editText.setText(qrContent);

            // Redirect to Detail screen if QR scan is successful
            redirectToDetailScreen(qrContent);
        } else {
            Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            boolean updateTextView = data.getBooleanExtra("updateTextView", false);
            if (updateTextView) {
                editText.setText("");
            }
        }
        // focus to edit text
        Common.focusCursorToEnd(editText);
    }
    public void redirectToDetailScreen(String qrResult) {
        NetworkManager apiServices = NetworkManager.getInstance(SearchActivity.this);
        EditText editText = findViewById(R.id.input_search);

        Common.showProgressDialog(this, "Searching...");
        apiServices.getItemRequestByAssetTag(qrResult, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                try {
                    if (object.has("status") && object.get("status").equals("error")) {
                        Common.hideProgressDialog();
                        Common.showCustomSnackBar(rootView, object.get("messages").toString(), Common.SnackBarType.ERROR, null);
                        // focus to edit text
                        Common.focusCursorToEnd(editText);
                    } else {
                        boolean userCanCheckIn = !object.getBoolean("user_can_checkout");
                        if (userCanCheckIn && searchMode == DetailActivity.TRANSFER_MODE) {
                            Common.hideProgressDialog();
                            Common.showCustomAlertDialog(SearchActivity.this, null, "This asset is already checked out.", false, null);
                            // focus to edit text
                            Common.focusCursorToEnd(editText);
                        } else if (!userCanCheckIn && searchMode == DetailActivity.CHECK_IN_MODE) {
                            Common.hideProgressDialog();
                            Common.showCustomAlertDialog(SearchActivity.this, null, "This asset is already checked in.", false, null);
                            // focus to edit text
                            Common.focusCursorToEnd(editText);
                        } else if (userCanCheckIn && searchMode == DetailActivity.CHECK_OUT_MODE) {
                            Common.hideProgressDialog();
                            Common.showCustomAlertDialog(SearchActivity.this, null, "This asset is already checked out.", false, null);
                            // focus to edit text
                            Common.focusCursorToEnd(editText);

                        }
                        else {
                            Intent intent;
                            if (DetailActivity.AUDIT_MODE == searchMode) {
                                intent = new Intent(SearchActivity.this, AuditActivity.class);

                            } else {
                                intent = new Intent(SearchActivity.this, DetailActivity.class);
                            }
                            intent.putExtra("DEVICE_INFO", object.toString());
                            intent.putExtra("MODE", searchMode);
                            startActivityForResult(intent, REQUEST_CODE);
                        }
                    }
                } catch (JSONException e) {
                    Common.hideProgressDialog();
                    Common.showCustomSnackBar(rootView, e.toString(), Common.SnackBarType.ERROR, null);
                    // focus to edit text
                    Common.focusCursorToEnd(editText);
                }
            }
        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {

                Common.hideProgressDialog();

                if (error.getMessage() == null) {
                    Common.showCustomSnackBar(rootView, "No permission", Common.SnackBarType.ERROR, null);
                } else {
                    Common.tokenInvalid(SearchActivity.this);
                }
                // focus to edit text
                Common.focusCursorToEnd(editText);
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
    @Override
    protected void onResume() {
        super.onResume();
        EditText editText = findViewById(R.id.input_search);
        editText.setText(""); // Làm rỗng EditText
    }
}