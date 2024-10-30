package com.example.nidecsnipeit.network.model;

public class AuditModel implements Comparable<AuditModel>{
    public String inventory_number;
    public String asset_tag;
    public String label_status;
    public String asset_status;
    public String location_id;
    public String current_location;

    public String getLabel_status() {
        return label_status;
    }

    public String getAsset_status() {
        return asset_status;
    }

    public void setAsset_status(String asset_status) {
        this.asset_status = asset_status;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public AuditModel() {
    }

    public String getCurrent_location() {
        return current_location;
    }

    public void setCurrent_location(String current_location) {
        this.current_location = current_location;
    }

    public String getLast_used() {
        return last_used;
    }

    public void setLast_used(String last_used) {
        this.last_used = last_used;
    }

    public String last_used;
    public String getInventory_number() {
        return inventory_number;
    }

    public void setInventory_number(String inventory_number) {
        this.inventory_number = inventory_number;
    }

    public String getAsset_tag() {
        return asset_tag;
    }

    public void setAsset_tag(String asset_tag) {
        this.asset_tag = asset_tag;
    }

    public String isLabel_status() {
        return label_status;
    }

    public void setLabel_status(String label_status) {
        this.label_status = label_status;
    }

    public String asset_status() {
        return asset_status;
    }

    public void getAsset_status(String asset_status) {
        this.asset_status = asset_status;
    }

    public String getLocation() {
        return location_id;
    }

    public void setLocation(String location) {
        this.location_id = location_id;
    }


    public AuditModel(String inventory_number, String asset_tag, String label_status, String asset_status, String location_id,String last_used) {
        this.inventory_number = inventory_number;
        this.asset_tag = asset_tag;
        this.label_status = label_status;
        this.asset_status = asset_status;
        this.location_id = location_id;
        this.last_used = last_used;
    }
    @Override
    public int compareTo(AuditModel other) {
        return this.inventory_number.compareTo(other.inventory_number);
    }
}
