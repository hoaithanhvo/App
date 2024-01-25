package com.example.nidecsnipeit.model;

public class CheckinItemModel {
    public int status;
    public String name;
    public String note;
    public String location;

    public CheckinItemModel(int status, String name, String note, String location) {
        this.status = status;
        this.name = name;
        this.note = note;
        this.location = location;
    }
}
