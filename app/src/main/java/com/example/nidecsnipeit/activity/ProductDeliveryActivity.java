package com.example.nidecsnipeit.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.ProductDeliveryAdapter;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.network.model.ProductDeliveryModel;
import com.example.nidecsnipeit.utility.Common;
import com.example.nidecsnipeit.utility.EndlessScrollListener;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductDeliveryActivity extends BaseActivity {
    private NetworkManager apiServices;
    private RecyclerView rcyProductDelivery;
    private ProductDeliveryAdapter adapter;
    private List<ProductDeliveryModel> productList = new ArrayList<>();
    private TextView txtTotal;
    private EditText txtSearch;
    private Boolean hasMoreData = true;
    private EndlessScrollListener endlessScrollListener;
    private List<ProductDeliveryModel> originalList = new ArrayList<>();
    private ImageButton imgClose;
    private Button bntSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setupActionBar(R.string.equipment_request);
        setContentView(R.layout.activity_product_delivery);
        apiServices = NetworkManager.getInstance(this);
        rcyProductDelivery = findViewById(R.id.rcyProductDelivery);
        txtSearch = findViewById(R.id.txtSearch);
        bntSearch = findViewById(R.id.bntSearch);
        txtTotal = findViewById(R.id.txtTotal);
        imgClose= findViewById(R.id.imgClose);
        adapter = new ProductDeliveryAdapter();
        GetDataProductDelivery(10, 0);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rcyProductDelivery.setAdapter(adapter);
        rcyProductDelivery.setLayoutManager(layoutManager);

        // Initialize and set EndlessScrollListener
        endlessScrollListener = new EndlessScrollListener(layoutManager) {
            @Override
            public void loadMoreItems(int offset, int limit) {
                txtTotal.setText(String.valueOf(adapter.getListItems().size()));
                if (hasMoreData) {
                    GetDataProductDelivery(limit, offset);
                }
            }
        };

        rcyProductDelivery.addOnScrollListener(endlessScrollListener);

        adapter.setOnItemClickListener(product -> {
            SharedPreferences sharedPreferences = getSharedPreferences("NIDEC_SNIPEIT", MODE_PRIVATE);
            int checkAssetView = sharedPreferences.getInt("ASSETVIEW", MODE_PRIVATE);
            if (checkAssetView == 0) {
                Toast.makeText(ProductDeliveryActivity.this, R.string.toast_permissionUser, Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent(ProductDeliveryActivity.this, ProductDetailActivity.class);
            JSONArray itemsRequest = product.getItems_request();
            String jsonString = itemsRequest.toString();
            intent.putExtra("ITEM_DATA", jsonString);
            startActivity(intent);
        }, ProductDeliveryActivity.this);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    endlessScrollListener.setLoading(true);
                    txtSearch.setText("");
            }
        });
        bntSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItemById();
            }
        });
    }

    private void GetDataProductDelivery(int limit, int offset) {
        Common.showProgressDialog(this, "Đang tải vui lòng đợi...");
        apiServices.getProductDelivery(limit, offset, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                parsObject(object);
                adapter.addData(productList);
                txtTotal.setText(String.valueOf(adapter.getListItems().size()));
                originalList.addAll(productList);
                endlessScrollListener.setLoading(true);
            }
        }, error -> {
            Toast.makeText(ProductDeliveryActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void filter(String text) {
        endlessScrollListener.setSearching(true);
        productList.clear();
        if (text.isEmpty()) {
            productList.addAll(originalList);
            endlessScrollListener.setSearching(false);
        } else {
            for (ProductDeliveryModel item : originalList) {
                if (item.getProductID() != null && item.getProductID().toLowerCase().contains(text.toLowerCase())) {
                    productList.add(item);
                }
            }
        }
        adapter.searchData(productList);
        txtTotal.setText(String.valueOf(productList.size()));
    }

    private void parsObject(JSONObject object){
        try {
            Common.hideProgressDialog();
            JSONArray rows = object.getJSONArray("rows");
            productList.clear();
            if (rows.length() <10 && !endlessScrollListener.isSearching()) {
                hasMoreData = false;
            }
            if (rows.length() == 0){
                Toast.makeText(ProductDeliveryActivity.this,R.string.empty,Toast.LENGTH_LONG).show();
                return;
            }
            for (int i = 0; i < rows.length(); i++) {
                ProductDeliveryModel productItem = new ProductDeliveryModel();
                JSONObject item = rows.getJSONObject(i);
                String id = item.getString("id");
                JSONObject user_request = item.getJSONObject("user_request");
                JSONObject created_at = item.getJSONObject("created_at");
                String formatted = created_at.getString("formatted");
                productItem.setUserID(user_request.getString("name"));
                productItem.setCreateAt(formatted);
                productItem.setProductID(id);
                JSONObject status = item.getJSONObject("status");
                productItem.setStatusMap(status.getString("name"), status.getString("color"));
                JSONArray items_request = item.getJSONArray("items_request");
                productItem.setItems_request(items_request);
                productList.add(productItem);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    private void searchItemById(){

        endlessScrollListener.setSearching(true);
        String textSearch = txtSearch.getText().toString();
        if(textSearch.equals("")){
            endlessScrollListener.setSearching(false);
            Toast.makeText(this,R.string.empty,Toast.LENGTH_LONG).show();
            return;
        }
        apiServices.getRequestAssetById(textSearch, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                parsObject(object);
                adapter.searchData(productList);
                txtTotal.setText(String.valueOf(adapter.getListItems().size()));
            }
        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {
                Toast.makeText(ProductDeliveryActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                return;
            }
        });
    }
}
