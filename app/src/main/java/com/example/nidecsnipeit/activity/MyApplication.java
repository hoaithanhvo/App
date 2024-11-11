package com.example.nidecsnipeit.activity;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nidecsnipeit.R;

import java.util.Locale;

public class MyApplication extends Application {

    private String currentUrlServer;
    private String currentApiKeyServer;
    private String currentIdApiKeyServer;
    private Boolean isFirstRun;
    private String userFullName;
    private boolean isAdmin;
    private String displayedFieldsJsonString;
    private final String PREF_NAME = "NIDEC_SNIPEIT";
    private final String URL_SERVER = "URL_SERVER";
    private final String API_KEY_SERVER = "API_KEY_SERVER";
    private final String ID_API_KEY_SERVER = "ID_API_KEY_SERVER";
    private final String IS_FIRST_RUN = "IS_FIRST_RUN";
    private final String USER_FULL_NAME = "USER_FULL_NAME";
    private final String IS_ADMIN = "IS_ADMIN";
    private final String DISPLAYED_FIELDS = "DISPLAYED_FIELDS";

    public String getSTART_AUDIT() {
        return START_AUDIT;
    }

    public String getEND_AUDIT() {
        return END_AUDIT;
    }

    private String START_AUDIT = "START_AUDIT";
    private  String END_AUDIT = "END_AUDIT";
    private  String ASSETVIEW = "ASSETVIEW";

    @Override
    public void onCreate() {
        super.onCreate();
        loadDataFromPreferences();
    }

    // load server information from local storage
    private void loadDataFromPreferences() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String urlServer = preferences.getString(URL_SERVER, "http://10.234.1.97:3000");
        String apiKey = preferences.getString(API_KEY_SERVER, null);
        String idApiKey = preferences.getString(ID_API_KEY_SERVER, null);
        boolean isFirstRun = preferences.getBoolean(IS_FIRST_RUN, true);
        String userFullName = preferences.getString(USER_FULL_NAME, "");
        boolean isAdmin = preferences.getBoolean(IS_ADMIN, false);
        String displayedFields = preferences.getString(DISPLAYED_FIELDS, "");
        String start_audit_date = preferences.getString("START_AUDIT","");
        String end_audit_date = preferences.getString("END_AUDIT","");
        this.currentUrlServer = urlServer;
        this.currentApiKeyServer = apiKey;
        this.currentIdApiKeyServer = idApiKey;
        this.isFirstRun = isFirstRun;
        this.userFullName = userFullName;
        this.isAdmin = isAdmin;
        this.displayedFieldsJsonString = displayedFields;
        this.START_AUDIT = start_audit_date;
        this.END_AUDIT=end_audit_date;
    }
    public String getUserFullName() {
        return this.userFullName;
    }
    public boolean isAdmin() {
        return this.isAdmin;
    }
    public boolean isFirstRun() {
        return this.isFirstRun;
    }
    public String getUrlServer() {
        return this.currentUrlServer;
    }
    public void setUrlServer(String newUrl) {
        this.currentUrlServer = newUrl;
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(URL_SERVER, this.currentUrlServer);
        editor.apply();
    }

    public void setDisplayedFields(String displayedFields) {
        this.displayedFieldsJsonString = displayedFields;
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(DISPLAYED_FIELDS, this.displayedFieldsJsonString);
        editor.apply();
    }

    public String getDisplayedFieldsJsonString() {
        return this.displayedFieldsJsonString;
    }

    public String getApiKeyServer() {
        return this.currentApiKeyServer;
    }
    public String getIdApiKeyServer() {
        return this.currentIdApiKeyServer;
    }

    // setting server info
    public void setLoginInfo(String newIdApiKey, String newApiKey, String userFullName, boolean isAdmin,int assetView) {
        // save server info to SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ID_API_KEY_SERVER, newIdApiKey);
        editor.putString(API_KEY_SERVER, newApiKey);
        editor.putBoolean(IS_FIRST_RUN, false);
        editor.putString(USER_FULL_NAME, userFullName);
        editor.putBoolean(IS_ADMIN, isAdmin);
        editor.putString(START_AUDIT,"2024-08-10 23:59:59");
        editor.putString(END_AUDIT,"2024-12-10 23:59:59");
        editor.putInt(ASSETVIEW,assetView);
        editor.apply();
        this.currentIdApiKeyServer = newIdApiKey;
        this.currentApiKeyServer = newApiKey;
        this.isFirstRun = false;
        this.userFullName = userFullName;
        this.isAdmin = isAdmin;
    }

    // reset server info
    public void resetLoginInfo() {
        // save server info to SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ID_API_KEY_SERVER, null);
        editor.putString(API_KEY_SERVER, null);
        editor.putBoolean(IS_FIRST_RUN, true);
        editor.putString(USER_FULL_NAME, null);
        editor.putString(DISPLAYED_FIELDS, null);
        editor.putBoolean(IS_ADMIN, false);
        editor.apply();
        this.currentIdApiKeyServer = null;
        this.currentApiKeyServer = null;
        this.isFirstRun = true;
        this.userFullName = "";
        this.isAdmin = false;
        this.displayedFieldsJsonString = "";
    }

}
