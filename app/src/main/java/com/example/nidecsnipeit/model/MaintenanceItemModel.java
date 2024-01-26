package com.example.nidecsnipeit.model;

public class MaintenanceItemModel {
    public String title;
    public int asset_id;
    public int supplier_id;
    public String supplier_name;
    public Boolean is_warranty;
    public float cost;
    public String notes;
    public String asset_maintenance_type;
    public String start_date;
    public String completion_date;

    public MaintenanceItemModel(String title, int asset_id, int supplier_id, String supplier_name, Boolean is_warranty, float cost, String notes, String asset_maintenance_type, String start_date, String completion_date) {
        this.title = title;
        this.asset_id = asset_id;
        this.supplier_id = supplier_id;
        this.is_warranty = is_warranty;
        this.cost = cost;
        this.notes = notes;
        this.asset_maintenance_type = asset_maintenance_type;
        this.start_date = start_date;
        this.completion_date = completion_date;
        this.supplier_name = supplier_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAssetID() {
        return asset_id;
    }

    public void setAssetID(int asset_id) {
        this.asset_id = asset_id;
    }

    public int getSupplierID() {
        return supplier_id;
    }

    public void setSupplierID(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getSupplierName() {
        return supplier_name;
    }

    public Boolean isIsWarranty() {
        return is_warranty;
    }

    public void setIsWarranty(Boolean is_warranty) {
        this.is_warranty = is_warranty;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAssetMaintenanceType() {
        return asset_maintenance_type;
    }

    public void setAssetMaintenanceType(String asset_maintenance_type) {
        this.asset_maintenance_type = asset_maintenance_type;
    }

    public String getStartDate() {
        return start_date;
    }

    public void setStartDate(String start_date) {
        this.start_date = start_date;
    }

    public String getCompletionDate() {
        return completion_date;
    }

    public void setCompletionDate(String completion_date) {
        this.completion_date = completion_date;
    }
}
