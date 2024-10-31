package com.example.nidecsnipeit.network.model;

public class ProductDetailsModel {
    private int AssetID;
    private String AssetTag;
    private String Serial;
    private String Name;
    private String CreatedAt;

    public int getAssetID() {
        return AssetID;
    }

    public void setAssetID(int assetID) {
        AssetID = assetID;
    }

    public String getAssetTag() {
        return AssetTag;
    }

    public void setAssetTag(String assetTag) {
        AssetTag = assetTag;
    }

    public String getSerial() {
        return Serial;
    }

    public void setSerial(String serial) {
        Serial = serial;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        CreatedAt = createdAt;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    private String Status;


    public ProductDetailsModel() {
    }
}
