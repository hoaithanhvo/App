package com.example.nidecsnipeit.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.model.AlertDialogCallback;
import com.example.nidecsnipeit.model.LoginItemModel;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utility.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private ImageButton showPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MyApplication MyApp = (MyApplication) getApplication();

        Intent intent = getIntent();
        boolean loginAgain = intent.getBooleanExtra("TOKEN_EXPIRED", false);

        if (loginAgain) {
            Common.showCustomAlertDialog(this, null, "Your session has expired. Please log in again.", false, new AlertDialogCallback() {
                @Override
                public void onPositiveButtonClick() {
                }
                @Override
                public void onNegativeButtonClick() {
                }
            });
        }

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        showPasswordButton = findViewById(R.id.show_password_button);

        passwordEditText.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {
           }
           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
           }
           @Override
           public void afterTextChanged(Editable s) {
               // disable showPasswordButton if passwordEditText is Empty
               if (s.toString().trim().isEmpty()) {
                   showPasswordButton.setVisibility(View.GONE);
               } else {
                   showPasswordButton.setVisibility(View.VISIBLE);
               }
           }
       }
        );

        // show or hide password
        showPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });


        Button buttonLogin = findViewById(R.id.login_button);
        Button buttonServerSetting = findViewById(R.id.configure_server_button);

        buttonServerSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ConfigureServerActivity.class);
                startActivity(intent);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset instance network
                NetworkManager.resetInstance();
                NetworkManager apiServices = NetworkManager.getInstance(LoginActivity.this);
                String userName = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                LoginItemModel loginItem = new LoginItemModel(userName, password);

                if (userName.isEmpty() || password.isEmpty()) {
                    Common.showCustomSnackBar(v, "Please enter username and password.", Common.SnackBarType.ERROR, null);
                } else {
                    Common.showProgressDialog(LoginActivity.this, "Login...");
                    apiServices.login(loginItem, new NetworkResponseListener<JSONObject>() {
                        @Override
                        public void onResult(JSONObject object) throws JSONException {
                            if (object.getString("status").equals("error")) {
                                Common.hideProgressDialog();
                                Common.showCustomSnackBar(v, object.getString("messages"), Common.SnackBarType.ERROR, null);
                            } else {
                                // reset instance network
                                NetworkManager.resetInstance();

                                JSONObject dataToken = object.getJSONObject("payload");
                                String accessToken = dataToken.getString("token");
                                String idToken = dataToken.getString("id");
                                String userFullName = dataToken.getString("user_full_name");
                                String expireTokenString = dataToken.getString("expires_at");
                                Date expireTokenDate = Common.convertStringToDate(expireTokenString);
                                // save token to local storage
                                MyApp.setLoginInfo(idToken, accessToken, expireTokenDate, userFullName);

                                Common.hideProgressDialog();
                                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }
                    }, new NetworkResponseErrorListener() {
                        @Override
                        public void onErrorResult(Exception error) {
                            Common.hideProgressDialog();
                            Common.showCustomSnackBar(v, error.getMessage(), Common.SnackBarType.ERROR, null);
                        }
                    });
                }
            }
        });

    }

    private void togglePasswordVisibility() {
        if (passwordEditText.getInputType() != InputType.TYPE_CLASS_TEXT) {
            // show password
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            showPasswordButton.setImageResource(R.drawable.ic_hide);
        } else {
            // hide password
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            showPasswordButton.setImageResource(R.drawable.ic_unhide);
        }

        passwordEditText.setSelection(passwordEditText.getText().length());
    }

}
