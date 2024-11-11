package com.example.nidecsnipeit.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.network.model.AlertDialogCallback;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utility.Common;
import com.example.nidecsnipeit.utility.LocaleHelper;

import org.json.JSONObject;

import java.util.Locale;

public class SettingsActivity extends BaseActivity {
    private ImageButton bnt_changelanguage;
    private SwitchCompat langSwitch;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        LocaleHelper localeHelper = LocaleHelper.getInstance(this);
        setupActionBar(R.string.setting);
        MyApplication MyApp = (MyApplication) getApplication();
        NetworkManager apiServices = NetworkManager.getInstance(this);
        if (!MyApp.isAdmin()) {
            // if is not admin, hide appearance_configuration view
            LinearLayout appearanceConfigView = findViewById(R.id.appearance_configuration);
            appearanceConfigView.setVisibility(View.GONE);
        }

        // Set app information
        TextView usernameText = findViewById(R.id.username_text);
        TextView serverUrlText = findViewById(R.id.server_url_text);
        usernameText.setText(MyApp.getUserFullName());
        serverUrlText.setText(MyApp.getUrlServer());

        Button logoutButton = findViewById(R.id.logout_button);
        LinearLayout customFieldsButton = findViewById(R.id.custom_field);
        LinearLayout custom_dateAudit = findViewById(R.id.custom_dateAudit);
        bnt_changelanguage = findViewById(R.id.bnt_changelanguage);
        preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        // handle logic to logout

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
                            public void onResult(JSONObject object) {
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
        custom_dateAudit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,CustomDateAuditActivity.class);
                startActivity(intent);
            }
        });
        customFieldsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, CategoryListActivity.class);
                startActivity(intent);
            }
        });


        bnt_changelanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localeHelper.applyLanguage(SettingsActivity.this);
            }
        });
       updateLanguageButton();

    }

    private void updateLanguageButton() {
        String selectedFlag = preferences.getString("SelectFlagImage", "img_flag_uk");
        int flagDrawableId = getResources().getIdentifier(selectedFlag, "drawable", getPackageName());
        bnt_changelanguage.setImageResource(flagDrawableId);  // Cập nhật cờ mới cho ImageButton
    }
}