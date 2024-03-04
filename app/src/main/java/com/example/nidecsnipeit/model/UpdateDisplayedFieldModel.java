package com.example.nidecsnipeit.model;

public class UpdateDisplayedFieldModel {
    public String category_id;
    public String list_field;

    public UpdateDisplayedFieldModel(String categoryId, String listField) {
        this.category_id = categoryId;
        this.list_field = listField;
    }
}
