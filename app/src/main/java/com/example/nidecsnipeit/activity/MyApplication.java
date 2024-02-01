package com.example.nidecsnipeit.activity;

import android.app.Application;

import com.example.nidecsnipeit.model.DetailFieldModel;
import com.example.nidecsnipeit.model.ListItemModel;

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
        detailScreenFields.add(new DetailFieldModel("assigned_to"));
        detailScreenFields.add(new DetailFieldModel("notes"));

        // Maintenance screen
        maintenanceScreenFields.add(new DetailFieldModel("supplier", ListItemModel.Mode.DROPDOWN, true));
        maintenanceScreenFields.add(new DetailFieldModel("title", ListItemModel.Mode.EDIT_TEXT));

    }

    public List<DetailFieldModel> getDetailScreenFields() {
        return detailScreenFields;
    }
    public List<DetailFieldModel> getMaintenanceScreenFields() {
        return maintenanceScreenFields;
    }
    public boolean isRequiredIdDropdown(String fieldName) {
        for (DetailFieldModel field : maintenanceScreenFields) {
            if (field.getName().equals(fieldName) && field.getRequiredId()) {
                return true;
            }
        }
        return false;
    }

}
