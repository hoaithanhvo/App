package com.example.nidecsnipeit.model;

public class CheckinItemModel {
    private int status;
    private String name;
    private String note;
    private String location;

    public CheckinItemModel(int status, String name, String note, String localtion) {
        this.status = status;
        this.name = name;
        this.note = note;
        this.location = localtion;
    }
}
