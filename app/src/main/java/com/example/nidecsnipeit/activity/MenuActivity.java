package com.example.nidecsnipeit.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.example.nidecsnipeit.Config;
import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.service.TokenValidationService;


public class MenuActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication MyApp = (MyApplication) getApplication();
        if (MyApp.isFirstRun()) {
            // if app first run, redirect to login screen
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        } else {
            Intent serviceIntent = new Intent(this, TokenValidationService.class);
            startService(serviceIntent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Get button
        Button checkInBtn = findViewById(R.id.checkin_btn);
        Button checkOutBtn = findViewById(R.id.checkout_btn);
        Button maintenanceBtn = findViewById(R.id.maintenance_btn);
        Button settingBtn = findViewById(R.id.setting_btn);

        // OnClickListener for button
        checkInBtn.setOnClickListener(createButtonClickListener(Config.CHECK_IN_MODE));
        checkOutBtn.setOnClickListener(createButtonClickListener(Config.CHECK_OUT_MODE));
        maintenanceBtn.setOnClickListener(createButtonClickListener(Config.MAINTENANCE_MODE));
        settingBtn.setOnClickListener(createButtonClickListener(Config.SETTING_MODE));
    }

    private View.OnClickListener createButtonClickListener(final Integer mode) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick(mode);
            }
        };
    }

    private void handleButtonClick(Integer mode) {
        Intent intent;
        if (mode == Config.SETTING_MODE) {
            intent = new Intent(this, SettingsActivity.class);
        } else {
            intent = new Intent(this, SearchActivity.class);
            intent.putExtra("mode", mode);
        }
        startActivity(intent);
    }

}