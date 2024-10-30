package com.example.nidecsnipeit.network.model;

import androidx.annotation.NonNull;

import java.util.Collection;

public class BasicItemModel {
    private String id;
    private String name;

    public BasicItemModel(String id, String name) {
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
