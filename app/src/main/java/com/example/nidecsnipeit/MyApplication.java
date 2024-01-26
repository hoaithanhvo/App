package com.example.nidecsnipeit;

import android.app.Application;

import com.example.nidecsnipeit.model.DetailFieldModel;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private final List<DetailFieldModel> detailScreenFields = new ArrayList<>();
    private final List<DetailFieldModel> maintenanceScreenFields = new ArrayList<>();


    @Override
    public void onCreate() {
        super.onCreate();
        // Detail screen
        detailScreenFields.add(new DetailFieldModel("model"));
        detailScreenFields.add(new DetailFieldModel("serial"));
        detailScreenFields.add(new DetailFieldModel("name"));
        detailScreenFields.add(new DetailFieldModel("location"));
        detailScreenFields.add(new DetailFieldModel("notes"));

        // Maintenance screen
        maintenanceScreenFields.add(new DetailFieldModel("title"));
        maintenanceScreenFields.add(new DetailFieldModel("supplier"));

    }

    public List<DetailFieldModel> getDetailScreenFields() {
        return detailScreenFields;
    }
    public List<DetailFieldModel> getMaintenanceScreenFields() {
        return maintenanceScreenFields;
    }

}
