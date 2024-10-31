package com.example.nidecsnipeit.network.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

public class ProductDeliveryModel implements Serializable {
    private String productID;
    private String userID;
    private String createAt;
    private String note;
    private JSONArray items_request;
    public ProductDeliveryModel(){

    }
    public ProductDeliveryModel(String productID, String userID, String createAt, String note) {
        this.productID = productID;
        this.userID = userID;
        this.createAt = createAt;
        this.note = note;
    }

    public JSONArray getItems_request() {
        return items_request;
    }

    public void setItems_request(JSONArray items_request) {
        this.items_request = items_request;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getProductID() {
        return productID;
    }

    public String getUserID() {
        return userID;
    }

    public String getCreateAt() {
        return createAt;
    }

    public String getNote() {
        return note;
    }
}
