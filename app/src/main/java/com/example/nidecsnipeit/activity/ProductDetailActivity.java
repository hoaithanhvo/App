package com.example.nidecsnipeit.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.ProductDetailsAdapter;
import com.example.nidecsnipeit.network.model.ProductDetailsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends BaseActivity {
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
                ProductDetailsModel itemProduct = new ProductDetailsModel();
                JSONObject item = items_request.getJSONObject(i);
                JSONObject Created_at = item.getJSONObject("created_at");
                String formatted = Created_at.getString("formatted");
                JSONObject category = item.getJSONObject("category");
                String nameCategory="";
                for(int j = 0 ; j< category.length();j++){
                    nameCategory = category.getString("name");
                }
                String manufacturer = item.getString("manufacturer");
                String catalog = item.getString("catalog");
                String varrial = item.getString("varrial");
                //String total = item.getString("total");
                itemProduct.setCreated(formatted);
                itemProduct.setCatalog(nameCategory);
                itemProduct.setManufactory(manufacturer);
                itemProduct.setVarrial(varrial);
                itemProduct.setCatalog(catalog);

//                JSONArray item_request_details = item.getJSONArray("item_request_details");
//                for(int j = 0;j<item_request_details.length();j++){
//                    JSONObject item_request_details_item = item_request_details.getJSONObject(j);
//
//                    JSONObject asset = item_request_details_item.getJSONObject("asset");
//                    itemProduct.setAssetID(asset.getInt("id"));
//                    itemProduct.setAssetTag(asset.getString("asset_tag"));
//                    itemProduct.setSerial(asset.getString("serial"));
//                    itemProduct.setName(asset.getString("name"));
//                    itemProduct.setCreatedAt(formatted);
//                    listProductDetails.add(itemProduct);
//                }
                adapter = new ProductDetailsAdapter(listProductDetails);
                rcyProductDetails.setAdapter(adapter);
                rcyProductDetails.setLayoutManager(new LinearLayoutManager(ProductDetailActivity.this));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}