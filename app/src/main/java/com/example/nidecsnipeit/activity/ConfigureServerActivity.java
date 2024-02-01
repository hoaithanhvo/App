package com.example.nidecsnipeit.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.model.SnackbarCallback;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utility.Common;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfigureServerActivity extends BaseActivity {
    private View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_server);
        view = findViewById(android.R.id.content);
        setupActionBar("Configure server");

        MyApplication MyApp = (MyApplication) getApplication();

        Button configureServerBtn = findViewById(R.id.configure_server_btn);
        Button configureDefaultServerBtn = findViewById(R.id.configure_default_btn);
        EditText urlServerEdit = findViewById(R.id.url_server_edit);
        EditText apiTokenEdit = findViewById(R.id.api_token_edit);
        urlServerEdit.setText(MyApp.getUrlServer());

        configureServerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.showProgressDialog(ConfigureServerActivity.this, "Checking server configuration...");
                String urlServer = String.valueOf(urlServerEdit.getText());
                String apiToken = String.valueOf(apiTokenEdit.getText());
                NetworkManager apiServices = NetworkManager.getInstance(ConfigureServerActivity.this);

                apiServices.checkConnection(urlServer, apiToken, new NetworkResponseListener<JSONObject>() {
                    @Override
                    public void onResult(JSONObject object) throws JSONException {
                        Common.hideProgressDialog();
                        MyApp.setServerInfo(urlServer, apiToken);
                        Common.showCustomSnackBar(view, "App configured!", Common.SnackBarType.SUCCESS, new SnackbarCallback() {
                            @Override
                            public void onSnackbar() {
                                finish();
                            }
                        });
                    }
                }, new NetworkResponseErrorListener() {
                    @Override
                    public void onErrorResult(Exception error) {
                        Common.hideProgressDialog();
                        Common.showCustomSnackBar(view, "Connection to server failed", Common.SnackBarType.ERROR, null);
                    }
                });
            }
        });

        // set default server
        configureDefaultServerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.showProgressDialog(ConfigureServerActivity.this, "Checking server configuration...");
                String urlServer = MyApp.getDefaultUrlServer();
                String apiToken = MyApp.getDefaultApiKeyServer();
                NetworkManager apiServices = NetworkManager.getInstance(ConfigureServerActivity.this);

                apiServices.checkConnection(urlServer, apiToken, new NetworkResponseListener<JSONObject>() {
                    @Override
                    public void onResult(JSONObject object) throws JSONException {
                        Common.hideProgressDialog();
                        MyApp.setDefaultServerInfo();
                        Common.showCustomSnackBar(view, "App configured!", Common.SnackBarType.SUCCESS, new SnackbarCallback() {
                            @Override
                            public void onSnackbar() {
                                finish();
                            }
                        });
                    }
                }, new NetworkResponseErrorListener() {
                    @Override
                    public void onErrorResult(Exception error) {
                        Common.hideProgressDialog();
                        Common.showCustomSnackBar(view, "Connection to server failed", Common.SnackBarType.ERROR, null);
                    }
                });
            }
        });
    }
}