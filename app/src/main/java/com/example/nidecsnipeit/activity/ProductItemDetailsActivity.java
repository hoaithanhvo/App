package com.example.nidecsnipeit.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.AuditRFIDAdapter;
import com.example.nidecsnipeit.adapter.ProductDetailsAdapter;
import com.example.nidecsnipeit.adapter.ProductItemDetailsAdapter;
import com.example.nidecsnipeit.model.Asset;
import com.example.nidecsnipeit.model.checkoutItemRequestModel;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.network.model.ImportAssetModel;
import com.example.nidecsnipeit.network.model.ProductDetailsModel;
import com.example.nidecsnipeit.network.model.ProductItemDetailsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductItemDetailsActivity extends BaseActivity {
    private RecyclerView rycProdoductItemDetails ,recycleListDataScan;
    private ProductDetailsAdapter adapter;
    private AuditRFIDAdapter listScanAdapter;
    private Button btnExportGoods,btnConfirm;
    private ImageView scan_img_btn;
    private EditText input_scan;
    private List<String> listScanData = new ArrayList<>();
    private ProductDetailsModel productDetails;
    private NetworkManager apiServices;
    private checkoutItemRequestModel checkoutcheckoutItemRequestModel = new checkoutItemRequestModel();
    private List<checkoutItemRequestModel> ListcheckoutItemRequestModels = new ArrayList<>();
    private List<Asset> listAsset = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_item_details);
        setupActionBar(R.string.scan_assign_asset);
        btnExportGoods = findViewById(R.id.btnExportGoods);
        btnConfirm=findViewById(R.id.btnConfirm);
        scan_img_btn = findViewById(R.id.scan_img_btn);
        rycProdoductItemDetails = findViewById(R.id.rycProdoductItemDetails);
        input_scan = findViewById(R.id.input_scan);
        recycleListDataScan = findViewById(R.id.recycleListDataScan);
        Intent intent = getIntent();
        productDetails = intent.getParcelableExtra("productDetails");
        int id = intent.getIntExtra("id",0);
        List<ProductDetailsModel> listProductItemDetails = new ArrayList<>();
        listProductItemDetails.add(productDetails);
        adapter = new ProductDetailsAdapter(listProductItemDetails, ProductItemDetailsActivity.this);
        rycProdoductItemDetails.setAdapter(adapter);
        rycProdoductItemDetails.setLayoutManager(new LinearLayoutManager(ProductItemDetailsActivity.this));
        listScanAdapter = new AuditRFIDAdapter(listScanData,AuditRFIDAdapter.ProductItemDetailsView);
        recycleListDataScan.setAdapter(listScanAdapter);
        apiServices = NetworkManager.getInstance(this);
        recycleListDataScan.setLayoutManager(new LinearLayoutManager(ProductItemDetailsActivity.this));

        String item_request_details = intent.getStringExtra("ITEM_DETAIL_DATA");
        try {
            productDetails.setHandOver(String.valueOf(new JSONArray(item_request_details).length()));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addObject();
                checkoutcheckoutItemRequestModel.setItem_request_id(id);
                checkoutcheckoutItemRequestModel.setAssets(listAsset);
                ListcheckoutItemRequestModels.clear();
                ListcheckoutItemRequestModels.add(checkoutcheckoutItemRequestModel);
                listScanData.clear();
                apiServices.patchuSccessItemRequest(ListcheckoutItemRequestModels, new NetworkResponseListener<JSONObject>() {
                    @Override
                    public void onResult(JSONObject object) {
                        String mess = null;
                        try {
                            mess = object.getString("messages");
                            Toast.makeText(ProductItemDetailsActivity.this,mess,Toast.LENGTH_LONG).show();
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
        });
        btnExportGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addObject();

                checkoutcheckoutItemRequestModel.setItem_request_id(id);
                checkoutcheckoutItemRequestModel.setAssets(listAsset);
                ListcheckoutItemRequestModels.clear();
                ListcheckoutItemRequestModels.add(checkoutcheckoutItemRequestModel);
                apiServices.patchCheckoutItemRequest(ListcheckoutItemRequestModels, new NetworkResponseListener<JSONObject>() {
                    @Override
                    public void onResult(JSONObject object) {
                        try {
                            String mess = object.getString("messages");
                            Toast.makeText(ProductItemDetailsActivity.this,mess,Toast.LENGTH_LONG).show();
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
        });
        scan_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemScan();
            }
        });

    }
    private  void addObject(){
        listAsset.clear();
        for(int i = 0;i<listScanData.size();i++){
            Asset asset = new Asset();
            asset.setSearch(listScanData.get(i));
            listAsset.add(asset);
        }
    }
    private void AddItemScan(){
        String itemScan = input_scan.getText().toString();
        if(itemScan.equals("")){
            Toast.makeText(this,"Trống",Toast.LENGTH_LONG).show();
            return;
        }
//        if(Integer.parseInt(productDetails.getTotal())<=listScanData.size()){
//            Toast.makeText(this,"Vượt quá số lượng yêu cầu",Toast.LENGTH_LONG).show();
//            return;
//        }
        listScanAdapter.addItemTop(itemScan, Color.BLACK);
        input_scan.setText("");
        recycleListDataScan.scrollToPosition(listScanData.size()-1);
    }
}