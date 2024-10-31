package com.example.nidecsnipeit.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.ProductDeliveryAdapter;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.network.model.ProductDeliveryModel;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_delivery);
        apiServices = NetworkManager.getInstance(this);
        rcyProductDelivery = findViewById(R.id.rcyProductDelivery);
        setupActionBar("Product Delivery");
        GetDataProductDelivery();

    }
    private void GetDataProductDelivery()
    {
        apiServices.getProductDelivery(new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                try {
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
                    adapter = new ProductDeliveryAdapter(productList);
                    rcyProductDelivery.setAdapter(adapter);
                    rcyProductDelivery.setLayoutManager(new LinearLayoutManager(ProductDeliveryActivity.this));
                    adapter.setOnItemClickListener(product -> {
                        // Xử lý khi item được click, ví dụ, chuyển sang màn hình chi tiết
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

            }
        });
    }
}