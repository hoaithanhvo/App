package com.example.nidecsnipeit.model;

public class GetLocationParamItemModel {
    public String sort;
    public String order; // asc or desc

    public GetLocationParamItemModel() {
        this.sort = "name";
        this.order = "asc";
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
