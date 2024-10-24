package com.example.nidecsnipeit.activity;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.CategoryListAdapter;
import com.example.nidecsnipeit.network.model.BasicItemModel;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utility.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategoryListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        View view = findViewById(android.R.id.content);
        setupActionBar("Custom fields");

        NetworkManager apiServices = (NetworkManager) NetworkManager.getInstance(CategoryListActivity.this);

        List<BasicItemModel> itemList = new ArrayList<>();
        itemList.add(new BasicItemModel("1", "Item 1"));
        itemList.add(new BasicItemModel("2", "Item 2"));
        // Thêm các item khác vào itemList
        Common.showProgressDialog(this, "Loading...");
        apiServices.getCategoryList(new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                try {
                    if (object.getInt("total") > 0) {
                        List<BasicItemModel> dataList = Common.convertArrayJsonToListIdName(object.getJSONArray("rows"));
                        RecyclerView recyclerView = findViewById(R.id.category_list_recycler);
                        recyclerView.setLayoutManager(new LinearLayoutManager(CategoryListActivity.this));
                        CategoryListAdapter adapter = new CategoryListAdapter(CategoryListActivity.this, dataList);
                        recyclerView.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    Common.showCustomSnackBar(view, e.getMessage(), Common.SnackBarType.ERROR, null);
                }
                Common.hideProgressDialog();
            }
        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {
                Common.hideProgressDialog();
                Common.showCustomSnackBar(view, error.getMessage(), Common.SnackBarType.ERROR, null);
            }
        });
    }
}