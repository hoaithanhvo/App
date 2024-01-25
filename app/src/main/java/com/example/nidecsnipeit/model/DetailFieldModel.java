package com.example.nidecsnipeit.model;

public class DetailFieldModel {
    private final String name;
    private final ListItemModel.Mode type;

    public DetailFieldModel(String fieldName) {
        this.name = fieldName;
        this.type = ListItemModel.Mode.TEXT;
    }
    public DetailFieldModel(String fieldName, ListItemModel.Mode fieldType) {
        this.name = fieldName;
        this.type = fieldType;
    }

    public String getName() {
        return this.name;
    }

    public ListItemModel.Mode getType() {
        return this.type;
    }
}
