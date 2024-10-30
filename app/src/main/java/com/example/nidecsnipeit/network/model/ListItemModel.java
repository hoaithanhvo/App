package com.example.nidecsnipeit.network.model;

import android.graphics.drawable.Drawable;

import com.example.nidecsnipeit.R;

import java.util.List;

public class ListItemModel {
    private final String title;
    private int titleColor = R.color.secondary;
    private String value;
    private List<BasicItemModel> dropdownItems;
    private final Mode mode;
    private final Drawable icon;
    private boolean dropdownScanner = false;

    public ListItemModel(String title, String value) {
        this.title = title;
        this.value = value;
        this.mode = Mode.TEXT;
        this.icon = null;
    }

    public ListItemModel(String title, int titleColor, String value) {
        this.title = title;
        this.value = value;
        this.mode = Mode.TEXT;
        this.icon = null;
        this.titleColor = titleColor;
    }
    public ListItemModel(String title, String value, Mode mode) {
        this.title = title;
        this.value = value;
        this.mode = mode;
        this.icon = null;
    }

    public ListItemModel(String title, String value, Mode mode, Drawable icon) {
        this.title = title;
        this.value = value;
        this.mode = mode;
        this.icon = icon;
    }
    public ListItemModel(String title, String value, Mode mode, List<BasicItemModel> dropdownItems) {
        this.title = title;
        this.value = value;
        this.mode = mode;
        this.dropdownItems = dropdownItems;
        this.icon = null;
    }

    public ListItemModel(String title, String value, Mode mode, List<BasicItemModel> dropdownItems, boolean dropdownScanner) {
        this.title = title;
        this.value = value;
        this.mode = mode;
        this.dropdownItems = dropdownItems;
        this.icon = null;
        this.dropdownScanner = dropdownScanner;
    }

    public String getTitle() {
        return this.title;
    }
    public int getTitleColor() {
        return this.titleColor;
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

    public List<BasicItemModel> getDropdownItems() {
        return this.dropdownItems;
    }
    public boolean isDropdownScanner() {
        return this.dropdownScanner;
    }

    public Mode getMode() {
        return mode;
    }

    public enum Mode {
        TEXT,
        EDIT_TEXT,
        DROPDOWN,
        AUTOCOMPLETE_TEXT

    }
    public String getValueByTitle(List<ListItemModel> dataList, String title) {
        for (ListItemModel item : dataList) {
            if (title.equals(item.getTitle())) {
                return item.getValue();
            }
        }
        return null; // Trả về null nếu không tìm thấy
    }

}
