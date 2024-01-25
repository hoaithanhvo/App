package com.example.nidecsnipeit.model;

public class MaintenanceItemModel {
    private String title;
    private String dateTitle;

    public MaintenanceItemModel(String title, String contentTitle) {
        this.title = title;
        this.dateTitle = contentTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTitle() {
        return dateTitle;
    }

    public void setDateTitle(String contentTitle) {
        this.dateTitle = contentTitle;
    }
}
