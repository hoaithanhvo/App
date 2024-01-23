package com.example.nidecsnipeit.model;

public class ListItemModel {
    private final String title;
    private final String value;
    private final String[] dropdownItems;
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

    public String[] getDropdownItems() {
        return dropdownItems;
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
