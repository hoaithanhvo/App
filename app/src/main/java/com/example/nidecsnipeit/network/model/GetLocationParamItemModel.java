package com.example.nidecsnipeit.network.model;

public class GetLocationParamItemModel {
    public String sort;
    public String order;
    public String companyId; // asc or desc

    public GetLocationParamItemModel(String companyId) {
        this.sort = "name";
        this.order = "asc";
        this.companyId = companyId;
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
}
