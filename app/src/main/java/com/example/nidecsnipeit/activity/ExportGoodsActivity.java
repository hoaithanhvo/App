package com.example.nidecsnipeit.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

public class ExportGoodsActivity extends BaseActivity {
    private ExportGoodsAdapter adapter;
    private RecyclerView rycSerial,recycleListDataScan;
    private ImageView scan_img_btn;
    private EditText input_scan;
    private List<String> listScan = new ArrayList<>();
    private AuditRFIDAdapter listScanAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setupActionBar("Export Goods");
        setContentView(R.layout.activity_export_goods);
        scan_img_btn= findViewById(R.id.scan_img_btn);
        input_scan = findViewById(R.id.input_scan);
        Intent intent = getIntent();
        ArrayList<String> serialList = intent.getStringArrayListExtra("serialList");
        rycSerial = findViewById(R.id.rycSerial);
        adapter = new ExportGoodsAdapter(serialList);
        recycleListDataScan = findViewById(R.id.recycleListDataScan);
        rycSerial.setAdapter(adapter);
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
        listScanAdapter = new AuditRFIDAdapter(listScan);
        recycleListDataScan.setAdapter(listScanAdapter);
        recycleListDataScan.setLayoutManager(new LinearLayoutManager(ExportGoodsActivity.this));
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
}