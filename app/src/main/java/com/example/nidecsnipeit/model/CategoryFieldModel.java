package com.example.nidecsnipeit.model;

import androidx.annotation.NonNull;

public class CategoryFieldModel {
    private String name;
    private String db_column;
    private int is_displayed;

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

}
