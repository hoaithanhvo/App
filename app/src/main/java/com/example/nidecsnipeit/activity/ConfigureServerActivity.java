package com.example.nidecsnipeit.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.network.model.SnackbarCallback;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.utility.Common;

public class ConfigureServerActivity extends BaseActivity {
    private View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_server);
        view = findViewById(android.R.id.content);
        setupActionBar("Configure server");

        MyApplication MyApp = (MyApplication) getApplication();

        Button configureServerBtn = findViewById(R.id.configure_server_btn);
        EditText urlServerEdit = findViewById(R.id.url_server_edit);
        urlServerEdit.setText(MyApp.getUrlServer());


        configureServerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlServer = String.valueOf(urlServerEdit.getText());
                MyApp.setUrlServer(urlServer);
                Common.showCustomSnackBar(view, "App configured!", Common.SnackBarType.SUCCESS, new SnackbarCallback() {
                    @Override
                    public void onSnackbar() {
                        NetworkManager.resetInstance();
                        finish();
                    }
                });
            }
        });
    }
}