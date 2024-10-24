package com.example.nidecsnipeit.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.network.model.AlertDialogCallback;
import com.example.nidecsnipeit.network.model.LoginItemModel;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utility.Common;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText;
    private EditText passwordEditText;
    private ImageButton showPasswordButton;


    //    private String filepath = "http://10.234.1.48:5000/fsdownload/gl0WU7pta/NIDEC_AMS082024V5.apk";
    private String filepath = "http://10.234.1.35:8080/B600044912/APK_ANDROID_WAREHOUSE/raw/master/APK_WAREHOUSE/mcs_android.apk";

    private URL url = null;
    private String fileName;
    private String filePath;
    private long downloadID;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MyApplication MyApp = (MyApplication) getApplication();
        filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/";
        Intent intent = getIntent();
        boolean loginAgain = intent.getBooleanExtra("TOKEN_EXPIRED", false);

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
                                                }
        );

        // show or hide password
        showPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });


        Button buttonLogin = findViewById(R.id.login_button);
        Button buttonServerSetting = findViewById(R.id.configure_server_button);
        Button buttonDownload = findViewById(R.id.download);
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
                NetworkManager apiServices = NetworkManager.getInstance(LoginActivity.this);
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
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFileFromUrl(filepath);
            }
        });
        try {
            url = new URL(filepath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        fileName = url.getPath();
        fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
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

    private void downloadFileFromUrl(String url) {
        clearApplicationCache(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải xuống...");
        progressDialog.setCancelable(false); // Không cho phép hủy bỏ
        progressDialog.show();
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        dir.mkdirs();
        Uri downloadLocation = Uri.fromFile(new File(dir, fileName));

        // Start the download
        DownloadManager.Request request = new DownloadManager.Request(uri)
                .setTitle(fileName)
                .setDescription("Downloading APK...")
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setVisibleInDownloadsUi(true)
                .setDestinationUri(downloadLocation)
                .setMimeType("application/vnd.android.package-archive");  // Ensure APK MIME type is set

        request.allowScanningByMediaScanner();
        downloadID = downloadManager.enqueue(request); // Save the download ID

        // Register BroadcastReceiver to listen for when the download completes
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    @Override
    protected void onDestroy() {
        // Hủy đăng ký BroadcastReceiver khi Activity bị hủy
        unregisterReceiver(onDownloadComplete);
        super.onDestroy();
    }
    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                // Lấy URI của file từ DownloadManager
                Uri fileUri = getUriFromDownloadManager(context, id);
                if (fileUri != null) {
                    Toast.makeText(context, "Download Completee", Toast.LENGTH_SHORT).show();
                    accessDownloadDirectory();
                } else {
                    Toast.makeText(context, "File does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    private Uri getUriFromDownloadManager(Context context, long downloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
            if (columnIndex != -1) {
                String fileUriString = cursor.getString(columnIndex);
                cursor.close();
                return Uri.parse(fileUriString);
            }
            cursor.close();
        }
        return null;
    }
    public void accessDownloadDirectory() {
        // Get the Download directory
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        // Check if the directory exists
        try {
            File apkFile = new File(downloadDir, fileName);
            if (apkFile.exists()) {
                Toast.makeText(this, "APK file found: " + apkFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //Uri apkUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", apkFile);
                    Uri apkUri = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID+ ".provider", apkFile);
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
            e.printStackTrace();
            Toast.makeText(this, "Error opening APK file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public void clearApplicationCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
