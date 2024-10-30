package com.example.nidecsnipeit.network.model;

import androidx.annotation.NonNull;

public class CategoryFieldModel {
    private String name;

    public CategoryFieldModel(String name, int id) {
        this.name = name;
        this.id = id;
    }

    private String db_column;
    private int is_displayed;
    private int id;

    public CategoryFieldModel(String name, String columnName, int isDisplayed) {
        this.name = name;
        this.db_column = columnName;
        this.is_displayed = isDisplayed;
    }

    public String getColumnName() {
        return db_column;
    }

    public String getName() {
        return name;
    }

    public int isDisplayed() {
        return is_displayed;
    }
    public void setDisplayed(boolean checked) {
        if (checked) {
            is_displayed = 1;
        } else {
            is_displayed = 0;
        }
    }

    public int getId() {
        return id;
    }
    @Override
    public String toString() {
        return name;  // Dùng để hiển thị tên trong AutoCompleteTextView
    }
}
