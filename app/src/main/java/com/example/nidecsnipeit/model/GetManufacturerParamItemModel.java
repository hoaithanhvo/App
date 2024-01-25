package com.example.nidecsnipeit.model;

public class GetManufacturerParamItemModel {
    public String name;
    public String url;
    public String support_url;
    public String support_phone;
    public String support_email;

    public GetManufacturerParamItemModel(String name, String url, String support_url, String support_phone, String support_email) {
        this.name = name;
        this.url = url;
        this.support_url = support_url;
        this.support_phone = support_phone;
        this.support_email = support_email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSupportUrl() {
        return support_url;
    }

    public void setSupportUrl(String support_url) {
        this.support_url = support_url;
    }

    public String getSupportPhone() {
        return support_phone;
    }

    public void setSupportPhone(String support_phone) {
        this.support_phone = support_phone;
    }

    public String getSupportEmail() {
        return support_email;
    }

    public void setSupportEmail(String support_email) {
        this.support_email = support_email;
    }
}
