package com.example.nidecsnipeit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.ProductDeliveryAdapter;
import com.example.nidecsnipeit.adapter.ProductDetailsAdapter;
import com.example.nidecsnipeit.network.model.ProductDeliveryModel;
import com.example.nidecsnipeit.network.model.ProductDetailsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Product_DetailActivity extends BaseActivity {
    private RecyclerView rcyProductDetails;
    private ProductDetailsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        rcyProductDetails = findViewById(R.id.rcyProductDetails);
        setupActionBar("Product Details");
        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("ITEM_DATA");

        try {
            JSONArray items_request = new JSONArray(jsonString);
            List<ProductDetailsModel> listProductDetails  = new ArrayList<>();
            for(int i = 0;i<items_request.length();i++){

                JSONObject item = items_request.getJSONObject(i);
                JSONObject Created_at = item.getJSONObject("created_at");
                String formatted = Created_at.getString("formatted");
                JSONArray item_request_details = item.getJSONArray("item_request_details");
                for(int j = 0;j<item_request_details.length();j++){
                    ProductDetailsModel itemProduct = new ProductDetailsModel();
                    JSONObject item_request_details_item = item_request_details.getJSONObject(j);

                    JSONObject asset = item_request_details_item.getJSONObject("asset");
                    itemProduct.setAssetID(asset.getInt("id"));
                    itemProduct.setAssetTag(asset.getString("asset_tag"));
                    itemProduct.setSerial(asset.getString("serial"));
                    itemProduct.setName(asset.getString("name"));
                    itemProduct.setCreatedAt(formatted);
                    listProductDetails.add(itemProduct);
                }
                adapter = new ProductDetailsAdapter(listProductDetails);
                rcyProductDetails.setAdapter(adapter);
                rcyProductDetails.setLayoutManager(new LinearLayoutManager(Product_DetailActivity.this));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}