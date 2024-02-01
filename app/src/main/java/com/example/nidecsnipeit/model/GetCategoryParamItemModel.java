package com.example.nidecsnipeit.model;

public class GetCategoryParamItemModel {
    public String name;
    public int limit;
    public int offset;
    public String search;
    public String sort;
    public String order; // asc or desc
    public String category_id;
    public String category_type;
    public String use_default_eula;
    public String require_acceptance;
    public String checkin_email;

    public GetCategoryParamItemModel(String name, int limit, int offset, String search, String sort, String order, String category_id, String category_type, String use_default_eula, String require_acceptance, String checkin_email) {
        this.name = name;
        this.limit = limit;
        this.offset = offset;
        this.search = search;
        this.sort = sort;
        this.order = order;
        this.category_id = category_id;
        this.category_type = category_type;
        this.use_default_eula = use_default_eula;
        this.require_acceptance = require_acceptance;
        this.checkin_email = checkin_email;
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

    public String getCategoryID() {
        return category_id;
    }

    public void setCategoryID(String category_id) {
        this.category_id = category_id;
    }

    public String getCategoryType() {
        return category_type;
    }

    public void setCategoryType(String category_type) {
        this.category_type = category_type;
    }

    public String getUseDefaultEula() {
        return use_default_eula;
    }

    public void setUseDefaultEula(String use_default_eula) {
        this.use_default_eula = use_default_eula;
    }

    public String getRequireAcceptance() {
        return require_acceptance;
    }

    public void setRequireAcceptance(String require_acceptance) {
        this.require_acceptance = require_acceptance;
    }

    public String getCheckinEmail() {
        return checkin_email;
    }

    public void setCheckinEmail(String checkin_email) {
        this.checkin_email = checkin_email;
    }
}
