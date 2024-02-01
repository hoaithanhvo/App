package com.example.nidecsnipeit.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.nidecsnipeit.Config;
import com.example.nidecsnipeit.R;


public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Get button
        Button checkInBtn = findViewById(R.id.checkin_btn);
        Button checkOutBtn = findViewById(R.id.checkout_btn);
        Button maintenanceBtn = findViewById(R.id.maintenance_btn);

        // OnClickListener for button
        checkInBtn.setOnClickListener(createButtonClickListener(Config.CHECK_IN_MODE));
        checkOutBtn.setOnClickListener(createButtonClickListener(Config.CHECK_OUT_MODE));
        maintenanceBtn.setOnClickListener(createButtonClickListener(Config.MAINTENANCE_MODE));
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
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("mode", mode);
        startActivity(intent);
    }

}