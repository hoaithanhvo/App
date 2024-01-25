package com.example.nidecsnipeit.model;

public class CheckoutItemModel {
    public int status;
    public String checkoutToType;
    public String assigned_user;
    public String assigned_asset;
    public int assigned_location;
    public String expected_checkin;
    public String checkout_at;
    public String name;
    public int note;

    public CheckoutItemModel() {
    }

    public int getStatus() {
        return status;
    }

    public String getCheckoutToType() {
        return checkoutToType;
    }

    public String getAssigned_user() {
        return assigned_user;
    }

    public String getAssigned_asset() {
        return assigned_asset;
    }

    public int getAssigned_location() {
        return assigned_location;
    }

    public String getExpected_checkin() {
        return expected_checkin;
    }

    public String getCheckout_at() {
        return checkout_at;
    }

    public String getName() {
        return name;
    }

    public int getNote() {
        return note;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setAssigned_asset(String assigned_asset) {
        this.assigned_asset = assigned_asset;
    }

    public void setCheckoutToType(String checkoutToType) {
        this.checkoutToType = checkoutToType;
    }

    public void setAssigned_location(int assigned_location) {
        this.assigned_location = assigned_location;
    }

    public void setAssigned_user(String assigned_user) {
        this.assigned_user = assigned_user;
    }

    public void setCheckout_at(String checkout_at) {
        this.checkout_at = checkout_at;
    }

    public void setExpected_checkin(String expected_checkin) {
        this.expected_checkin = expected_checkin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNote(int note) {
        this.note = note;
    }
}
