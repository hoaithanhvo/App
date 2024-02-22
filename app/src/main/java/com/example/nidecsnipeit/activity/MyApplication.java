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

    private final List<DetailFieldModel> detailScreenFields = new ArrayList<>();
    private String currentUrlServer;
    private String currentApiKeyServer;
    private String currentIdApiKeyServer;
    private Date expireApiKeyServer;
    private Boolean isFirstRun;
    private String userFullName;
//    private final String urlServerDefault = "https://develop.snipeitapp.com";
    private final String urlServerDefault = "http://192.168.0.190:4402";
    private final String PREF_NAME = "NIDEC_SNIPEIT";
    private final String URL_SERVER = "URL_SERVER";
    private final String API_KEY_SERVER = "API_KEY_SERVER";
    private final String ID_API_KEY_SERVER = "ID_API_KEY_SERVER";
    private final String EXPIRE_API_KEY_SERVER = "EXPIRE_API_KEY_SERVER";
    private final String IS_FIRST_RUN = "IS_FIRST_RUN";
    private final String USER_FULL_NAME = "USER_FULL_NAME";

    @Override
    public void onCreate() {
        super.onCreate();
        initDetailScreenFields();
        loadDataFromPreferences();
    }

    private void initDetailScreenFields() {
        detailScreenFields.add(new DetailFieldModel("model"));
        detailScreenFields.add(new DetailFieldModel("serial"));
        detailScreenFields.add(new DetailFieldModel("name"));
        detailScreenFields.add(new DetailFieldModel("assigned_to"));
        detailScreenFields.add(new DetailFieldModel("notes"));
    }

    // load server information from local storage
    private void loadDataFromPreferences() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String urlServer = preferences.getString(URL_SERVER, urlServerDefault);
        String apiKey = preferences.getString(API_KEY_SERVER, null);
        String idApiKey = preferences.getString(ID_API_KEY_SERVER, null);
        String expireApiKey = preferences.getString(EXPIRE_API_KEY_SERVER, null);
        boolean isFirstRun = preferences.getBoolean(IS_FIRST_RUN, true);
        String userFullName = preferences.getString(USER_FULL_NAME, "");

        this.currentUrlServer = urlServer;
        this.currentApiKeyServer = apiKey;
        this.currentIdApiKeyServer = idApiKey;
        this.expireApiKeyServer = Common.convertStringToDate(expireApiKey);
        this.isFirstRun = isFirstRun;
        this.userFullName = userFullName;
    }

    public List<DetailFieldModel> getDetailScreenFields() {
        return detailScreenFields;
    }

    public String getUserFullName() {
        return this.userFullName;
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
    public void setLoginInfo(String newIdApiKey, String newApiKey, Date newExpireApiKey, String userFullName) {
        // add 7 days to expire time token
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newExpireApiKey);
        calendar.add(Calendar.DAY_OF_MONTH, 0);
        Date expireTime =  calendar.getTime();

        // save server info to SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ID_API_KEY_SERVER, newIdApiKey);
        editor.putString(API_KEY_SERVER, newApiKey);
        editor.putString(EXPIRE_API_KEY_SERVER, Common.convertDateToString(expireTime));
        editor.putBoolean(IS_FIRST_RUN, false);
        editor.putString(USER_FULL_NAME, userFullName);
        editor.apply();
        this.currentIdApiKeyServer = newIdApiKey;
        this.currentApiKeyServer = newApiKey;
        this.expireApiKeyServer = expireTime;
        this.isFirstRun = false;
        this.userFullName = userFullName;
    }

    // reset server info
    public void resetLoginInfo() {
        // save server info to SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ID_API_KEY_SERVER, null);
        editor.putString(API_KEY_SERVER, null);
        editor.putString(EXPIRE_API_KEY_SERVER, null);
        editor.putBoolean(IS_FIRST_RUN, true);
        editor.putString(USER_FULL_NAME, null);
        editor.apply();
        this.currentIdApiKeyServer = null;
        this.currentApiKeyServer = null;
        this.expireApiKeyServer = null;
        this.isFirstRun = true;
        this.userFullName = "";
    }

    public void setDefaultServerInfo() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(URL_SERVER, urlServerDefault);
        editor.putString(ID_API_KEY_SERVER, null);
        editor.putString(API_KEY_SERVER, null);
        editor.putString(EXPIRE_API_KEY_SERVER, null);
        editor.apply();
        this.currentUrlServer = urlServerDefault;
        this.currentIdApiKeyServer = null;
        this.currentApiKeyServer = null;
        this.expireApiKeyServer = null;
    }
}
