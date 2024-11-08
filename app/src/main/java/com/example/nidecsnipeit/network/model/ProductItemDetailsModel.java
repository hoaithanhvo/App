package com.example.nidecsnipeit.network.model;

import java.util.HashMap;

public class ProductItemDetailsModel {

    private String AssetID;
    private String AssetTag;
    private String Serial;

    public HashMap<String, String> getStatusMap() {
        return statusMap;
    }

    public void setStatusMap(String nameStatus, String colorStatus) {
        this.statusMap = statusMap;
    }

    private HashMap<String,String> statusMap = new HashMap<>();


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSerial() {
        return Serial;
    }

    public void setSerial(String serial) {
        Serial = serial;
    }

    public String getAssetTag() {
        return AssetTag;
    }

    public void setAssetTag(String assetTag) {
        AssetTag = assetTag;
    }

    public String getAssetID() {
        return AssetID;
    }

    public void setAssetID(String assetID) {
        AssetID = assetID;
    }

    private String Name;

    public ProductItemDetailsModel() {
    }
}
