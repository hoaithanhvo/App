package com.example.nidecsnipeit;

import android.app.Application;

import com.example.nidecsnipeit.model.DetailFieldModel;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private final List<DetailFieldModel> detailScreenFields = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        detailScreenFields.add(new DetailFieldModel("model"));
        detailScreenFields.add(new DetailFieldModel("serial"));
        detailScreenFields.add(new DetailFieldModel("name"));
        detailScreenFields.add(new DetailFieldModel("location"));
        detailScreenFields.add(new DetailFieldModel("notes"));
    }

    public List<DetailFieldModel> getDetailScreenFields() {
        return detailScreenFields;
    }

}
