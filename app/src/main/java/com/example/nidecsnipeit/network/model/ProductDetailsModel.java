package com.example.nidecsnipeit.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class ProductDetailsModel implements Parcelable {
    private String Category;
    private String Manufactory;
    private String Model;
    private String Varrial;
    public static int item_request_id;

    private String Total;
    private String Created;
    private Map<String, String> statusMap = new HashMap<>();
    private JSONArray item_request_details;

    public String getHandOver() {
        return handOver;
    }

    public void setHandOver(String handOver) {
        this.handOver = handOver;
    }

    private String handOver;

    public ProductDetailsModel() {
    }

    // Các getter và setter
    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getManufactory() {
        return Manufactory;
    }

    public void setManufactory(String manufactory) {
        Manufactory = manufactory;
    }

    public String getCatalog() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getVarrial() {
        return Varrial;
    }

    public void setVarrial(String varrial) {
        Varrial = varrial;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getCreated() {
        return Created;
    }

    public void setCreated(String created) {
        Created = created;
    }

    public Map<String, String> getStatusMap() {
        return statusMap;
    }

    public void setStatusMap(String nameStatus, String colorStatus) {
        statusMap.put("name", nameStatus);
        statusMap.put("color", colorStatus);
    }

    public JSONArray getItem_request_details() {
        return item_request_details;
    }

    public void setItem_request_details(JSONArray item_request_details) {
        this.item_request_details = item_request_details;
    }

    // Triển khai Parcelable
    protected ProductDetailsModel(Parcel in) {
        Category = in.readString();
        Manufactory = in.readString();
        Model = in.readString();
        Varrial = in.readString();
        Total = in.readString();
        Created = in.readString();
        item_request_id = in.readInt();

        // Đọc statusMap
        int statusMapSize = in.readInt();
        statusMap = new HashMap<>();
        for (int i = 0; i < statusMapSize; i++) {
            String key = in.readString();
            String value = in.readString();
            statusMap.put(key, value);
        }

        try {
            item_request_details = new JSONArray(in.readString());
        } catch (Exception e) {
            item_request_details = null;
        }
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(Category);
        dest.writeString(Manufactory);
        dest.writeString(Model);
        dest.writeString(Varrial);
        dest.writeString(Total);
        dest.writeString(Created);
        dest.writeInt(item_request_id);

        // Ghi statusMap
        dest.writeInt(statusMap.size());
        for (Map.Entry<String, String> entry : statusMap.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }

        dest.writeString(item_request_details != null ? item_request_details.toString() : null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductDetailsModel> CREATOR = new Creator<ProductDetailsModel>() {
        @Override
        public ProductDetailsModel createFromParcel(Parcel in) {
            return new ProductDetailsModel(in);
        }

        @Override
        public ProductDetailsModel[] newArray(int size) {
            return new ProductDetailsModel[size];
        }
    };
}
