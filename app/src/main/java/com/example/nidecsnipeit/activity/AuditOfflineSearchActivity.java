package com.example.nidecsnipeit.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nidecsnipeit.R;
import com.example.nidecsnipeit.network.model.AuditModel;
import com.example.nidecsnipeit.network.NetworkManager;
import com.example.nidecsnipeit.network.NetworkResponseErrorListener;
import com.example.nidecsnipeit.network.NetworkResponseListener;
import com.example.nidecsnipeit.utility.Common;
import com.example.nidecsnipeit.utility.DatabaseManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuditOfflineSearchActivity extends AppCompatActivity {

    private NetworkManager apiServices;
    ImageButton imgClear,imgback;
    EditText input_search;
    ListView lv;
    String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database=null;
    String DATABASE_NAME="tbtassetv1.db";
    ArrayList<String> datalist;
    ArrayAdapter<String> myapdapter;
    Button bntlog,bntsave,bntcheckinternet;
    public SQLiteDatabase insertAudit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_audit_offline_search);
        getSupportActionBar().hide();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No network connection. You cannot go back.", Toast.LENGTH_SHORT).show();
        }
        input_search = findViewById(R.id.input_search);
        lv = findViewById(R.id.lv);
        bntlog=findViewById(R.id.bntlog);
        bntsave=findViewById(R.id.bntsave);
        imgClear = findViewById(R.id.imgClear);
        imgback = findViewById(R.id.imgback);
        apiServices = NetworkManager.getInstance(this);
        //Event Clear
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_search.setText("");
            }
        });
        datalist = new ArrayList<>();
        myapdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,datalist);
        lv.setAdapter(myapdapter);
        processCopy();
        database = openOrCreateDatabase("tbtassetv1.db", MODE_PRIVATE,null);
        input_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchData = s.toString();
                if (searchData.contains("*")) {
                    String regex = "\\*(.*?)\\*";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(searchData);
                    if (matcher.find()) {
                        searchData = matcher.group(1);
                    }
                }
                if (searchData.equals("")) {
                    datalist.clear();
                    myapdapter.notifyDataSetChanged();
                    return;
                }
                datalist.clear();
                String selection = "_snipeit_inventory_number_32 LIKE ?";
                String[] selectionArgs = new String[]{"%" + searchData + "%"};
                Cursor c = database.query("tbtasset", null, selection, selectionArgs, null, null, null);
                if (c.moveToFirst()) {
                    do {
                        String data =  c.getString(5) + "_" + c.getString(3) + "_" + c.getString(4)+ "_" + c.getString(1)+ "_" + c.getString(2);
                        datalist.add(data);
                    } while (c.moveToNext());
                }

                c.close();
                myapdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //Event Listview
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = datalist.get(position);
                Intent myIntent = new Intent(AuditOfflineSearchActivity.this,AuditOfflineActivity.class);
                myIntent.putExtra("selectedItem", selectedItem);
                startActivity(myIntent);
            }
        });
        //Event Log
        bntlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuditOfflineSearchActivity.this,LogAuditActivity.class);
                startActivity(intent);
            }
        });
        bntsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable()) {
                    Toast.makeText(AuditOfflineSearchActivity.this, "No network connection. You cannot save.", Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseManager dbManager = DatabaseManager.getInstance(AuditOfflineSearchActivity.this);
                Cursor c = dbManager.getData();
                List<AuditModel> auditModels = new ArrayList<>();
                auditModels.clear();

                // Lấy dữ liệu từ SQLite
                if (c != null && c.moveToFirst()) {
                    int _snipeit_inventory_number_32 = c.getColumnIndex("_snipeit_inventory_number_32");
                    int asset_tag = c.getColumnIndex("asset_tag");
                    int label_status = c.getColumnIndex("label_status");
                    int asset_status = c.getColumnIndex("asset_status");
                    int location = c.getColumnIndex("location");
                    int last_used = c.getColumnIndex("last_used");
                    do {
                        AuditModel item = new AuditModel(
                                c.getString(_snipeit_inventory_number_32),  // Snipeit_inventory_number_32
                                c.getString(asset_tag),  // asset_tag
                                c.getString(label_status),  // label_status
                                c.getString(asset_status),  // asset_status
                                c.getString(location),
                                c.getString(last_used)
                        );
                        auditModels.add(item);
                    } while (c.moveToNext());

                    c.close();
                }
                if (!auditModels.isEmpty()) {
                    try {
                        apiServices.postAuditObject(auditModels, new NetworkResponseListener<JSONObject>() {
                            @Override
                            public void onResult(JSONObject object) {
                                try {
                                    if (object.has("status") && object.get("status").equals("error")) {
                                        Common.hideProgressDialog();
                                        Toast.makeText(AuditOfflineSearchActivity.this, object.getString("messages"), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Common.hideProgressDialog();
                                        //database.delete("tbtaudit", null, null);
                                        dbManager.deleteData();
                                        String messageSuccessful = "Insert all asset audit out successfully.";
                                        Toast.makeText(AuditOfflineSearchActivity.this, messageSuccessful, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(AuditOfflineSearchActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new NetworkResponseErrorListener() {
                            @Override
                            public void onErrorResult(Exception error) {
                                Toast.makeText(AuditOfflineSearchActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(AuditOfflineSearchActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AuditOfflineSearchActivity.this, "Bộ nhớ tạm trống không có dữ liệu để cập nhật", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable()) {
                    Toast.makeText(AuditOfflineSearchActivity.this, "No network connection. You cannot go back.", Toast.LENGTH_SHORT).show();
                    return;
                }
                finish();
            }
        });
    }
    private void processCopy() {
        File dbFile = getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists())
        {
            try{CopyDataBaseFromAsset();
                Toast.makeText(this, "Copying sucess from Assets folder",
                        Toast.LENGTH_LONG).show();
            }
            catch (Exception e){
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
    private String getDatabasePath() {
        return getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
    }
    public void CopyDataBaseFromAsset() {
// TODO Auto-generated method stub
        try {
            InputStream myInput;
            myInput = getAssets().open(DATABASE_NAME);
// Path to the just created empty db
            String outFileName = getDatabasePath();
// if the path doesn't exist first, create it
            File f = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists())
                f.mkdir();
// Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);
// transfer bytes from the inputfile to the outputfile
// Truyền bytes dữ liệu từ input đến output
            int size = myInput.available();
            byte[] buffer = new byte[size];
            myInput.read(buffer);
            myOutput.write(buffer);
// Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }}

    @Override
    public void onBackPressed() {
        if (isNetworkAvailable()) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Cannot go back without a network connection.", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}