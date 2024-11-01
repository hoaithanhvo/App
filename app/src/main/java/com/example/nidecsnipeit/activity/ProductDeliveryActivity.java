package com.example.nidecsnipeit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductDeliveryActivity extends BaseActivity {
    private NetworkManager apiServices;
    private RecyclerView rcyProductDelivery;
    private ProductDeliveryAdapter adapter;
    private List<ProductDeliveryModel> productList;
    private List<ProductDeliveryModel> filteredList;
    private TextView txtTotal;
    private EditText txtSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_delivery);
        apiServices = NetworkManager.getInstance(this);
        rcyProductDelivery = findViewById(R.id.rcyProductDelivery);
        txtSearch = findViewById(R.id.txtSearch);
        txtTotal = findViewById(R.id.txtTotal);
        setupActionBar("Product Delivery");
        GetDataProductDelivery();
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

    }
    private void GetDataProductDelivery()
    {
        Common.showProgressDialog(this,"Đang tải vui lòng đợi...");
        apiServices.getProductDelivery(new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                try {
                    Common.hideProgressDialog();
                    JSONArray rows  = object.getJSONArray("rows");
                    productList= new ArrayList<>();
                    for(int i =0;i<rows.length();i++){
                        ProductDeliveryModel productItem  = new ProductDeliveryModel();
                        JSONObject item = rows.getJSONObject(i);
                        String id = item.getString("id");
                        JSONObject user_request = item.getJSONObject("user_request");
                        JSONObject created_at = item.getJSONObject("created_at");
                        String formatted = created_at.getString("formatted");
                        productItem.setUserID(user_request.getString("name"));
                        productItem.setCreateAt(formatted);
                        productItem.setProductID(id);
                        JSONArray items_request = item.getJSONArray("items_request");
                        productItem.setItems_request(items_request);
                        productList.add(productItem);
                    }
                    txtTotal.setText(String.valueOf(productList.size()));
                    filteredList = new ArrayList<>(productList);
                    adapter = new ProductDeliveryAdapter(filteredList);
                    rcyProductDelivery.setAdapter(adapter);
                    rcyProductDelivery.setLayoutManager(new LinearLayoutManager(ProductDeliveryActivity.this));
                    adapter.setOnItemClickListener(product -> {
                        Intent intent = new Intent(ProductDeliveryActivity.this, ProductDetailActivity.class);
                        JSONArray itemsRequest = product.getItems_request();
                        String jsonString = itemsRequest.toString();
                        intent.putExtra("ITEM_DATA", jsonString);  // ProductDeliveryModel cần Serializable hoặc Parcelable
                        startActivity(intent);
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {
                Toast.makeText(ProductDeliveryActivity.this,"Lỗi:" +error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(productList);
        } else {
            for (ProductDeliveryModel item : productList) {
                if (item.getProductID().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}