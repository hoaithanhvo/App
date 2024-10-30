package com.example.nidecsnipeit.network.model;

public class DetailFieldModel {
    private final String name;
    private final ListItemModel.Mode type;
    private boolean requiredId = false;

    public DetailFieldModel(String fieldName) {
        this.name = fieldName;
        this.type = ListItemModel.Mode.TEXT;
    }
    public DetailFieldModel(String fieldName, ListItemModel.Mode fieldType) {
        this.name = fieldName;
        this.type = fieldType;
    }

    public DetailFieldModel(String fieldName, ListItemModel.Mode fieldType, boolean requiredId) {
        this.name = fieldName;
        this.type = fieldType;
        this.requiredId = requiredId;
    }

    public String getName() {
        return this.name;
    }

    public ListItemModel.Mode getType() {
        return this.type;
    }

    public boolean getRequiredId() {
        return this.requiredId;
    }
}
