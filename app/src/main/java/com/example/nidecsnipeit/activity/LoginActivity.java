package com.example.nidecsnipeit.activity;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.network.model.AlertDialogCallback;
import com.example.nidecsnipeit.network.model.LoginItemModel;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utility.Common;
import com.example.nidecsnipeit.utility.LocaleHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;

public class LoginActivity extends BaseActivity {
    private TextInputEditText usernameEditText;
    private EditText passwordEditText;
    private ImageButton showPasswordButton;
    private NetworkManager apiServices;
    private TextView txtVersion;
    private ImageButton bnt_changelanguage;
    private SharedPreferences preferences;
    private LocaleHelper localeHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MyApplication MyApp = (MyApplication) getApplication();
        Intent intent = getIntent();
        boolean loginAgain = intent.getBooleanExtra("TOKEN_EXPIRED", false);
        apiServices = NetworkManager.getInstance(this);
        localeHelper = LocaleHelper.getInstance(this);
        if (loginAgain) {
            Common.showCustomAlertDialog(this, null, "Your session has expired. Please log in again.", false, new AlertDialogCallback() {
                @Override
                public void onPositiveButtonClick() {
                }

                @Override
                public void onNegativeButtonClick() {
                }
            });
        }
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        showPasswordButton = findViewById(R.id.show_password_button);
        txtVersion = findViewById(R.id.txtVersion);
        bnt_changelanguage = findViewById(R.id.bnt_changelanguage);
        preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // disable showPasswordButton if passwordEditText is Empty
                if (s.toString().trim().isEmpty()) {
                    showPasswordButton.setVisibility(View.GONE);
                } else {
                    showPasswordButton.setVisibility(View.VISIBLE);
                }
            }
        });
        // show or hide password
        showPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
        Button buttonLogin = findViewById(R.id.login_button);
        Button buttonServerSetting = findViewById(R.id.configure_server_button);
//        Button buttonDownload = findViewById(R.id.bntdownload);
        buttonServerSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ConfigureServerActivity.class);
                startActivity(intent);
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset instance network
                NetworkManager.resetInstance();

                String userName = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                LoginItemModel loginItem = new LoginItemModel(userName, password);

                if (userName.isEmpty() || password.isEmpty()) {
                    Common.showCustomSnackBar(v, "Please enter username and password.", Common.SnackBarType.ERROR, null);
                } else {
                    Common.showProgressDialog(LoginActivity.this, "Login...");
                    // proceed login
                    apiServices.login(loginItem, new NetworkResponseListener<JSONObject>() {
                        @Override
                        public void onResult(JSONObject object) {
                            try {
                                if (object.getString("status").equals("error")) {
                                    Common.hideProgressDialog();
                                    Common.showCustomSnackBar(v, object.getString("messages"), Common.SnackBarType.ERROR, null);
                                } else {
                                    // reset instance network
                                    NetworkManager.resetInstance();

                                    JSONObject dataToken = object.getJSONObject("payload");
                                    String accessToken = dataToken.getString("token");
                                    String idToken = dataToken.getString("id");
                                    String userFullName = dataToken.getString("user_full_name");
                                    boolean isAdmin = dataToken.getBoolean("is_admin");
                                    // save token to local storage
                                    MyApp.setLoginInfo(idToken, accessToken, userFullName, isAdmin);

                                    // load data setting
                                    NetworkManager apiServices = NetworkManager.getInstance(LoginActivity.this);
                                    apiServices.getFieldsAllCategory(new NetworkResponseListener<JSONObject>() {
                                        @Override
                                        public void onResult(JSONObject object) {
                                            // Convert JSON to String to save shared preferences
                                            String jsonString = null;
                                            try {
                                                jsonString = object.getString("payload");
                                            } catch (JSONException e) {
                                                Common.showCustomSnackBar(v, e.getMessage(), Common.SnackBarType.ERROR, null);
                                            }
                                            MyApp.setDisplayedFields(jsonString);

                                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                            startActivity(intent);
                                            finish();
                                            Common.hideProgressDialog();
                                        }
                                    }, new NetworkResponseErrorListener() {
                                        @Override
                                        public void onErrorResult(Exception error) {
                                            MyApp.resetLoginInfo();
                                            Common.hideProgressDialog();
                                            Common.showCustomSnackBar(v, error.getMessage(), Common.SnackBarType.ERROR, null);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                Common.showCustomSnackBar(v, e.getMessage(), Common.SnackBarType.ERROR, null);
                            }

                        }
                    }, new NetworkResponseErrorListener() {
                        @Override
                        public void onErrorResult(Exception error) {
                            Common.hideProgressDialog();
                            if (error.getMessage() == null) {
                                Common.showCustomSnackBar(v, "Failed to connect to server", Common.SnackBarType.ERROR, null);
                            } else {
                                Common.showCustomSnackBar(v, error.getMessage(), Common.SnackBarType.ERROR, null);
                            }
                        }
                    });
                }
            }
        });
        //checkVersionApp();
        bnt_changelanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localeHelper.applyLanguage(LoginActivity.this);
            }
        });
        updateLanguageButton();

    }
    public void UpdateVersion()
    {
        apiServices.getAPK(new NetworkResponseListener<byte[]>() {
            @Override
            public void onResult(byte[] object) {
                File apkFile = saveAPKFile(object);
                installApk(apkFile);
            }
        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {
                Toast.makeText(LoginActivity.this,"Call API fail: "+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private File saveAPKFile(byte[] apkData) {
        File apkFile = new File(getExternalFilesDir(null), "downloaded_app.apk");
        try (FileOutputStream fos = new FileOutputStream(apkFile)) {
            fos.write(apkData);
            Common.hideProgressDialog();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apkFile;
    }

    private void installApk(File apkFile) {
        try {
            if (apkFile.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri apkUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", apkFile);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    Uri apkUri = Uri.fromFile(apkFile);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intent);
            } else {
                Toast.makeText(this, "APK file not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            Toast.makeText(this, "Error opening APK file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void togglePasswordVisibility() {
        if (passwordEditText.getInputType() != InputType.TYPE_CLASS_TEXT) {
            // show password
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            showPasswordButton.setImageResource(R.drawable.ic_hide);
        } else {
            // hide password
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            showPasswordButton.setImageResource(R.drawable.ic_unhide);
        }
        passwordEditText.setSelection(passwordEditText.getText().length());
    }

    private void checkVersionApp(){
        apiServices.getVersion(new NetworkResponseListener<String>() {
            @Override
            public void onResult(String object) {
                PackageInfo packageInfo = null;
                try {
                    packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    if (!object.contains(packageInfo.versionName.trim())){
                        Common.showProgressDialog(LoginActivity.this,"Đang tải file cập nhật vui lòng đợi...");
                        UpdateVersion();
                        packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    };
                    txtVersion.setText(packageInfo.versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {
                Toast.makeText(LoginActivity.this,"Lỗi:" +error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
    private void updateLanguageButton() {
        String selectedFlag = preferences.getString("SelectFlagImage", "img_flag_uk");
        int flagDrawableId = getResources().getIdentifier(selectedFlag, "drawable", getPackageName());
        bnt_changelanguage.setImageResource(flagDrawableId);  // Cập nhật cờ mới cho ImageButton
    }
}
