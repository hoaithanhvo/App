package com.example.nidecsnipeit.utility;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nidecsnipeit.network.model.AuditOfflineModel;

import java.util.ArrayList;

public class DatabaseManager {
    private static DatabaseManager instance;
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    private DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public static synchronized DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    public SQLiteDatabase openDatabase() {
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
        return database;
    }

    public void closeDatabase() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    // Phương thức để thêm dữ liệu vào cơ sở dữ liệu
    public void addData(ArrayList<AuditOfflineModel> listLog, String labelStatus, String assetStatus, String location,String last_used) {
        dbHelper.addData(listLog, labelStatus, assetStatus, location,last_used);
    }

    // Phương thức để lấy dữ liệu từ cơ sở dữ liệu
    public Cursor getData() {
        return dbHelper.getData();
    }
    public void deleteData(){
        dbHelper.deleteAllData();
    }
}
