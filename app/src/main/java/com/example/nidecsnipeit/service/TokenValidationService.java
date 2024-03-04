package com.example.nidecsnipeit.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.nidecsnipeit.activity.LoginActivity;
import com.example.nidecsnipeit.activity.MyApplication;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utility.Common;

import org.json.JSONObject;

public class TokenValidationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyApplication MyApp = (MyApplication) getApplication();
        String currentToken = MyApp.getApiKeyServer();
        NetworkManager apiServices = NetworkManager.getInstance(getApplicationContext());

        apiServices.checkConnection(currentToken, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
            }
        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {
                Common.tokenInvalid(getApplicationContext());
            }
        });

        return START_NOT_STICKY;
    }
}
