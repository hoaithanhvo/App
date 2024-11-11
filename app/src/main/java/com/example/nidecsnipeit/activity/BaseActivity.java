package com.example.nidecsnipeit.activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nidecsnipeit.R;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar(R.string.app_name);
    }

    protected void onBackButtonPressed() {
        finish();
    }

//    protected void setupActionBar(String title) {
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setTitle(title);
//            // showing the back button in action bardd
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//    }
    protected void setupActionBar(int titleResId) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Lấy tiêu đề từ tài nguyên chuỗi theo ID
            String title = getString(titleResId);

            // Đặt tiêu đề cho ActionBar
            actionBar.setTitle(title);

            // Hiển thị nút quay lại trong ActionBar
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackButtonPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackButtonPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(updateResources(newBase));
    }

    // Phương thức cập nhật ngôn ngữ cho Context
    private Context updateResources(Context context) {
      String languageCodeq = getLanguageFromPreferences(context);
      Locale locale = new Locale(languageCodeq);
      Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.createConfigurationContext(configuration);
        } else {
            context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
            return context;
        }
    }
    private String getLanguageFromPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return preferences.getString("LanguagePrefs", "");  // Default to English if not set
    }
}
