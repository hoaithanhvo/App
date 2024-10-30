package com.example.nidecsnipeit.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.nidecsnipeit.network.model.AuditOfflineModel;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tbtauditv2.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "tbtaudit";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ASSET_TAG = "asset_tag";
    public static final String COLUMN_COST_CENTER = "_snipeit_cost_center_29";
    public static final String COLUMN_ROOM = "_snipeit_room_30";
    public static final String COLUMN_INVENTORY_NUMBER = "_snipeit_inventory_number_32";
    public static final String COLUMN_CURRENT_APC = "_snipeit_current_apc_34";
    public static final String COLUMN_CURRBKVAL = "_snipeit_currbkval_36";
    public static final String COLUMN_LABEL_STATUS = "label_status";
    public static final String COLUMN_ASSET_STATUS = "asset_status";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_LAST_USED = "last_used";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableAuditOffline = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_ASSET_TAG + " TEXT, " +
                COLUMN_COST_CENTER + " TEXT, " +
                COLUMN_ROOM + " TEXT, " +
                COLUMN_INVENTORY_NUMBER + " TEXT, " +
                COLUMN_CURRENT_APC + " TEXT, " +
                COLUMN_CURRBKVAL + " TEXT, " +
                COLUMN_LABEL_STATUS + " TEXT, " +
                COLUMN_ASSET_STATUS + " TEXT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_LAST_USED + " TEXT)";
        db.execSQL(createTableAuditOffline);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Phương thức để thêm dữ liệu vào cơ sở dữ liệu
    public void addData(ArrayList<AuditOfflineModel> listLog, String labelStatus, String assetStatus, String location,String last_used) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues logItem = new ContentValues();
//        logItem.put(COLUMN_ID, listLog.get(0).getValue());
//        logItem.put(COLUMN_NAME, listLog.get(1).getValue());
        logItem.put(COLUMN_ASSET_TAG, listLog.get(4).getValue());
        logItem.put(COLUMN_INVENTORY_NUMBER, listLog.get(0).getValue());
        logItem.put(COLUMN_COST_CENTER, listLog.get(1).getValue());
        logItem.put(COLUMN_ROOM, listLog.get(2).getValue());
//        logItem.put(COLUMN_CURRENT_APC, listLog.get(6).getValue());
//        logItem.put(COLUMN_CURRBKVAL, listLog.get(7).getValue());
        logItem.put(COLUMN_LABEL_STATUS, labelStatus);
        logItem.put(COLUMN_ASSET_STATUS, assetStatus);
        logItem.put(COLUMN_LOCATION, location);
        logItem.put(COLUMN_LAST_USED, last_used);
        db.insert(TABLE_NAME, null, logItem);
        db.close();
    }
    // Phương thức để lấy dữ liệu từ cơ sở dữ liệu
    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}
