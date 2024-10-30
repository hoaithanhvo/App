package com.example.nidecsnipeit.network.model;

public class GetSupplierParamItemModel {
    public String name;
    public String address;
    public String address2;
    public String city;
    public String zip;
    public String country;
    public String fax;
    public String email;
    public String url;
    public String notes;

    public GetSupplierParamItemModel(String name, String address, String address2, String city, String zip, String country, String fax, String email, String url, String notes) {
        this.name = name;
        this.address = address;
        this.address2 = address2;
        this.city = city;
        this.zip = zip;
        this.country = country;
        this.fax = fax;
        this.email = email;
        this.url = url;
        this.notes = notes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
