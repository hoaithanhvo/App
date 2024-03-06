package com.example.nidecsnipeit.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.CategoryFieldAdapter;
import com.example.nidecsnipeit.model.CategoryFieldModel;
import com.example.nidecsnipeit.model.UpdateDisplayedFieldModel;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utility.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SetupFieldActivity extends BaseActivity {

    private CategoryFieldAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_field);

        Intent intent = getIntent();
        String categoryId = intent.getStringExtra("CATEGORY_ID");
        String categoryName = intent.getStringExtra("CATEGORY_NAME");
        setupActionBar("Custom for " + categoryName);

        NetworkManager apiServices = NetworkManager.getInstance();
        Common.showProgressDialog(this, "Loading...");
        // get field list by category id
        apiServices.getFieldsByCategoryId(categoryId, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) throws JSONException {
                if (object.getJSONArray("payload").length() > 0) {
                    JSONArray dataJSON = object.getJSONArray("payload");
                    List<CategoryFieldModel> dataList = Common.convertArrayJsonCategoryField(dataJSON);

                    RecyclerView recyclerView = findViewById(R.id.category_fields_recycler);
                    recyclerView.setLayoutManager(new LinearLayoutManager(SetupFieldActivity.this));
                    adapter = new CategoryFieldAdapter(SetupFieldActivity.this, dataList);
                    recyclerView.setAdapter(adapter);

                }
                Common.hideProgressDialog();
            }
        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {
                Common.hideProgressDialog();
                Common.tokenInvalid(SetupFieldActivity.this);
            }
        });

        Button saveButton = findViewById(R.id.save_custom_button);
        // handle save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> dataList = adapter.getColumnAllowedDisplay();
                if (dataList.size() == 0) {
                    Common.showCustomSnackBar(v, "Please select least one field to show.", Common.SnackBarType.ERROR, null);
                } else {
                    UpdateDisplayedFieldModel updateDisplayedFieldItem = new UpdateDisplayedFieldModel(categoryId, dataList.toString());
                    Common.showProgressDialog(SetupFieldActivity.this, "Saving...");

                    apiServices.updateDisplayedFieldByCategoryId(updateDisplayedFieldItem, new NetworkResponseListener<JSONObject>() {
                        @Override
                        public void onResult(JSONObject object) throws JSONException {
                            if (object.getString("status").equals("success")) {
                                MyApplication MyApp = (MyApplication) getApplication();

                                // update displayed field list
                                apiServices.getFieldsAllCategory(new NetworkResponseListener<JSONObject>() {
                                    @Override
                                    public void onResult(JSONObject object) throws JSONException {
                                        // Convert JSON to String to save shared preferences
                                        String jsonString = object.getJSONObject("payload").toString();
                                        MyApp.setDisplayedFields(jsonString);
                                        Common.hideProgressDialog();
                                        Common.showCustomSnackBar(v, "Settings saved.", Common.SnackBarType.SUCCESS, null);
                                        finish();
                                    }
                                }, new NetworkResponseErrorListener() {
                                    @Override
                                    public void onErrorResult(Exception error) {
                                        Common.hideProgressDialog();
                                        Common.tokenInvalid(SetupFieldActivity.this);
                                    }
                                });
                            }
                        }
                    }, new NetworkResponseErrorListener() {
                        @Override
                        public void onErrorResult(Exception error) {
                            Common.hideProgressDialog();
                            Common.tokenInvalid(SetupFieldActivity.this);
                        }
                    });
                }
            }
        });
    }
}