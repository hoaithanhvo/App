package com.example.nidecsnipeit.network.model;

import org.json.JSONArray;

public class ProductDetailsModel {
    private String Category;
    private String Manufactory;
    private String Catalog;
    private String Varrial;
    private String Total;

    public JSONArray getItem_request_details() {
        return item_request_details;
    }

    public void setItem_request_details(JSONArray item_request_details) {
        this.item_request_details = item_request_details;
    }

    private JSONArray item_request_details;

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getVarrial() {
        return Varrial;
    }

    public void setVarrial(String varrial) {
        Varrial = varrial;
    }

    private String Created;
    private String Status;

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getManufactory() {
        return Manufactory;
    }

    public void setManufactory(String manufactory) {
        Manufactory = manufactory;
    }

    public String getCatalog() {
        return Catalog;
    }

    public void setCatalog(String catalog) {
        Catalog = catalog;
    }

    public String getCreated() {
        return Created;
    }

    public void setCreated(String created) {
        Created = created;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public ProductDetailsModel() {
    }
}
