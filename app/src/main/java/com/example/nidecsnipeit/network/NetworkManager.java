package com.example.nidecsnipeit.network;

import android.content.Context;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nidecsnipeit.model.CheckinItemModel;
import com.example.nidecsnipeit.model.CheckoutItemModel;
import com.example.nidecsnipeit.model.GetMaintenanceParamItemModel;

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

    //This method can be adapted to be used however needed for example swapping GET for POST, supplying a json object as the body instead of an empty new JsonObject();
    public void getItemRequestByAssetTag(String assetTag, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/hardware/bytag/" + assetTag;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {
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
        };
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Check in asset item
     * @param assetID
     * @param checkinItem
     * @param listener
     * @param errorListener
     */
    public void checkinItem(int assetID, CheckinItemModel checkinItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/hardware/" + assetID + "/checkin";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
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
                params.put("status", String.valueOf(checkinItem.status));
                params.put("name", checkinItem.name);
                params.put("note", checkinItem.note);
                params.put("location", checkinItem.location);

                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
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

                Field[] fields = checkoutItem.getClass().getDeclaredFields();

                for (Field field: fields) {
                    try {
                        params.put(field.getName(), String.valueOf(field.get(checkoutItem)));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public void getMaintenanceList(GetMaintenanceParamItemModel paramItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/maintenances/";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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

                Field[] fields = paramItem.getClass().getDeclaredFields();

                for (Field field: fields) {
                    try {
                        params.put(field.getName(), String.valueOf(field.get(paramItem)));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                return params;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }


    public <T> void push(String Url, T myObject, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Url, null, new Response.Listener<JSONObject>() {
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

    //This method can be adapted to be used however needed for example swapping GET for POST, supplying a json object as the body instead of an empty new JsonObject();
    public void getItemRequestByAssetTagV2(String assetTag, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL +  "/hardware/bytag/" + assetTag;
        this.get(url, null, listener, errorListener);
    }

    public <T> void get(String Url, T myObject, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
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