package com.example.nidecsnipeit.network.model;

public class ProductItemDetailsModel {

    private String AssetID;
    private String AssetTag;
    private String Serial;

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
