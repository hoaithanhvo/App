package com.example.nidecsnipeit.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.densowave.scannersdk.BuildConfig;
import com.densowave.scannersdk.Common.CommException;
import com.densowave.scannersdk.Common.CommManager;
import com.densowave.scannersdk.Common.CommScanner;
import com.densowave.scannersdk.Dto.RFIDScannerSettings;
import com.densowave.scannersdk.Listener.RFIDDataDelegate;
import com.densowave.scannersdk.Listener.ScannerAcceptStatusListener;
import com.densowave.scannersdk.RFID.RFIDData;
import com.densowave.scannersdk.RFID.RFIDDataReceivedEvent;
import com.densowave.scannersdk.RFID.RFIDException;
import com.densowave.scannersdk.RFID.RFIDScanner;
import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.AuditRFIDAdapter;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.network.model.AuditModel;
import com.example.nidecsnipeit.network.model.GetModelParamItemModel;
import com.example.nidecsnipeit.service.LoadingView;
import com.example.nidecsnipeit.service.Sp1Container;
import com.example.nidecsnipeit.utility.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RFID_Activity extends BaseActivity implements ScannerAcceptStatusListener, RFIDDataDelegate {
    Sp1Container Sp1 = null;
    RFIDScanner scanner = null;
    private List<String> listTags = new ArrayList<>();
    private AuditRFIDAdapter listScanAdapter, listAuditAdapter,listRFIDdataAdapter;
    private TextView txtCountScaner, txtAuditCompare, txtUnauditAll;
    private RecyclerView recycleScan, recycleCompare,recycleRFIDaudit;
    private Button bntAudit, bntScan, bntStop, bntClear;
    private List<String> listTagUnAudit = new ArrayList<>();
    private List<String> listTagData = new ArrayList<>();
    private JSONArray rowsUnaudit;
    private List<String> ListResult = new ArrayList<>();
    private List<AuditModel> data = new ArrayList<>();
    private NetworkManager apiServices;
    private CommScanner commScanner = null;
    LottieAnimationView lottieLoadingView;
    private LoadingView loadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rfid);
        setupActionBar("Audit RFID");
        apiServices = NetworkManager.getInstance();
        bntScan = findViewById(R.id.bntScan);
        bntAudit = findViewById(R.id.bntAudit);
        recycleScan = findViewById(R.id.recycleScan);
        recycleCompare = findViewById(R.id.recycleCompare);
        recycleRFIDaudit = findViewById(R.id.recycleRFIDaudit);
        txtCountScaner = findViewById(R.id.CountScaner);
        txtAuditCompare = findViewById(R.id.AuditCompare);
        txtUnauditAll = findViewById(R.id.UnauditAll);
        bntClear = findViewById(R.id.bntClear);
        bntStop = findViewById(R.id.bntStop);
