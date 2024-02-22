package com.example.nidecsnipeit.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.model.AlertDialogCallback;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utility.Common;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupActionBar("Settings");
        MyApplication MyApp = (MyApplication) getApplication();
        NetworkManager apiServices = NetworkManager.getInstance(this);

        TextView usernameText = findViewById(R.id.username_text);
        usernameText.setText(MyApp.getUserFullName());

        Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.showCustomAlertDialog(SettingsActivity.this, null, "Are you sure you want to log out?", true, new AlertDialogCallback() {
                    @Override
                    public void onPositiveButtonClick() {
                        Common.showProgressDialog(SettingsActivity.this, "Logout...");
                        // call api to logout
                        apiServices.logout(MyApp.getIdApiKeyServer(), new NetworkResponseListener<JSONObject>() {
                            @Override
                            public void onResult(JSONObject object) throws JSONException {
                                Common.hideProgressDialog();
                                MyApp.resetLoginInfo();
                                Intent loginIntent = new Intent(SettingsActivity.this, LoginActivity.class);
                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(loginIntent);
                                finish();
                            }
                        }, new NetworkResponseErrorListener() {
                            @Override
                            public void onErrorResult(Exception error) {
                                Common.hideProgressDialog();
                                Common.tokenInvalid(SettingsActivity.this);
                            }
                        });
                    }

                    @Override
                    public void onNegativeButtonClick() {

                    }
                });

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}