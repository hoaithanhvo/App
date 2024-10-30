package com.example.nidecsnipeit.network.model;

public class CheckoutItemModel {
    public int status_id = 5;
    public String checkout_to_type;
    public int assigned_location;

    public String name;

    public CheckoutItemModel(int assigned_location, String name) {
        this.checkout_to_type = "location";
        this.assigned_location = assigned_location;
        this.name = name;
    }

    public String getCheckoutToType() {
        return checkout_to_type;
    }

    public int getAssigned_location() {
        return assigned_location;
    }

    public String getName() {
        return name;
    }

    public void setCheckoutToType(String checkout_to_type) {
        this.checkout_to_type = checkout_to_type;
    }

    public void setAssigned_location(int assigned_location) {
        this.assigned_location = assigned_location;
    }

    public void setName(String name) {
        this.name = name;
    }
}
