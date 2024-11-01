package com.example.nidecsnipeit.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.AuditRFIDAdapter;
import com.example.nidecsnipeit.adapter.CaterogyAdapter;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.network.model.BasicItemModel;
import com.example.nidecsnipeit.network.model.CategoryFieldModel;
import com.example.nidecsnipeit.network.model.ImportAssetModel;
import com.example.nidecsnipeit.utility.Common;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Import_AssetActivity extends BaseActivity {
    private ImageView scan_img_btn;
    private EditText input_scan;
    private RecyclerView recycleListDataScan;
    private AuditRFIDAdapter listScanAdapter;
    private Button importAsset;
    private List<String> listScan = new ArrayList<>();
    List<String> listCategory = new ArrayList<>();
    private List<String> listManafactory = new ArrayList<>();
    private List<String> listCatalog = new ArrayList<>();
    private List<String> listVarrials = new ArrayList<>();
    private List<String> listVarrialsCustom = new ArrayList<>();

    private  List<ImportAssetModel> dataPost = new ArrayList<>();
    private NetworkManager apiServices;
    private Map<String,Integer> categoryMap = new HashMap<>();
    private Map<String,Integer> manufactoryMap = new HashMap<>();
    private Map<String,Integer> catalogMap = new HashMap<>();
    private Map<String,Integer> varrialsMap = new HashMap<>();
    private  int selectedCategoryId;
    private  int selectedManufactoryId;
    private AutoCompleteTextView autoCategory,autoManufactures,autoCatalog,autoVarials;
    //private MultiAutoCompleteTextView autoVarials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_import_asset);
        apiServices = NetworkManager.getInstance();
        scan_img_btn= findViewById(R.id.scan_img_btn);
        setupActionBar("Import Asset");
        initial();
        loadCategories();
        setupFocusChangeListeners();
        setupOnClickListener();
        listScanAdapter = new AuditRFIDAdapter(listScan);
        recycleListDataScan.setAdapter(listScanAdapter);
        recycleListDataScan.setLayoutManager(new LinearLayoutManager(Import_AssetActivity.this));
    }
    private void addItemtoRecyclerView(){
        String ScanData = input_scan.getText().toString();
        if(ScanData.equals("")){
            Toast.makeText(Import_AssetActivity.this, "Chưa quét", Toast.LENGTH_SHORT).show();
            return;
        }
        listScanAdapter.addItemTop(ScanData, Color.GREEN);
        input_scan.setText("");
        Toast.makeText(Import_AssetActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
    }
    private void setupOnClickListener(){
        scan_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    addItemtoRecyclerView();
                }
                catch(Exception ex){
                    Toast.makeText(Import_AssetActivity.this,"Lỗi:"+ ex.getMessage(),Toast.LENGTH_LONG).show();
                    Log.e("Lỗi:", ex.getMessage());
                }
            }
        });
        importAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    ImportAssetModel importAssetModel = new ImportAssetModel();
                    importAssetModel.category_id = String.valueOf(categoryMap.get(autoCategory.getText().toString()));
                    importAssetModel.varrial_id = String.valueOf(varrialsMap.get(autoVarials.getText().toString()));
                    importAssetModel.name = autoCategory.getText().toString()+autoManufactures.getText()+autoCatalog.getText();
                    for (String itemScan : listScan) {
                        importAssetModel.addAsset(itemScan);
                    }
                    if(!importAssetModel.category_id.isEmpty()){
                        try{
                            apiServices.postAssetObject(importAssetModel, new NetworkResponseListener<JSONObject>() {
                                @Override
                                public void onResult(JSONObject object) {
                                    try {
                                        String status = object.getString("status");
                                        String messages = object.getString("messages");
                                        if(status.equals("success")){
                                            listScan.clear();
                                            autoCategory.setText("");
                                            autoManufactures.setText("");
                                            autoCatalog.setText("");
                                            autoVarials.setText("");
                                            listScanAdapter.notifyDataSetChanged();
                                            Toast.makeText(Import_AssetActivity.this,messages,Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            Toast.makeText(Import_AssetActivity.this,messages,Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(Import_AssetActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                        throw new RuntimeException(e);
                                    }
                                }
                            }, new NetworkResponseErrorListener() {
                                @Override
                                public void onErrorResult(Exception error) {
                                }
                            });
                        }catch (Exception ex){
                            Toast.makeText(Import_AssetActivity.this,ex.getMessage().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }

                }catch(Exception ex){
                    Toast.makeText(Import_AssetActivity.this,"Lỗi:"+ex.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void  initial(){
        input_scan = findViewById(R.id.input_scan);
        //scan_img_btn = findViewById(R.id.scan_img_btn);
        recycleListDataScan = findViewById(R.id.recycleListDataScan);
        autoCategory = findViewById(R.id.autoCategory);
        autoManufactures = findViewById(R.id.autoManufactures);
        autoCatalog = findViewById(R.id.autoCatalog);
        autoVarials = findViewById(R.id.autoVarials);
        importAsset = findViewById(R.id.Import_Asset);
    }
    private void loadCategories() {
        apiServices.getCategoryAll(new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                handleCategoryResponse(object);
            }
        }, this::handleApiError);  // Sử dụng method reference để xử lý lỗi
    }
    private void handleCategoryResponse(JSONObject object) {
        try {
            JSONArray row = object.getJSONArray("rows");
            for (int i = 0; i < row.length(); i++) {
                JSONObject item = row.getJSONObject(i);
                String categoryName = item.optString("name", null);
                int categoryId = item.optInt("id", -1);
                if (categoryName != null && categoryId != -1) {
                    listCategory.add(categoryName);
                    categoryMap.put(categoryName, categoryId);
                }
            }
            setupAutoCompleteAdapter(autoCategory, listCategory);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    private void setupAutoCompleteAdapter(AutoCompleteTextView view, List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, data);
        view.setAdapter(adapter);
    }
    private void setupMuilteCompleteAdapter(MultiAutoCompleteTextView view, List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        view.setAdapter(adapter);
        view.setThreshold(1);
        view.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }
    private void setupFocusChangeListeners() {
        autoCategory.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                handleCategorySelection();
            }
        });

        autoManufactures.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                handleManufactureSelection();
            }
        });

        autoCatalog.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                handleCatalogSelection();
            }
        });
    }
    // Xử lý khi chọn Category
    private void handleCategorySelection() {
        String inputCategory = autoCategory.getText().toString();
        if (listCategory.contains(inputCategory)) {
            selectedCategoryId = categoryMap.get(inputCategory);
            Common.showProgressDialog(this, "Loading...");
            loadManufactures();
        } else {
            autoManufactures.setText("");
            Toast.makeText(this, "Không có trong danh sách Category", Toast.LENGTH_LONG).show();
        }
    }
    // Gọi API để tải Manufacture
    private void loadManufactures() {
        listManafactory.clear();
        manufactoryMap.clear();
        apiServices.getManafactoryAll(selectedCategoryId,new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                handleManufactureResponse(object);
                Common.hideProgressDialog();
            }
        }, this::handleApiError);
    }
    // Xử lý khi chọn Manufacture
    private void handleManufactureSelection() {
        String getManufactory = autoManufactures.getText().toString();
        if (listManafactory.contains(getManufactory)) {
            selectedManufactoryId = manufactoryMap.get(getManufactory);
            Common.showProgressDialog(this, "Loading...");
            loadCatalogs();
        }
    }
    // Gọi API để tải Catalog
    private void loadCatalogs() {
        listCatalog.clear();
        catalogMap.clear();
        apiServices.getCatalogAll(selectedManufactoryId, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                handleCatalogResponse(object);
                Common.hideProgressDialog();
            }
        }, this::handleApiError);
    }
    // Xử lý khi chọn Catalog
    private void handleCatalogSelection() {
        String getCatalog = autoCatalog.getText().toString();
        if (listCatalog.contains(getCatalog)) {
            int catalogID = catalogMap.get(getCatalog);
            Common.showProgressDialog(this, "Loading...");
            loadVarrials(catalogID);
        }
    }
    // Gọi API để tải Varrials
    private void loadVarrials(int catalogID) {
        listVarrials.clear();
        apiServices.getVarrials(catalogID, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                handleVarrialsResponse(object);
                Common.hideProgressDialog();
            }
        }, this::handleApiError);
    }
    // Xử lý API Varrials response
    private void handleVarrialsResponse(JSONObject object) {
        try {
            JSONArray rows = object.getJSONArray("rows");
            for (int i = 0; i < rows.length(); i++) {
                JSONObject item = rows.getJSONObject(i);
                String name = item.optString("name");
                int id = item.optInt("id");
                listVarrials.add(name);
                varrialsMap.put(name,id);
            }
            setupAutoCompleteAdapter(autoVarials, listVarrials);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    // Xử lý lỗi API
    private void handleApiError(Exception error) {
        Log.e("API Error", "Error calling API: " + error.getMessage());
        Toast.makeText(this, "Lỗi API: " + error.getMessage(), Toast.LENGTH_LONG).show();
        if (error instanceof AuthFailureError) {
            Log.e("Auth Error", "Authentication failure: " + error.getMessage());
        }
    }
    // Xử lý response của API Manufacture
    private void handleManufactureResponse(JSONObject object) {
        try {
            JSONArray row = object.getJSONArray("rows");
            for (int i = 0; i < row.length(); i++) {
                JSONObject item = row.getJSONObject(i);
                String manufactureName = item.optString("name", null);
                int manufactureId = item.optInt("id", -1);
                if (manufactureName != null && manufactureId != -1) {
                    listManafactory.add(manufactureName);
                    manufactoryMap.put(manufactureName, manufactureId);
                }
            }
            setupAutoCompleteAdapter(autoManufactures, listManafactory);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    // Xử lý response của API Catalog
    private void handleCatalogResponse(JSONObject object) {
        try {
            JSONArray row = object.getJSONArray("rows");
            for (int i = 0; i < row.length(); i++) {
                JSONObject item = row.getJSONObject(i);
                String catalogName = item.optString("catalog_code", null);
                int catalogId = item.optInt("id", -1);
                if (catalogName != null && catalogId != -1) {
                    listCatalog.add(catalogName);
                    catalogMap.put(catalogName, catalogId);
                }
            }
            setupAutoCompleteAdapter(autoCatalog, listCatalog);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}