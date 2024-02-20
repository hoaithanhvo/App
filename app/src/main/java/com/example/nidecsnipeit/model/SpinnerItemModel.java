package com.example.nidecsnipeit.model;

import androidx.annotation.NonNull;

public class SpinnerItemModel {
    private String id;
    private String name;

    public SpinnerItemModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
