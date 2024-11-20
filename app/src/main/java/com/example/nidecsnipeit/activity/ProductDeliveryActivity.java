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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.ProductDeliveryAdapter;
import com.example.nidecsnipeit.network.ApiManager;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.network.model.ProductDeliveryModel;
import com.example.nidecsnipeit.utility.Common;
import com.example.nidecsnipeit.utility.EndlessScrollListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDeliveryActivity extends BaseActivity {
    private NetworkManager apiServices;
    private RecyclerView rcyProductDelivery;
    private ProductDeliveryAdapter productDeliveryAdapter;
    private List<ProductDeliveryModel> productList = new ArrayList<>();
    private TextView txtTotal;
    private EditText txtSearch;
    private Boolean hasMoreData = true;
    private EndlessScrollListener endlessScrollListener;
    private List<ProductDeliveryModel> originalList = new ArrayList<>();
    private ImageButton imgClose;
    private Button bntSearch;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final int SCREEN_B_REQUEST_CODE = 1;
    private boolean isswipeRefreshLayout = false;
    private ApiManager apiManager;
    private Map<String, String> params = new HashMap() {
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setupActionBar(R.string.equipment_request);
        setContentView(R.layout.activity_product_delivery);
        Initialize();
        apiManager = ApiManager.getInstance(this);
        productDeliveryAdapter = new ProductDeliveryAdapter();
        params.put("offset", "0");
        params.put("limit", "10");
        GetDataProductDelivery(params);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rcyProductDelivery.setAdapter(productDeliveryAdapter);
        rcyProductDelivery.setLayoutManager(layoutManager);
        // Initialize and set EndlessScrollListener
        endlessScrollListener = new EndlessScrollListener(layoutManager) {
            @Override
            public void loadMoreItems(int offset, int limit) {
                params.clear();
                params.put("offset", String.valueOf(offset));
                params.put("limit", String.valueOf(limit));
                txtTotal.setText(String.valueOf(productDeliveryAdapter.getListItems().size()));
                if (hasMoreData) {
                    GetDataProductDelivery(params);
                }
            }
        };
        rcyProductDelivery.addOnScrollListener(endlessScrollListener);
        productDeliveryAdapter.setOnItemClickListener(product -> {
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
            startActivityForResult(intent, 1);
        }, ProductDeliveryActivity.this);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearch.setText("");
            }
        });
        bntSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItemById();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(() -> {
            isswipeRefreshLayout = true;
            productList.clear();
            params.clear();
            params.put("offset", "0");
            params.put("limit", "10");
            GetDataProductDelivery(params);
            swipeRefreshLayout.setRefreshing(false);

        });
    }

    private void Initialize() {
        rcyProductDelivery = findViewById(R.id.rcyProductDelivery);
        txtSearch = findViewById(R.id.txtSearch);
        bntSearch = findViewById(R.id.bntSearch);
        txtTotal = findViewById(R.id.txtTotal);
        imgClose = findViewById(R.id.imgClose);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }
    private void GetDataProductDelivery(Map<String, String> params) {
        Common.showProgressDialog(this, "Đang tải vui lòng đợi...");
        apiManager.getProductDelivery(params, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                parsObject(object);
            }
        }, error -> {
            Toast.makeText(ProductDeliveryActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
    private void filter(String text) {
        productList.clear();
        endlessScrollListener.setLoading(false);
        endlessScrollListener.setSearching(true);
        if (text.isEmpty()) {
            productList.addAll(originalList);
            endlessScrollListener.setLoading(true);
            endlessScrollListener.setSearching(false);
        } else {
            for (ProductDeliveryModel item : originalList) {
                if (item.getProductID() != null && item.getProductID().toLowerCase().contains(text.toLowerCase())) {
                    productList.add(item);
                }
            }
        }
        productDeliveryAdapter.searchData(productList);
        txtTotal.setText(String.valueOf(productList.size()));
    }
    private void searchItemById() {
        productList.clear();
        endlessScrollListener.setSearching(true);
        endlessScrollListener.setLoading(false);
        String textSearch = txtSearch.getText().toString();
        if (textSearch.equals("")) {
            endlessScrollListener.setSearching(false);
            endlessScrollListener.setLoading(true);
            productList.addAll(originalList);
            productDeliveryAdapter.searchData(productList);
        }
        apiServices.getRequestAssetById(textSearch, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                parsObject(object);
                productDeliveryAdapter.searchData(productList);
                txtTotal.setText(String.valueOf(productDeliveryAdapter.getListItems().size()));
            }
        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {
                Toast.makeText(ProductDeliveryActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCREEN_B_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Kiểm tra cờ dữ liệu có thay đổi từ Screen B
                boolean isDataChanged = data.getBooleanExtra("isDataChanged", false);
                if (isDataChanged) {
                    productList.clear();
                    originalList.clear();
                    params.clear();
                    params.put("offset", "0");
                    params.put("limit", "10");

                    GetDataProductDelivery(params);
                }
            }
        }
    }
    private void parsObject(JSONObject object) {
        try {
            Common.hideProgressDialog();
            JSONArray rows = object.getJSONArray("rows");
            // Kiêm tra data get dưới item va khong tim kiem bang id
            if (rows.length() < 10 && !endlessScrollListener.isSearching() && !isswipeRefreshLayout) {
                hasMoreData = false;
            }
            if (isswipeRefreshLayout) {
                hasMoreData = true;
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
                if(!endlessScrollListener.isSearching() && !isswipeRefreshLayout){
                    originalList.add(productItem);
                }
            }
            productDeliveryAdapter.searchData(productList);
            txtTotal.setText(String.valueOf(productDeliveryAdapter.getListItems().size()));
            endlessScrollListener.setLoading(true);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
