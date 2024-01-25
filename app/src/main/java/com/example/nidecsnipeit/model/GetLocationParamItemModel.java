package com.example.nidecsnipeit.model;

public class GetLocationParamItemModel {
    public String name;
    public int limit;
    public int offset;
    public String search;
    public String sort;
    public String order; // asc or desc
    public String address;
    public String address2;
    public String city;
    public String zip;
    public String country;

    public GetLocationParamItemModel(String name, int limit, int offset, String search, String sort, String order, String address, String address2, String city, String zip, String country) {
        this.name = name;
        this.limit = limit;
        this.offset = offset;
        this.search = search;
        this.sort = sort;
        this.order = order;
        this.address = address;
        this.address2 = address2;
        this.city = city;
        this.zip = zip;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
