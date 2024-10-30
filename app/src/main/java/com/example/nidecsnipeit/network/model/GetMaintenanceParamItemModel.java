package com.example.nidecsnipeit.network.model;

public class GetMaintenanceParamItemModel {
    public int limit = 100;
    public int offset = 0;
    public String search = "";
    public String sort = "created_at";
    public String order = "desc";
    public int asset_id;

    public GetMaintenanceParamItemModel(int assetID) {
        this.asset_id = assetID;
    }

    public GetMaintenanceParamItemModel(int limit, int offset, String search, String sort, String order, int assetID) {
        this.limit = limit;
        this.offset = offset;
        this.search = search;
        this.sort = sort;
        this.order = order;
        this.asset_id = assetID;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getAssetID() {
        return asset_id;
    }

    public void setAssetID(int asset_id) {
        this.asset_id = asset_id;
    }
}
