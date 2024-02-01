package com.example.nidecsnipeit.activity;

import android.app.Application;
import android.content.SharedPreferences;

import com.example.nidecsnipeit.model.DetailFieldModel;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private final List<DetailFieldModel> detailScreenFields = new ArrayList<>();
    private String currentUrlServer;
    private String currentApiKeyServer;
    private final String urlServerDefault = "https://develop.snipeitapp.com";
    private final String apiTokenDefault = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiZTU2MDc0MjVmYjM5YTEwYjFjNTZlZTAxMTBmZDk4ZjQ0ZjVjODMzYjcxZWVhYjZlNDk1NGMwOThlY2YzMzU2MDY4Mzg4MmFhMDMzOTAzNzciLCJpYXQiOjE2MzI4NjU5MTgsIm5iZiI6MTYzMjg2NTkxOCwiZXhwIjoyMjY0MDIxNTE4LCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.LgGVzyH67IRhXvccHd4j2Dn6TDuIuQTBoo30_wD9jPehy8v_h0xBmE1-dOUBRJyeJOI8B4gwPeALsWaudpGj9Lb5qWAtKV7eYtH9IYQKoLF_iHgOGXnAUcNwID6zBU_YyLNSI6gp8zjutLJias33CBLsHy5ZRNpxVibVrZouJ_HjYuIYbtZyLus-KFFeibtZoPiTWOeHhQFD37MR6ifx4dBqT37fN-xDS99mONtrkAplEIou5aSO1oZ4IlJIPCUyA1lixPgpn1YU7PxiBDZp1teeugD0WEmrAqxRS2I0bH4qPsuTsrVXS_lo87Sf5LBGLW7lGHKqyYH6J47OZOM0K-SrxLKtE1ww8jyLBgnnxH0lJHRLCBiwUnL5ZGTUmiOysUA-wSJ6s78o8Pc-ec6bpBvAlelHdiQ-wslE7gzEJDptbejFg-75b_CEwgJYh7J2D18ul6Qu5EFCUEgt033mm04dgVk0isWTDt6EW5ZvTo5Qhr1LY0YnEIXCTqIRN-BSQjL55sZaCrtwR_21bnBGgniyI5MRDYblFawVmFKroeClCpSjBo9vi66akdD5hjpvx67RL3r33BZQhEXmPifUPNH5wP_U-IHGFUD99TJk2c1awF0RASveZRLSunbJb1x6hGAVUaIvQV4r2quWzXqYyKLph9kGTyJYrb6iJtH5smE";

    private static final String PREF_NAME = "NIDEC_SNIPEIT";
    private static final String IS_FIRST_RUN = "IS_FIRST_RUN";
    private static final String URL_SERVER = "URL_SERVER";
    private static final String API_KEY_SERVER = "API_KEY_SERVER";

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
        boolean isFirstRun = preferences.getBoolean(IS_FIRST_RUN, true);
        String urlServer = preferences.getString(URL_SERVER, urlServerDefault);
        String apiKey = preferences.getString(API_KEY_SERVER, apiTokenDefault);

        if (isFirstRun) {
            // First time, save default value to SharedPreferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(URL_SERVER, urlServerDefault);
            editor.putString(API_KEY_SERVER, apiTokenDefault);
            editor.putBoolean(IS_FIRST_RUN, false);
            editor.apply();
            this.currentUrlServer = urlServerDefault;
            this.currentApiKeyServer = apiTokenDefault;
        } else {
            this.currentUrlServer = urlServer;
            this.currentApiKeyServer = apiKey;
        }
    }

    public List<DetailFieldModel> getDetailScreenFields() {
        return detailScreenFields;
    }

    public String getUrlServer() {
        return this.currentUrlServer;
    }

    public String getApiKeyServer() {
        return this.currentApiKeyServer;
    }

    public String getDefaultUrlServer() {
        return this.urlServerDefault;
    }

    public String getDefaultApiKeyServer() {
        return this.apiTokenDefault;
    }

    public void setServerInfo(String newUrlServer, String newApiKey) {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(URL_SERVER, newUrlServer);
        editor.putString(API_KEY_SERVER, newApiKey);
        editor.apply();
        this.currentUrlServer = newUrlServer;
        this.currentApiKeyServer = newApiKey;
    }

    public void setDefaultServerInfo() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(URL_SERVER, urlServerDefault);
        editor.putString(API_KEY_SERVER, apiTokenDefault);
        editor.apply();
        this.currentUrlServer = urlServerDefault;
        this.currentApiKeyServer = apiTokenDefault;
    }
}
