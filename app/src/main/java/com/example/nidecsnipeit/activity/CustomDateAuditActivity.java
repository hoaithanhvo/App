package com.example.nidecsnipeit.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.utility.CustomDatePicker;

public class CustomDateAuditActivity extends BaseActivity {
    private Button bntdatePickerStart, bntdatePickerEnd,bntSave;
    private  NetworkManager apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_date_audit);
        setupActionBar("Custom Date Audit");
        apiService = NetworkManager.getInstance();
        bntdatePickerStart = findViewById(R.id.bntdatePickerStart);
        bntdatePickerEnd = findViewById(R.id.bntdatePickerEnd);
        bntSave=findViewById(R.id.bntSave);
        bntdatePickerStart.setText(CustomDatePicker.getTodaysDate());
        bntdatePickerEnd.setText(CustomDatePicker.getTodaysDate());
        bntdatePickerEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDatePicker.showDatePickerDialog(CustomDateAuditActivity.this, new CustomDatePicker.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(int day, int month, int year) {
                        String selectedDate = String.format("%02d-%02d-%d",day,month,year);
                        bntdatePickerEnd.setText(selectedDate);
                    }
                });
            }
        });
        bntdatePickerStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDatePicker.showDatePickerDialog(CustomDateAuditActivity.this, new CustomDatePicker.OnDateSelectedListener(){
                    @Override
                    public void onDateSelected(int day, int month, int year) {
                        String selectDate = String.format("%02d-%02d-%d",day,month,year);
                        bntdatePickerStart.setText(selectDate);
                    }
                });
            }
        });

        bntSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startDate = String.valueOf(bntdatePickerStart.getText());
                String endDate = String.valueOf(bntdatePickerEnd.getText());
            }
        });

    }
}