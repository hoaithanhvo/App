package com.example.nidecsnipeit.network.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tbtaudit.db";
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng khi cơ sở dữ liệu được tạo lần đầu
        String createTable = "CREATE TABLE my_table (id INTEGER PRIMARY KEY, name TEXT, value TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nâng cấp cơ sở dữ liệu nếu cần thiết
        db.execSQL("DROP TABLE IF EXISTS tbtaudit");
        onCreate(db);
    }

    // Phương thức để thêm dữ liệu
    public void insertData(String name, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("value", value);
        db.insert("tbtaudit", null, contentValues);
    }
    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM tbtaudit", null);
    }
}

