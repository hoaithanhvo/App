package com.example.nidecsnipeit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.lifecycle.viewmodel.InitializerViewModelFactoryKt;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.ProductDetailsAdapter;
import com.example.nidecsnipeit.network.model.ProductDeliveryModel;
import com.example.nidecsnipeit.network.model.ProductDetailsModel;
import com.example.nidecsnipeit.network.model.ProductItemDetailsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends BaseActivity {
    private RecyclerView rcyProductDetails;
    private ProductDetailsAdapter adapter;
    private TextView txtTotal;
    private EditText txtSearch;
    private List<ProductDetailsModel> listProductDetails = new ArrayList<>();
    private List<ProductDetailsModel> filteredList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        rcyProductDetails = findViewById(R.id.rcyProductDetails);
        txtTotal = findViewById(R.id.txtTotal);
        txtSearch=findViewById(R.id.txtSearch);
        setupActionBar(R.string.equipment_detail);
        try{
            Intent intent = getIntent();
            String jsonString = intent.getStringExtra("ITEM_DATA");
            loadDataAdapter(jsonString);
            txtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    fillter(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        catch(Exception error){
            Toast.makeText(ProductDetailActivity.this,error.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

    }

    private void loadDataAdapter(String jsonString){
        try {
            JSONArray items_request = new JSONArray(jsonString);
            for(int i = 0;i<items_request.length();i++){
                ProductDetailsModel itemProduct = new ProductDetailsModel();
                JSONObject item = items_request.getJSONObject(i);
                ProductDetailsModel.item_request_id = item.getInt("id");
                JSONObject Created_at = item.getJSONObject("created_at");
                String formatted = Created_at.getString("formatted");
                String nameCategory = "";
                String nameManufacturer = "";
                String nameVarrial = "";
                String nameModel = "";
                String nameStatus = "";
                String colorStatus = "";
                String total = item.getString("total");
                JSONObject category = item.optJSONObject("category");
                if (category == null) {
                    nameCategory = "null";
                } else {
                    for(int j = 0 ; j< category.length();j++){
                        nameCategory = category.getString("name");
                    }
                }
                JSONObject manufacturer = item.optJSONObject("manufacturer");
                if (manufacturer == null) {
                    nameManufacturer = "null";
                } else {
                    for(int j = 0 ; j< manufacturer.length();j++){
                        nameManufacturer = manufacturer.getString("name");
                    }
                }

                JSONObject varrial = item.optJSONObject("varrial");
                if (varrial == null) {
                    nameVarrial = "null";
                } else {
                    for(int j = 0 ; j< varrial.length();j++){
                        nameVarrial = varrial.getString("name");
                    }
                }

                JSONObject model = item.optJSONObject("model");
                if (model == null) {
                    nameModel = "null";
                } else {
                    for(int j = 0 ; j< model.length();j++){
                        nameModel = model.getString("name");
                    }
                }

                JSONObject status = item.getJSONObject("status");
                for (int j=0;j<status.length();j++){
                    nameStatus = status.getString("name");
                    colorStatus=status.getString("color");
                }
                JSONArray item_request_details = item.getJSONArray("item_request_details");
                itemProduct.setCreated(formatted);
                itemProduct.setCategory(nameCategory);
                itemProduct.setManufactory(nameManufacturer);
                itemProduct.setVarrial(nameVarrial);
                itemProduct.setModel(nameModel);
                itemProduct.setStatusMap(nameStatus,colorStatus);
                itemProduct.setTotal(total);
                itemProduct.setItem_request_details(item_request_details);
                listProductDetails.add(itemProduct);
            }
            txtTotal.setText(String.valueOf(listProductDetails.size()));
            filteredList = new ArrayList<>(listProductDetails);
            adapter = new ProductDetailsAdapter(filteredList,ProductDetailActivity.this);
            rcyProductDetails.setAdapter(adapter);
            rcyProductDetails.setLayoutManager(new LinearLayoutManager(ProductDetailActivity.this));
            adapter.setOnItemClickListener(product -> {
                Intent intentItem = new Intent(ProductDetailActivity.this, ProductItemDetailsActivity.class);
                JSONArray itemsRequest = product.getItem_request_details();
                String itemsRequestString = itemsRequest.toString();
                intentItem.putExtra("productDetails", product);
                intentItem.putExtra("ITEM_DETAIL_DATA", itemsRequestString);
                startActivity(intentItem);
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void fillter(String text){
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(listProductDetails);
        } else {
            for (ProductDetailsModel item : listProductDetails) {
                if (item.getCategory().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}