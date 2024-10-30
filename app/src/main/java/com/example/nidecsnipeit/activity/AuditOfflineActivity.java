package com.example.nidecsnipeit.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.adapter.AuditOfflineAdapter;
import com.example.nidecsnipeit.network.model.AuditOfflineModel;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.utility.CustomDatePicker;
import com.example.nidecsnipeit.utility.DatabaseManager;

import java.util.ArrayList;

public class AuditOfflineActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText editlocation;
    Button bntaudit,bntdatePickerButton;
    ImageButton imgback;
    String name[] = {"Inventory Number","Cost Center","Room","Model","Asset Tag"};
    private Spinner spinnerGroupAsset;
    String value[];
    AuditOfflineAdapter myapdapter;
    ListView lv;
    ArrayList<AuditOfflineModel> datalist;
    public SQLiteDatabase insertAudit;
    private RadioGroup radioGroupLabel;
    private  String assetLocation = "";
    LinearLayout lnDatePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_audit_offline);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        lv= findViewById(R.id.lv);
        imgback = findViewById(R.id.imgback);
        bntaudit = findViewById(R.id.bntaudit);
        editlocation = findViewById(R.id.editlocation);
        spinnerGroupAsset = findViewById(R.id.spinnerAssetStatus);
        radioGroupLabel = findViewById(R.id.radioGroupLabel);
        bntdatePickerButton=findViewById(R.id.bntdatePickerButton);
        lnDatePicker=findViewById(R.id.lnDatePicker);
        // Map data_source into spinnerAssetStatus
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(
                this, R.array.asset_status_array,
                R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGroupAsset.setAdapter(arrayAdapter);
        // Register event to spinner
        spinnerGroupAsset.setOnItemSelectedListener(this);
        spinnerGroupAsset.setSelection(0);
        // Event back
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        String selectedItem = intent.getStringExtra("selectedItem");
        // Cut String
        value = selectedItem.split("_");
        datalist = new ArrayList<>();
        for (int i = 0; i<name.length;i++){
            datalist.add(new AuditOfflineModel(name[i],value[i]));
        }
        myapdapter = new AuditOfflineAdapter(AuditOfflineActivity.this,R.layout.layout_audit_offline,datalist);
        lv.setAdapter(myapdapter);
        // Create csdl
        insertAudit = openOrCreateDatabase("qlaudit.db",MODE_PRIVATE,null);
        //Create table chứa data;
        try{
            String sql = "CREATE TABLE tbtaudit(id INTEGER primary key, name TEXT, asset_tag TEXT,_snipeit_cost_center_29 TEXT, _snipeit_room_30 TEXT,_snipeit_inventory_number_32 TEXT,_snipeit_current_apc_34 TEXT,_snipeit_currbkval_36 TEXT , label_status TEXT , asset_status TEXT , location TEXT)";
            insertAudit.execSQL(sql);
        }
        catch (Exception e){
            Log.e("Error","Table đã tồn tại");
        }
        if(radioGroupLabel.getCheckedRadioButtonId()==R.id.rdoLabelGood){
            lnDatePicker.setVisibility(View.GONE);
        }else{
            lnDatePicker.setVisibility(View.VISIBLE);
        }
        radioGroupLabel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.rdoLabelGood){
                    lnDatePicker.setVisibility(View.GONE);
                }
                else{
                    lnDatePicker.setVisibility(View.VISIBLE);
                }
            }
        });
        bntaudit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedIdLabel = radioGroupLabel.getCheckedRadioButtonId();
                RadioButton radselectLabel = findViewById(selectedIdLabel);
                String LabelStatus = radselectLabel.getText().toString();
                String AssetStatus = spinnerGroupAsset.getSelectedItem().toString();
                assetLocation = editlocation.getText().toString();
                String labelStatus;
                String lastUsed = (String) bntdatePickerButton.getText();
                if (LabelStatus.equals("Good")) {
                    labelStatus = "true";
                    lastUsed="";
                } else {
                    labelStatus = "false";
                }
                String assetStatus;
                if (AssetStatus.equals("Available to use")) {
                    assetStatus = "A";
                } else if (AssetStatus.equals("Available to use after repairing")) {
                    assetStatus = "B";
                } else if (AssetStatus.equals("Unable to use or out-of-date")) {
                    assetStatus = "C";
                } else {
                    Toast.makeText(AuditOfflineActivity.this, "Please choose asset status", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseManager dbManager = DatabaseManager.getInstance(AuditOfflineActivity.this);
                dbManager.addData(datalist, labelStatus, assetStatus, assetLocation,lastUsed);
                Toast.makeText(AuditOfflineActivity.this, "Insert record Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        bntdatePickerButton.setText(CustomDatePicker.getTodaysDate());
        bntdatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDatePicker.showDatePickerDialog(AuditOfflineActivity.this, new CustomDatePicker.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(int day, int month, int year) {
                        String selectedDate = String.format("%02d-%02d-%d", day, month, year);
                        bntdatePickerButton.setText(selectedDate);
                    }
                });
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}