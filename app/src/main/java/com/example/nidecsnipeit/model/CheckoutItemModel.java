package com.example.nidecsnipeit.model;

public class CheckoutItemModel {
    public int status;
    public String checkoutToType;
    public String assignedUser;
    public String assignedAsset;
    public int assignedLocation;
    public String expectedCheckin;
    public String checkoutAt;
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

    public String getAssignedUser() {
        return assignedUser;
    }

    public String getAssignedAsset() {
        return assignedAsset;
    }

    public int getAssignedLocation() {
        return assignedLocation;
    }

    public String getExpectedCheckin() {
        return expectedCheckin;
    }

    public String getCheckoutAt() {
        return checkoutAt;
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

    public void setAssignedAsset(String assignedAsset) {
        this.assignedAsset = assignedAsset;
    }

    public void setCheckoutToType(String checkoutToType) {
        this.checkoutToType = checkoutToType;
    }

    public void setAssignedLocation(int assignedLocation) {
        this.assignedLocation = assignedLocation;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }

    public void setCheckoutAt(String checkoutAt) {
        this.checkoutAt = checkoutAt;
    }

    public void setExpectedCheckin(String expectedCheckin) {
        this.expectedCheckin = expectedCheckin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNote(int note) {
        this.note = note;
    }
}
