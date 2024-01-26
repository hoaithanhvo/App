package com.example.nidecsnipeit.model;

public class GetMaintenanceParamItemModel {
    public int limit;
    public int offset;
    public String search;
    public String sort;
    public String order;
    public String asset_id;

    public GetMaintenanceParamItemModel(int limit, int offset, String search, String sort, String order, String assetID) {
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

    public String getAssetID() {
        return asset_id;
    }

    public void setAssetID(String asset_id) {
        this.asset_id = asset_id;
    }
}
