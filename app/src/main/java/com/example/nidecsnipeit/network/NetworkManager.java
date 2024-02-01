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

    private String URL = "http://192.168.0.190:4402/api/v1";
    private final String ACCESS_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiYmE2MmIyNDc1MDk5OWVhYzQxMTZkY2U4Yzc2MjQxODIzZmIxODRiZjgzZDkxM2VhOWViYTNkYzBhYWU4ZTkxYTczOGE0NDU2NThhYjVhMjQiLCJpYXQiOjE3MDY1ODM1MjEuOTk3MzMsIm5iZiI6MTcwNjU4MzUyMS45OTczNDIsImV4cCI6MjE3OTk2OTEyMS45ODQyMDcsInN1YiI6IjIiLCJzY29wZXMiOltdfQ.xGT8UH10xzovBcNZg__mbLLlrSSq2nWDYHW_RDYqxZwjAJgfjavgfcpuGUAe7ihSVwLgKf9ircRqjy21QyXe1A2saDfRDSAj9NU17JBn3bRODRjr-NTQcZCLaKv3zKpTKnEp8gQlWV_iRtrBQ22PAr5516ur5sFXty-lLcnePYp7e3JdSD2rfA5LDOIG_P3rQG6SRvz-gB7xmiLEsAn5T1XYDuQ8wpwEqv_ZmSfTUauyxiLlB68IqLB5BtUmxX5GlxSASi4Zlpcvlt9LHyEhtLcAJ7b72m8D8bmlVEHFKkf6P5vi73OqjVOgBZtSylepTbMGyy8UJPXNlGAIa5ikX4Go27L1_-1NNXN7y9aUat19FGtbbgKwDoIzPcMbcRe9b43ZzKOkMO49X-ydLBh7X2ojEoN_SXWO91Ndc8aNMrgRi7X1HEuZx7v6NCp4_Ru0Cgrye4B5qCU2DwG3BsSt7pCZQT2C7uY1ARg_uNXeydtljBevgH2OuJshXBN_uWBltZ9KJjS-Y7ZELsVhZCFuW92PCgcFgiAibvxhljFlCz2PfWMncNsuAyjyn4w9BRDW5wVOxeITR1uVNKWmNT5tdXwe-sk_Y6gpdzTC5ME0CET3ylvH4QUUvLztsa5wSvJy4nBv_C5COgBv55A_IMddNn2HEKduWtz10DYCvLKy3A0";
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
     * @param listener
     * @param errorListener
     */
    public void checkinItem(int assetID, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/hardware/" + assetID + "/checkin";
        this.postAPI(url, Request.Method.POST, null, listener, errorListener);
    }

    /**
     * Checkout asset item
     * @param assetID
     * @param checkoutItem
     * @param listener
     * @param errorListener
     */
    public void checkoutItem(int assetID, CheckoutItemModel checkoutItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/hardware/" + assetID + "/checkout";
        this.postAPI(url, Request.Method.POST, checkoutItem, listener, errorListener);
    }

    // =============================================
    // ======= MAINTENANCES ========================
    // =============================================
    public void getMaintenanceList(GetMaintenanceParamItemModel paramItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/maintenances/";
        this.getAPI(url, Request.Method.GET, paramItem, listener, errorListener);
    }

    public void updateMaintenanceItem(int maintenanceID, MaintenanceItemModel maintenanceItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/maintenances/" + maintenanceID;
        this.postAPI(url, Request.Method.PUT, maintenanceItem, listener, errorListener);
    }

    public void createMaintenanceItem(MaintenanceItemModel maintenanceItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/maintenances/";
        this.postAPI(url, Request.Method.POST, maintenanceItem, listener, errorListener);
    }

    public void deleteMaintenanceItem(int maintenanceID, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/maintenances/" + maintenanceID;
        this.postAPI(url, Request.Method.DELETE, null, listener, errorListener);
    }

    // =============================================
    // ======= Supplements ========================
    // =============================================
    public void getItemRequestByAssetTag(String assetTag, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/hardware/bytag/" + assetTag;
        if (assetTag.contains("/hardware/")) {
            String[] parts = assetTag.split("/hardware/");
            url = URL +  "/hardware/" + parts[1];
        }
        this.getAPI(url, Request.Method.GET, null, listener, errorListener);
    }

    public void getLocationsList(GetLocationParamItemModel locationParamItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/locations";
        this.getAPI(url, Request.Method.GET, locationParamItem, listener, errorListener);
    }

    public void getManufacturerList(GetManufacturerParamItemModel manufacturerParamItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/manufacturers";
        this.getAPI(url, Request.Method.GET, manufacturerParamItem, listener, errorListener);
    }

    public void getSupplierList(final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/suppliers";
        this.getAPI(url, Request.Method.GET, null, listener, errorListener);
    }

    public void getCategoryList(GetCategoryParamItemModel categoryParamItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/categories";
        this.getAPI(url, Request.Method.GET, categoryParamItem, listener, errorListener);
    }

    public void getModelList(GetModelParamItemModel modelParamItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/models";
        this.getAPI(url, Request.Method.GET, modelParamItem, listener, errorListener);
    }

    // =============================================
    // ======= Generic method ======================
    // =============================================
    public <T> void postAPI(String Url, int httpMethod, T myObject, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        Map<String, String> params = new HashMap<String, String>();
        JSONObject jsonObject = null;
        if (myObject != null) {
            Field[] fields = myObject.getClass().getDeclaredFields();

            for (Field field: fields) {
                try {
                    params.put(field.getName(), String.valueOf(field.get(myObject)));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            jsonObject = new JSONObject(params);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(httpMethod, Url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    listener.onResult(response);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
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
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }
    public <T> void getAPI(String Url, int httpMethod, T myObject, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        if (myObject != null) {
            Url += this.getQueryParams(myObject);
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(httpMethod, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    listener.onResult(response);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
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
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }
    private <T> String getQueryParams(T myObject)
    {
        StringBuilder queryParams = new StringBuilder("?");
        Field[] fields = myObject.getClass().getDeclaredFields();

        for (Field field: fields) {
            try {
                queryParams.append(field.getName()).append("=").append(String.valueOf(field.get(myObject))).append("&");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return queryParams.substring(0, queryParams.length() - 1);
    }
}