package com.example.nidecsnipeit.model;

import java.util.List;

public class checkoutItemRequestModel {
    private int items_request_id;
    private List<Asset> assets;

    public checkoutItemRequestModel() {

    }

    public int getItem_request_id() {
        return items_request_id;
    }

    public void setItem_request_id(int item_request_id) {
        this.items_request_id = item_request_id;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }
}
