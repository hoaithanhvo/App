package com.example.nidecsnipeit.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.AuditRFIDAdapter;
import com.example.nidecsnipeit.adapter.ExportGoodsAdapter;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.network.model.ProductDetailsModel;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import okhttp3.internal.http.RetryAndFollowUpInterceptor;

public class ExportGoodsActivity extends BaseActivity {
    private ExportGoodsAdapter adapter;
    private RecyclerView rycSerial,recycleListDataScan;
    private ImageView scan_img_btn;
    private EditText input_scan;
    private List<String> listScan = new ArrayList<>();
    private AuditRFIDAdapter listScanAdapter;
    private Button btnExportGoods;
    private NetworkManager apiServices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setupActionBar(R.string.export_goods);
        setContentView(R.layout.activity_export_goods);
        scan_img_btn= findViewById(R.id.scan_img_btn);
        input_scan = findViewById(R.id.input_scan);
        rycSerial = findViewById(R.id.rycSerial);
        btnExportGoods = findViewById(R.id.btnExportGoods);
        recycleListDataScan = findViewById(R.id.recycleListDataScan);
        Intent intent = getIntent();
        ArrayList<String> serialList = intent.getStringArrayListExtra("serialList");
        adapter = new ExportGoodsAdapter(serialList);
        rycSerial.setAdapter(adapter);
        apiServices = NetworkManager.getInstance(this);
        rycSerial.setLayoutManager(new LinearLayoutManager(ExportGoodsActivity.this));
        scan_img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    addItemtoRecyclerView();
                }
                catch(Exception ex){
                    Toast.makeText(ExportGoodsActivity.this,"Lỗi:"+ ex.getMessage(),Toast.LENGTH_LONG).show();
                    Log.e("Lỗi:", ex.getMessage());
                }
            }
        });

        listScanAdapter = new AuditRFIDAdapter(listScan,AuditRFIDAdapter.RFIDview);
        recycleListDataScan.setAdapter(listScanAdapter);
        recycleListDataScan.setLayoutManager(new LinearLayoutManager(ExportGoodsActivity.this));
        btnExportGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!compareListData(listScanAdapter.listitemRFID,serialList)){
                    Toast.makeText(ExportGoodsActivity.this,"Đã quét không trùng khớp vui lòng quét lại",Toast.LENGTH_LONG).show();
                    listScanAdapter.listitemRFID.clear();
                    return;
                }
                apiServices.patchCheckoutItemRequest(ProductDetailsModel.item_request_id, new NetworkResponseListener<JSONObject>() {
                    @Override
                    public void onResult(JSONObject object) {
                        try {
                            String status = object.getString("status");
                            Toast.makeText(ExportGoodsActivity.this,status,Toast.LENGTH_LONG).show();
                            Intent intent1 = new Intent(ExportGoodsActivity.this,ProductDeliveryActivity.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent1);
                        } catch (JSONException e) {
                            Toast.makeText(ExportGoodsActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }, new NetworkResponseErrorListener() {
                    @Override
                    public void onErrorResult(Exception error) {
                        Toast.makeText(ExportGoodsActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    private void addItemtoRecyclerView(){
        String ScanData = input_scan.getText().toString();
        if(ScanData.equals("")){
            Toast.makeText(ExportGoodsActivity.this, "Chưa quét", Toast.LENGTH_SHORT).show();
            return;
        }
        listScanAdapter.addItemTop(ScanData, Color.BLACK);
        input_scan.setText("");
        Toast.makeText(ExportGoodsActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
    }

    private boolean compareListData(List<String> listDataScan, ArrayList<String> serialList) {
        if (listDataScan != null && serialList != null && serialList.size()>0 && listDataScan.size()>0 ) {
            List<String> lowerCaseListDataScan = listDataScan.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            List<String> lowerCaseSerialList = serialList.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            boolean areEqual = lowerCaseListDataScan.size() == lowerCaseSerialList.size() &&
                    lowerCaseListDataScan.containsAll(lowerCaseSerialList);
            return areEqual;
        }
        return false;
    }}
