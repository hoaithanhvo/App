package com.example.nidecsnipeit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.nidecsnipeit.adapter.CustomRecyclerAdapter;
import com.example.nidecsnipeit.adapter.MaintenanceAdapter;
import com.example.nidecsnipeit.model.DetailFieldModel;
import com.example.nidecsnipeit.model.ListItemModel;
import com.example.nidecsnipeit.model.MaintenanceItemModel;
import com.example.nidecsnipeit.model.SpinnerItemModel;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utils.Common;
import com.example.nidecsnipeit.utils.FullNameConvert;
import com.example.nidecsnipeit.utils.QRScannerHelper;
import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MaintenanceAddActivity extends BaseActivity {
    private CustomRecyclerAdapter adapter;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_add);

        MyApplication MyApp = (MyApplication) getApplication();
        NetworkManager apiServices = NetworkManager.getInstance();

        List<SpinnerItemModel> maintenanceTypes = new ArrayList<>(); // {"Maintenance", "Repair", "PAT Test", "Upgrade", "Hardware Support", "Software Support"};
        maintenanceTypes.add(new SpinnerItemModel("0", "Maintenance"));
        maintenanceTypes.add(new SpinnerItemModel("1", "Repair"));
        maintenanceTypes.add(new SpinnerItemModel("2", "PAT Test"));
        maintenanceTypes.add(new SpinnerItemModel("3", "Upgrade"));
        maintenanceTypes.add(new SpinnerItemModel("4", "Hardware Support"));
        maintenanceTypes.add(new SpinnerItemModel("5", "Software Support"));

        List<ListItemModel> dataList = new ArrayList<>();

        // Get detail data
        Intent intent = getIntent();
        MaintenanceItemModel maintenanceInfo = (MaintenanceItemModel) intent.getSerializableExtra("MAINTENANCE_INFO");

        Button maintenanceButton = findViewById(R.id.maintenance_add_btn);
        List<DetailFieldModel> fields = MyApp.getMaintenanceScreenFields();

        // get data all displayed fields
        if (maintenanceInfo != null) {
            // Set up action bar
            setupActionBar("Update maintenance");
            maintenanceButton.setText("UPDATE MAINTENANCE");
            dataList.add(new ListItemModel("Maintenance type", maintenanceInfo.asset_maintenance_type, ListItemModel.Mode.DROPDOWN, maintenanceTypes));
            for (DetailFieldModel field : fields) {
                String valueField = "";
                String titleField = FullNameConvert.getFullName(field.getName());
                ListItemModel.Mode typeField = field.getType();
                String[] dropdownItems = new String[0];

                if (typeField == ListItemModel.Mode.DROPDOWN) {
                    apiServices.getSupplierList(new NetworkResponseListener<JSONObject>() {
                        @Override
                        public void onResult(JSONObject object) throws JSONException {
                            maintenanceButton.setText("UPDATE MAINTENANCE");
                        }
                    }, new NetworkResponseErrorListener() {
                        @Override
                        public void onErrorResult(Exception error) {

                        }
                    });
//                    dataList.add(new ListItemModel(titleField, valueField, ListItemModel.Mode.DROPDOWN, dropdownItems));
                } else {
                    dataList.add(new ListItemModel(titleField, valueField, typeField));
                }
            }
        } else {
            // Set up action bar
            setupActionBar("Add maintenance");
            maintenanceButton.setText("SAVE MAINTENANCE");
        }

        // map item list to view
        RecyclerView recyclerView = findViewById(R.id.recycler_maintenance_add);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomRecyclerAdapter(this, dataList, recyclerView);
        recyclerView.setAdapter(adapter);
        Common.hideProgressDialog();

        maintenanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> valuesMap = adapter.getAllValuesByTitle();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Get QR scanned value
        String scannedValue = QRScannerHelper.processScanResult(requestCode, resultCode, data);

        if (scannedValue != null) {
            // Update dropdown selection following position
            adapter.updateDropdownSelection(adapter.getCurrentPosition(), scannedValue);
        }
    }
}