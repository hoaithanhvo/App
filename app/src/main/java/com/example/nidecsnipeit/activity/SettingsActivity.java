package com.example.nidecsnipeit.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nidecsnipeit.R;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupActionBar("Settings");
        MyApplication MyApp = (MyApplication) getApplication();

        LinearLayout configureServer = findViewById(R.id.configure_server);
        TextView urlText = findViewById(R.id.url_server_text);
        urlText.setText(MyApp.getUrlServer());

        configureServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ConfigureServerActivity.class);
                startActivity(intent);
            }
        });
    }
}