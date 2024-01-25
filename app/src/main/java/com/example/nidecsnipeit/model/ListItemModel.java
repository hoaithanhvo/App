package com.example.nidecsnipeit.model;

import android.graphics.drawable.Drawable;
import android.widget.ImageButton;

public class ListItemModel {
    private final String title;
    private String value;
    private String[] dropdownItems;
    private final Mode mode;
    private final Drawable icon;

    public ListItemModel(String title, String value) {
        this.title = title;
        this.value = value;
        this.mode = Mode.TEXT;
        this.dropdownItems = new String[0];
        this.icon = null;
    }
    public ListItemModel(String title, String value, Mode mode) {
        this.title = title;
        this.value = value;
        this.mode = mode;
        this.dropdownItems = new String[0];
        this.icon = null;
    }

    public ListItemModel(String title, String value, Mode mode, Drawable icon) {
        this.title = title;
        this.value = value;
        this.mode = mode;
        this.dropdownItems = new String[0];
        this.icon = icon;
    }
    public ListItemModel(String title, Mode mode, String[] dropdownItems) {
        this.title = title;
        this.value = null;
        this.mode = mode;
        this.dropdownItems = dropdownItems;
        this.icon = null;
    }

    public String getTitle() {
        return this.title;
    }
    public Drawable getIcon() {
        return this.icon;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String[] getDropdownItems() {
        return this.dropdownItems;
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