//        lottieLoadingView = findViewById(R.id.lottieLoadingView);

        //ImageView blurOverlay = new ImageView(this);

        CommManager.addAcceptStatusListener(this);
        CommManager.startAccept();

        listAuditAdapter = new AuditRFIDAdapter(listTagUnAudit);
        listScanAdapter = new AuditRFIDAdapter(listTags);
        listRFIDdataAdapter = new AuditRFIDAdapter(listTagData);

        recycleScan.setAdapter(listScanAdapter);
        recycleScan.setLayoutManager(new LinearLayoutManager(RFID_Activity.this));

        recycleCompare.setAdapter(listAuditAdapter);
        recycleCompare.setLayoutManager(new LinearLayoutManager(RFID_Activity.this));

        recycleRFIDaudit.setAdapter(listRFIDdataAdapter);
        recycleRFIDaudit.setLayoutManager(new LinearLayoutManager(RFID_Activity.this));


        //thanh2k add

        bntScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Sp1 = Sp1Container.GetInstance();
                    if(Sp1.GetScanner() == null){
                        Toast.makeText(RFID_Activity.this, "Chưa kết nối SP1" , Toast.LENGTH_LONG).show();
                        return;
                    }
                    scanner = Sp1.GetScanner().getRFIDScanner();
                    scanner.setDataDelegate(RFID_Activity.this);
                    RFIDScannerSettings scannerSettings = scanner.getSettings();
                    scannerSettings.scan.triggerMode = RFIDScannerSettings.Scan.TriggerMode.CONTINUOUS1;
                    scannerSettings.scan.sessionFlag = RFIDScannerSettings.Scan.SessionFlag.S0;
                    scannerSettings.scan.polarization = RFIDScannerSettings.Scan.Polarization.Both;
                    scannerSettings.scan.doubleReading = RFIDScannerSettings.Scan.DoubleReading.Free;
                    scannerSettings.scan.powerSave = false;
                    scannerSettings.scan.powerLevelRead = 30;
                    scannerSettings.scan.powerLevelWrite = 30;
                    scannerSettings.scan.channel = 920L;
                    scannerSettings.scan.qParam = 4;
                    scannerSettings.scan.linkProfile = 4;
                    scanner.setSettings(scannerSettings);
                    byte[] password = new byte[]{0, 0, 0, 0};
                    scanner.openRead(RFIDScannerSettings.RFIDBank.UII, (short) 0, (short) 4, password);
                } catch (RFIDException e) {
                    Toast.makeText(RFID_Activity.this, "Lỗi:" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        bntStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (scanner != null) {
                        scanner.setDataDelegate(null);
                        scanner.close();
                    }
                    Toast.makeText(RFID_Activity.this, "Máy quét đã dừng", Toast.LENGTH_LONG).show();
                } catch (RFIDException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        bntAudit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    scanner.close();
                    for (int i = 0; i < rowsUnaudit.length(); i++) {
                        JSONObject row = rowsUnaudit.getJSONObject(i);
                        AuditModel auditModel = new AuditModel();
                        String RFID_tag = row.optString("RFID_tag", null);
                        String inventory = row.optString("inventory_number", "");
                        String asset_tag = row.optString("asset_tag", "");
                        if (listTagData.contains(RFID_tag)) {
                            auditModel.inventory_number = inventory;
                            auditModel.current_location = "";
                            auditModel.location_id = "";
                            auditModel.asset_tag = asset_tag;
                            auditModel.label_status = "";
                            auditModel.asset_status = "";
                            auditModel.last_used = "";
                            data.add(auditModel);
                        }
                    }
                    if (!data.isEmpty()) {
                        try {
                            apiServices.postAuditObject(data, new NetworkResponseListener<JSONObject>() {
                                @Override
                                public void onResult(JSONObject object) {
                                    try {
                                        if (object.has("status") && object.get("status").equals("error")) {
                                            Common.hideProgressDialog();
                                            Toast.makeText(RFID_Activity.this, object.getString("messages"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Common.hideProgressDialog();
                                            String messageSuccessful = "Insert all asset audit out successfully.";
                                            listTags.clear();
                                            getUnAudit();
                                            Toast.makeText(RFID_Activity.this, messageSuccessful, Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(RFID_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new NetworkResponseErrorListener() {
                                @Override
                                public void onErrorResult(Exception error) {
                                    Toast.makeText(RFID_Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            Toast.makeText(RFID_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RFID_Activity.this, "Bộ nhớ tạm trống không có dữ liệu để cập nhật", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                }
            }
        });
        bntClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listTags.clear();
                listTagData.clear();
                ListResult.clear();
                listRFIDdataAdapter.notifyDataSetChanged();
                listAuditAdapter.notifyDataSetChanged();
                listScanAdapter.notifyDataSetChanged();
                txtAuditCompare.setText("0");
                txtCountScaner.setText("0");
                compareAndHighlightAsync(listTags, listTagUnAudit);
            }
        });
        getUnAudit();

        loadingView = new LoadingView(this, null);
        FrameLayout rootLayout = findViewById(android.R.id.content);

        // Thêm LoadingView vào layout chính
        rootLayout.addView(loadingView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (Sp1.GetScanner() != null) {
                scanner.close();
                //Sp1.DisconnectScanner();
                Toast.makeText(RFID_Activity.this, "Máy quét đã dừng", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(RFID_Activity.this, "Lỗi khi ngắt kết nối máy quét: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onRFIDDataReceived(CommScanner commScanner, RFIDDataReceivedEvent rfidDataReceivedEvent) {
        try {
            for (RFIDData data : rfidDataReceivedEvent.getRFIDData()) {
                byte[] tagUii = data.getUII();
                String tagUiiString = bytesToHex(tagUii);
                byte[] tagData = data.getData();
                String tagDataString = bytesToHex(tagData);
                if (BuildConfig.DEBUG) {
                    Log.d("-DG", "TagUII: " + tagUiiString);
                    Log.d("DWEU-DG", "TagData: " + tagDataString);
                }
                if (tagData.length != 0) {
                    if (!listTags.contains(tagUiiString)) {
                        listTags.add(0, tagUiiString);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listScanAdapter.addItemTop(tagUiiString,Color.BLUE);
                                recycleScan.scrollToPosition(0);
                                txtCountScaner.setText(Integer.toString(listTags.size()));
                                compareAndHighlightAsync(listTags, listTagUnAudit);
                            }
                        });
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("Lỗi Adapter", "Dữ liệu đang quá tải" + ex.toString());
        }
    }
    @Override
    public void OnScannerAppeared(CommScanner commScanner) {
        if (commScanner == null) {
            return;
        }
        try {
            commScanner.claim();
            Sp1 = Sp1Container.GetInstance();
            Sp1.SetConnectedScanner(commScanner);
        } catch (CommException e) {
            e.printStackTrace();
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private void compareAndHighlightAsync(List<String> listTagsScan, List<String> listTagsUnAudit) {
        try {
            int countCompare = 0;
            for (int i = 0; i < listTagsUnAudit.size(); i++) {
                String unAuditTag = listTagsUnAudit.get(i).split("\\|")[0];
                if (listTagsScan.contains(unAuditTag)) {
                    countCompare++;
                    final int finalCountCompare = countCompare;
                    runOnUiThread(() -> txtAuditCompare.setText(String.valueOf(finalCountCompare)));
                    if(!ListResult.contains(unAuditTag)){
                        listRFIDdataAdapter.addItemTop(unAuditTag,Color.GREEN);
                        recycleRFIDaudit.scrollToPosition(0);
                    }
                    ListResult.add(unAuditTag);

                    listAuditAdapter.setItemColor(i, Color.GREEN);
                } else {
                    listAuditAdapter.setItemColor(i, Color.BLACK);
                }
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void getUnAudit() {
        listTagUnAudit = new ArrayList<>();
        //Common.showProgressDialog(RFID_Activity.this,"Loading RFID");
//        if(loadingView !=null){
//            loadingView.show();
//        }
        getUnAuditAsync(500,0);
    }
    public void getUnAuditAsync(int limit, int offset) {
        GetModelParamItemModel model = new GetModelParamItemModel.Builder()
                .setLimit(Integer.toString(limit))
                .setOffset(Integer.toString(offset))
                .setOrder("desc")
                .setStartDate("2023-09-23")
                .setEndDate("2024-10-23").build();
        apiServices.getUnAudit(model, new NetworkResponseListener<JSONObject>() {
            @Override
            public void onResult(JSONObject object) {
                try {
                    int totalItems = object.getInt("total");
                    rowsUnaudit = object.getJSONArray("rows");
                    if (rowsUnaudit != null) {
                        for (int i = 0; i < rowsUnaudit.length(); i++) {
                            JSONObject row = rowsUnaudit.getJSONObject(i);
                            String rfidTag = row.optString("RFID_tag", null);
                            String inventory = row.optString("inventory_number", "");
                            if (rfidTag != null) {
                                listTagUnAudit.add(rfidTag + "| " + inventory);
                            }
                        }
                    }
                    listAuditAdapter.updateData(listTagUnAudit);
                    txtUnauditAll.setText(Integer.toString(listTagUnAudit.size()));
                    if(offset + limit< totalItems){
                        getUnAuditAsync(limit, offset + limit);
                    }
                    else{
                        //Common.hideProgressDialog();
                       //loadingView.hide();
                    }
                } catch (JSONException e) {
                    Toast.makeText(RFID_Activity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Common.hideProgressDialog();
                }
            }
        }, new NetworkResponseErrorListener() {
            @Override
            public void onErrorResult(Exception error) {
                Toast.makeText(RFID_Activity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_LONG).show();
                Common.hideProgressDialog();
            }
        });
    }

}
