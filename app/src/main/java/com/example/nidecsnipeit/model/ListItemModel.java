package com.example.nidecsnipeit.model;

import java.util.List;

public class ListItemModel {
    private final String title;
    private String value;
    private String[] dropdownItems;
    private final Mode mode;

    public ListItemModel(String title, String value, Mode mode) {
        this.title = title;
        this.value = value;
        this.mode = mode;
        this.dropdownItems = new String[0];
    }

    public ListItemModel(String title, String value, Mode mode, String[] dropdownItems) {
        this.title = title;
        this.value = value;
        this.mode = mode;
        this.dropdownItems = dropdownItems;
    }

    public String getTitle() {
        return title;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String[] getDropdownItems() {
        return dropdownItems;
    }

    public void setDropdownItems(String[] dropdownItems) {
        this.dropdownItems = dropdownItems;
    }

    public Mode getMode() {
        return mode;
    }

    public enum Mode {
        TEXT,
        EDIT_TEXT,
        DROPDOWN

    }
}
