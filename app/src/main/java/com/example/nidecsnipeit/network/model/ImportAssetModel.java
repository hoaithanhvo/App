package com.example.nidecsnipeit.network.model;

import java.util.ArrayList;
import java.util.List;

public class ImportAssetModel {
    public String varrial_id;
    public String category_id;
    public String name;
    private String model_id;
    public List<Asset> assets;

    public ImportAssetModel() {
        assets = new ArrayList<>();
    }

    public void addAsset( String serial) {
        this.assets.add(new Asset(serial));
    }

    public static class Asset {
        public String serial;
        public Asset(String serial) {
            this.serial = serial;
        }
    }
}




