package com.example.nidecsnipeit.network;

import android.content.Context;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nidecsnipeit.model.CheckinItemModel;
import com.example.nidecsnipeit.model.CheckoutItemModel;
import com.example.nidecsnipeit.model.GetCategoryParamItemModel;
import com.example.nidecsnipeit.model.GetLocationParamItemModel;
import com.example.nidecsnipeit.model.GetMaintenanceParamItemModel;
import com.example.nidecsnipeit.model.GetManufacturerParamItemModel;
import com.example.nidecsnipeit.model.GetModelParamItemModel;
import com.example.nidecsnipeit.model.GetSupplierParamItemModel;
import com.example.nidecsnipeit.model.MaintenanceItemModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class NetworkManager {

    private static final String TAG = "NetworkManager";
    private static NetworkManager instance = null;

    private String URL = "http://192.168.0.98:4402/api/v1";
    private final String ACCESS_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiZTY3NzIwNzQ0ZTkxOTU3MjYzZWEzMmJkZGI4M2MxZTQwN2NlMDYwOWUzNzYzMTQ1NTUxMjZhOTNiNWJkZjY1YTViMWJiNjM1YzRmMGUzYjIiLCJpYXQiOjE3MDYwNzYzMTAuMjEzMDY2LCJuYmYiOjE3MDYwNzYzMTAuMjEzMDcyLCJleHAiOjIxNzk0NjE5MTAuMjAxNzAyLCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.ObzNIS-Ah31t80YtYLueYVmRgT1uycJu7J1L_e6QEXu85s59bSjhrrp7964Ln6AZOz-H5-Xq581CL39PDeaYtb5MeScUsRvTBAYRYJalBF33nCwwSBk6gFUFk-OwnqeyPolzPVdOirU_ZlzSl8rsWCB0B_B9UC2nfTJblG28H9rmkYvH1AIhL_yjBP-5ksqtBYmXbU5Yeg7vIIa4gHREqV56uX69zXxeFXfp3DfYGjWkPtwWuoqOQjlkSCBFEhdK53ZiZQvalYCRG5k9uNfbomz3g9xzrh70L3GEUVI9hjJTOg_zw8bIn5Sn6h5_d9guXR1Ocn0vVx5pDUgiAKoKzxEkrHLytYr9pQ-mKHvbbm_FZdXNZKZDSdUBebmUP0WW2_5Yqh91YA0HqixoxQhrdV5JA42aMxW5HCVjW0o2sHul0yuREtONslvrvESCompMB_q5fqxxtwjfVvlT_6DlSZdE4qjdDVqHzr-NCu0V6eDVkGzCLPKEwdEJqqiFIT5FupX07j576InuWME4fh9mtK6zUQvBwKbEIvsKDSjKmRZgy1tfRwvV1LkzSYVqwrD2zYHdZWPpRNgwOSEA43qv5FnRhG4UaHRIFizbqwxoC7jlJ21kSsM_sCQtcB20zUShxzzNVjs-FLdjzbeC9ArO3n0Bldy7DwiUeb_7YUjspCQ";

    public RequestQueue requestQueue;

    private NetworkManager(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public void setServerURL(String serverURL) {
        URL = serverURL;
    }

    public String getServerURL() {
        return this.URL;
    }

    public static synchronized NetworkManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkManager(context);
        }
        return instance;
    }

    public static synchronized NetworkManager getInstance() {
        if (null == instance) {
            throw new IllegalStateException(NetworkManager.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    // =============================================
    // ======= Checkin/Checkout ====================
    // =============================================

    /**
     * Check in asset item
     * @param assetID
     * @param checkinItem
     * @param listener
     * @param errorListener
     */
    public void checkinItem(int assetID, CheckinItemModel checkinItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/hardware/" + assetID + "/checkin";
        this.callAPI(url, Request.Method.POST, checkinItem, listener, errorListener);
    }

    /**
     * Checkout asset item
     * @param assetID
     * @param checkoutItem
     * @param listener
     * @param errorListener
     */
    public void checkoutItem(int assetID, CheckoutItemModel checkoutItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/hardware/" + assetID + "/checkin";
        this.callAPI(url, Request.Method.POST, checkoutItem, listener, errorListener);
    }

    // =============================================
    // ======= MAINTENANCES ========================
    // =============================================
    public void getMaintenanceList(GetMaintenanceParamItemModel paramItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/maintenances/";
        this.callAPI(url, Request.Method.GET, paramItem, listener, errorListener);
    }

    public void updateMaintenanceItem(int maintenanceID, MaintenanceItemModel maintenanceItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/maintenances/" + maintenanceID;
        this.callAPI(url, Request.Method.PUT, maintenanceItem, listener, errorListener);
    }

    public void createMaintenanceItem(MaintenanceItemModel maintenanceItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/maintenances/";
        this.callAPI(url, Request.Method.POST, maintenanceItem, listener, errorListener);
    }

    public void deleteMaintenanceItem(int maintenanceID, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/maintenances/" + maintenanceID;
        this.callAPI(url, Request.Method.DELETE, null, listener, errorListener);
    }

    // =============================================
    // ======= Supplements ========================
    // =============================================
    public void getItemRequestByAssetTag(String assetTag, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/hardware/bytag/" + assetTag;
        this.callAPI(url, Request.Method.GET, null, listener, errorListener);
    }

    public void getLocationsList(GetLocationParamItemModel locationParamItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/locations";
        this.callAPI(url, Request.Method.GET, locationParamItem, listener, errorListener);
    }

    public void getManufacturerList(GetManufacturerParamItemModel manufacturerParamItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/manufacturers";
        this.callAPI(url, Request.Method.GET, manufacturerParamItem, listener, errorListener);
    }

    public void getSupplierList(GetSupplierParamItemModel supplierParamItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/suppliers";
        this.callAPI(url, Request.Method.GET, supplierParamItem, listener, errorListener);
    }

    public void getCategoryList(GetCategoryParamItemModel categoryParamItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/categories";
        this.callAPI(url, Request.Method.GET, categoryParamItem, listener, errorListener);
    }

    public void getModelList(GetModelParamItemModel modelParamItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/models";
        this.callAPI(url, Request.Method.GET, modelParamItem, listener, errorListener);
    }

    // =============================================
    // ======= Generic method ======================
    // =============================================
    public <T> void callAPI(String Url, int httpMethod, T myObject, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(httpMethod, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorListener.onErrorResult(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Content-Type", "application/json");
                headerMap.put("Authorization", "Bearer " + ACCESS_TOKEN);
                return headerMap;
            }

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();

                Field[] fields = myObject.getClass().getDeclaredFields();

                for (Field field: fields) {
                    try {
                        params.put(field.getName(), String.valueOf(field.get(myObject)));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
}