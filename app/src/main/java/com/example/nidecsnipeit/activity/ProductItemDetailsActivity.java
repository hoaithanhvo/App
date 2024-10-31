package com.example.nidecsnipeit.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.ProductDetailsAdapter;
import com.example.nidecsnipeit.adapter.ProductItemDetailsAdapter;
import com.example.nidecsnipeit.network.model.ProductItemDetailsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductItemDetailsActivity extends BaseActivity {
    private RecyclerView rycProdoductItemDetails;
    private ProductItemDetailsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_item_details);
        setupActionBar("Product Item Details");
        rycProdoductItemDetails = findViewById(R.id.rycProdoductItemDetails);
        Intent intent = getIntent();
        List<ProductItemDetailsModel> productItemDetailsModelList = new ArrayList<>();
        String jsonString = intent.getStringExtra("ITEM_DETAIL_DATA");
        try {
            JSONArray items_request = new JSONArray(jsonString);
            for(int i=0;i<items_request.length();i++){
                ProductItemDetailsModel productItemDetailsModel = new ProductItemDetailsModel();
                JSONObject objectItem = items_request.getJSONObject(i);
                JSONObject asset = objectItem.getJSONObject("asset");
                for(int j = 0;j<asset.length();j++)
                {
                    productItemDetailsModel.setAssetID(String.valueOf(asset.getInt("id")));
                    productItemDetailsModel.setAssetTag(asset.getString("asset_tag"));
                    productItemDetailsModel.setSerial(asset.getString("serial"));
                    productItemDetailsModel.setName(asset.getString("name"));
                }
                productItemDetailsModelList.add(productItemDetailsModel);
            }
            adapter = new ProductItemDetailsAdapter(productItemDetailsModelList);
            rycProdoductItemDetails.setAdapter(adapter);
            rycProdoductItemDetails.setLayoutManager(new LinearLayoutManager(ProductItemDetailsActivity.this));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}