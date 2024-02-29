package com.example.nidecsnipeit.activity;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.nidecsnipeit.model.DetailFieldModel;
import com.example.nidecsnipeit.utility.Common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyApplication extends Application {

    private String currentUrlServer;
    private String currentApiKeyServer;
    private String currentIdApiKeyServer;
    private Boolean isFirstRun;
    private String userFullName;
    private boolean isAdmin;
    private final String urlServerDefault = "https://develop.snipeitapp.com";
    private String displayedFieldsJsonString;
    private final String PREF_NAME = "NIDEC_SNIPEIT";
    private final String URL_SERVER = "URL_SERVER";
    private final String API_KEY_SERVER = "API_KEY_SERVER";
    private final String ID_API_KEY_SERVER = "ID_API_KEY_SERVER";
    private final String IS_FIRST_RUN = "IS_FIRST_RUN";
    private final String USER_FULL_NAME = "USER_FULL_NAME";
    private final String IS_ADMIN = "IS_ADMIN";
    private final String DISPLAYED_FIELDS = "DISPLAYED_FIELDS";

    @Override
    public void onCreate() {
        super.onCreate();
        loadDataFromPreferences();
    }

    // load server information from local storage
    private void loadDataFromPreferences() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String urlServer = preferences.getString(URL_SERVER, urlServerDefault);
        String apiKey = preferences.getString(API_KEY_SERVER, null);
        String idApiKey = preferences.getString(ID_API_KEY_SERVER, null);
        boolean isFirstRun = preferences.getBoolean(IS_FIRST_RUN, true);
        String userFullName = preferences.getString(USER_FULL_NAME, "");
        boolean isAdmin = preferences.getBoolean(IS_ADMIN, false);
        String displayedFields = preferences.getString(DISPLAYED_FIELDS, "");

        this.currentUrlServer = urlServer;
        this.currentApiKeyServer = apiKey;
        this.currentIdApiKeyServer = idApiKey;
        this.isFirstRun = isFirstRun;
        this.userFullName = userFullName;
        this.isAdmin = isAdmin;
        this.displayedFieldsJsonString = displayedFields;
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

    public String getDefaultUrlServer() {
        return this.urlServerDefault;
    }

    // setting server info
    public void setLoginInfo(String newIdApiKey, String newApiKey, String userFullName, boolean isAdmin) {
        // save server info to SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ID_API_KEY_SERVER, newIdApiKey);
        editor.putString(API_KEY_SERVER, newApiKey);
        editor.putBoolean(IS_FIRST_RUN, false);
        editor.putString(USER_FULL_NAME, userFullName);
        editor.putBoolean(IS_ADMIN, isAdmin);
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
