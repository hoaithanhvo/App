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

import org.json.JSONObject;

import java.util.Locale;

public class SettingsActivity extends BaseActivity {
    private static final String PREF_NAME = "LanguagePrefs";
    private static final String LANGUAGE_KEY = "selected_language";
    private Button bnt_changelanguage;
    private SwitchCompat langSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String language = getSharedPreferences("app_prefs", MODE_PRIVATE).getString("language", "en");
        setContentView(R.layout.activity_settings);
        setupActionBar("Settings");
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


        bnt_changelanguage = findViewById(R.id.bnt_changelanguage);
        bnt_changelanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyLanguage();
            }
        });

    }
    private void applyLanguage() {
        final String language[] = {"English","VietNam"};
        AlertDialog.Builder  mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Change");
        mBuilder.setSingleChoiceItems(language, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    lan("");
                    recreate();

                }else {
                    lan("vi");
                    recreate();
                }
            }
        });
        mBuilder.create();
        mBuilder.show();
    }
    private void lan(String s){
        Locale locale = new Locale(s);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale=locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
    }

//    private void saveLanguageToPreferences(String languageCode) {
//        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(LANGUAGE_KEY, languageCode);
//        editor.apply();
//    }
//
//    private String getLanguageFromPreferences() {
//        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
//        return preferences.getString(LANGUAGE_KEY, "");  // Default to English ("" = English)
//    }
}