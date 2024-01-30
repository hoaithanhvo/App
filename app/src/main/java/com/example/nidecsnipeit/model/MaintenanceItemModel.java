package com.example.nidecsnipeit.model;

import java.io.Serializable;

public class MaintenanceItemModel implements Serializable {
    public int id;
    public String title;
    public int asset_id;
    public int supplier_id;
    public String supplier_name;
    public String asset_maintenance_type;
    public String start_date;
    public MaintenanceItemModel() {

    }

    public MaintenanceItemModel(int id, String title, int asset_id, int supplier_id, String supplier_name, String asset_maintenance_type, String start_date) {
        this.id = id;
        this.title = title;
        this.asset_id = asset_id;
        this.supplier_id = supplier_id;
        this.asset_maintenance_type = asset_maintenance_type;
        this.start_date = start_date;
        this.supplier_name = supplier_name;
    }

    public MaintenanceItemModel(String title, int asset_id, int supplier_id, String supplier_name, String asset_maintenance_type, String start_date) {
        this.title = title;
        this.asset_id = asset_id;
        this.supplier_id = supplier_id;
        this.asset_maintenance_type = asset_maintenance_type;
        this.start_date = start_date;
        this.supplier_name = supplier_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}
