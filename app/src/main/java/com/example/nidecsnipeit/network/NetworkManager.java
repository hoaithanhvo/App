package com.example.nidecsnipeit.network;

import android.content.Context;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nidecsnipeit.activity.LoginActivity;
import com.example.nidecsnipeit.activity.MyApplication;
import com.example.nidecsnipeit.network.model.AuditModel;
import com.example.nidecsnipeit.network.model.CheckinItemModel;
import com.example.nidecsnipeit.network.model.CheckoutItemModel;
import com.example.nidecsnipeit.network.model.GetLocationParamItemModel;
import com.example.nidecsnipeit.network.model.GetMaintenanceParamItemModel;
import com.example.nidecsnipeit.network.model.GetManufacturerParamItemModel;
import com.example.nidecsnipeit.network.model.GetModelParamItemModel;
import com.example.nidecsnipeit.network.model.ImportAssetModel;
import com.example.nidecsnipeit.network.model.LoginItemModel;
import com.example.nidecsnipeit.network.model.MaintenanceItemModel;
import com.example.nidecsnipeit.network.model.UpdateDisplayedFieldModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {

    private static final String TAG = "NetworkManager";
    private static NetworkManager instance = null;

    private final String URL;
    private final String ACCESS_TOKEN;
    public static RequestQueue requestQueue;

    private NetworkManager(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        MyApplication MyApp = (MyApplication) context.getApplicationContext();
        URL = MyApp.getUrlServer() + "/api/v1";
        ACCESS_TOKEN = MyApp.getApiKeyServer();
    }

    public static synchronized NetworkManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkManager(context.getApplicationContext());
        }
        return instance;
    }

    public static synchronized void resetInstance() {
        instance = null;
    }

    public static synchronized NetworkManager getInstance() {
        if (null == instance) {
            throw new IllegalStateException(NetworkManager.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    // =============================================
    // ===== Login with username and password ======
    // =============================================

    /**
     * Login with username and password
     *
     * @param loginItem
     * @param listener
     * @param errorListener
     */
    public void login(LoginItemModel loginItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/account/personal-access-tokens/create-from-username-password";
        this.postAPI(url, Request.Method.POST, loginItem, listener, errorListener);
    }

    // =============================================
    // ===== Logout ======
    // =============================================

    /**
     * Logout
     *
     * @param tokenId
     * @param listener
     * @param errorListener
     */
    public void logout(String tokenId, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/account/personal-access-tokens/mobile/" + tokenId;
        this.postAPI(url, Request.Method.DELETE, null, listener, errorListener);
    }

    // =============================================
    // === Check connection with API Bearer token ==
    // =============================================

    /**
     * Check connection
     *
     * @param token
     * @param listener
     * @param errorListener
     */
    public void checkConnection(String token, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/users/me";
        this.getAPI(url, Request.Method.GET, null, token, listener, errorListener);
    }

    // =============================================
    // ======= Checkin/Checkout/Transfer location ====================
    // =============================================

    /**
     * Transfer asset item
     *
     * @param assetID
     * @param listener
     * @param errorListener
     */
    public void transferItem(int assetID, CheckinItemModel transferItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/hardware/" + assetID + "/transfer";
        this.postAPI(url, Request.Method.POST, transferItem, listener, errorListener);
    }

    /**
     * Check in asset item
     *
     * @param assetID
     * @param listener
     * @param errorListener
     */
    public void checkinItem(int assetID, CheckinItemModel checkinItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/hardware/" + assetID + "/checkin";
        this.postAPI(url, Request.Method.POST, checkinItem, listener, errorListener);
    }

    /**
     * Checkout asset item
     *
     * @param assetID
     * @param checkoutItem
     * @param listener
     * @param errorListener
     */
    public void checkoutItem(int assetID, CheckoutItemModel checkoutItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/hardware/" + assetID + "/checkout";
        this.postAPI(url, Request.Method.POST, checkoutItem, listener, errorListener);
    }

    // =============================================
    // ======= AUDIT ========================
    // =============================================
    public void createAudit(AuditModel auditmodelItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/hardware/audit";
        this.postAPI(url, Request.Method.POST, auditmodelItem, listener, errorListener);
    }
//    public void createAudiOffline(List<AuditModel> auditmodelItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
//        String url = URL + "/hardware/auditListObject";
//        Type auditModelListType = new TypeToken<List<AuditModel>>() {}.getType();
//        this.postAPIV1(url, Request.Method.POST, auditmodelItem,auditModelListType, listener, errorListener);
//    }

    // =============================================
    // ======= MAINTENANCES ========================
    // =============================================
    public void getMaintenanceList(GetMaintenanceParamItemModel paramItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/maintenances/";
        this.getAPI(url, Request.Method.GET, paramItem, ACCESS_TOKEN, listener, errorListener);
    }

    public void updateMaintenanceItem(int maintenanceID, MaintenanceItemModel maintenanceItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/maintenances/" + maintenanceID;
        this.postAPI(url, Request.Method.PUT, maintenanceItem, listener, errorListener);
    }

    public void createMaintenanceItem(MaintenanceItemModel maintenanceItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/maintenances/create";
        this.postAPI(url, Request.Method.POST, maintenanceItem, listener, errorListener);
    }

    public void deleteMaintenanceItem(int maintenanceID, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/maintenances/" + maintenanceID;
        this.postAPI(url, Request.Method.DELETE, null, listener, errorListener);
    }

    // =============================================
    // ======= Supplements ========================
    // =============================================
    public void getItemRequestByAssetTag(String assetTag, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/hardware/bytag/" + assetTag;
        if (assetTag.contains("/hardware/")) {
            String[] parts = assetTag.split("/hardware/");
            url = URL + "/hardware/" + parts[parts.length - 1];
        }
        this.getAPI(url, Request.Method.GET, null, ACCESS_TOKEN, listener, errorListener);
    }

    public void getLocationsList(GetLocationParamItemModel locationParamItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/locations/list";
        this.getAPI(url, Request.Method.GET, locationParamItem, ACCESS_TOKEN, listener, errorListener);
    }

    public void getManufacturerList(GetManufacturerParamItemModel manufacturerParamItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/manufacturers";
        this.getAPI(url, Request.Method.GET, manufacturerParamItem, ACCESS_TOKEN, listener, errorListener);
    }

    public void getSupplierList(final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/suppliers";
        this.getAPI(url, Request.Method.GET, null, ACCESS_TOKEN, listener, errorListener);
    }

    public void getCategoryList(final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/categories?limit=200";
        this.getAPI(url, Request.Method.GET, null , ACCESS_TOKEN, listener, errorListener);
    }

    public void getFieldsByCategoryId(String id, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/categories/fields/byid/" + id;
        this.getAPI(url, Request.Method.GET, null, ACCESS_TOKEN, listener, errorListener);
    }

    public void updateDisplayedFieldByCategoryId(UpdateDisplayedFieldModel object, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/categories/fields/update";
        this.postAPI(url, Request.Method.POST, object, listener, errorListener);
    }

    public void getFieldsAllCategory(final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/categories/fields/all";
        this.getAPI(url, Request.Method.GET, null, ACCESS_TOKEN, listener, errorListener);
    }

    public void getModelList(GetModelParamItemModel modelParamItem, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String url = URL + "/models";
        this.getAPI(url, Request.Method.GET, modelParamItem, ACCESS_TOKEN, listener, errorListener);
    }


    //Thanh2K add api get all audit
    public void getUnAudit(GetModelParamItemModel modelParamItem,final NetworkResponseListener<JSONObject> listener,final NetworkResponseErrorListener errorListener){
        String url = URL + "/hardware/unAudit";
        this.getAPI(url,Request.Method.GET,modelParamItem,ACCESS_TOKEN,listener,errorListener);
    }

    public void postAuditObject(List<AuditModel> auditAllModel,final NetworkResponseListener<JSONObject> listener, final  NetworkResponseErrorListener errorListener){
        String url = URL + "/hardware/auditListObject";
        Type auditModelListType = new TypeToken<List<AuditModel>>() {}.getType();
        this.postAPIObject(url,Request.Method.POST,auditAllModel,auditModelListType,listener,errorListener);
    }

    //Thanh2k 10/10/2024 add api import asset to AMS
    public  void postAssetObject(ImportAssetModel importAssetModels, final NetworkResponseListener<JSONObject> listener, final  NetworkResponseErrorListener errorListener){
        String url = URL + "/hardware/createNewAssets";
        this.postAPI(url,Request.Method.POST,importAssetModels,listener,errorListener);
    }

    //Thanh2k 15/10/2024 add api get all manufacturers

    public void getCategoryAll(final NetworkResponseListener<JSONObject> listener , final  NetworkResponseErrorListener errorListener){
        String url = URL + "/categories/fields/getAll";
        this.getAPI(url,Request.Method.GET,null,ACCESS_TOKEN,listener,errorListener);
    }

    public void getManafactoryAll(int id ,final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener){
        String url = URL + "/manufacturers/getById?id=" + id;
        this.getAPI(url,Request.Method.GET,null,ACCESS_TOKEN,listener,errorListener);
    }

    public void getCatalogAll(int id, final  NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener){
        String url = URL  + "/catalogs/getByManufacturer?manufacturer_id=" + id;
        this.getAPI(url, Request.Method.GET,null,ACCESS_TOKEN,listener,errorListener);
    }

    public void getVarrials(int id , final NetworkResponseListener<JSONObject> listener, final  NetworkResponseErrorListener errorListener){
        String url = URL + "/varrials/getAll?catalog_id=" + id;
        this.getAPI(url,Request.Method.GET,null,ACCESS_TOKEN,listener,errorListener);
    }

    public void getVersion(final NetworkResponseListener<String> listener,final NetworkResponseErrorListener errorListener){
        String url = URL + "/application/getVersionApp";
        this.getAPIString(url,Request.Method.GET,null,listener,errorListener);
    }


    public void getProductDelivery(final NetworkResponseListener<JSONObject> listener,final NetworkResponseErrorListener errorListener){
        String url = URL + "/requestAsset/getAll?requestable=true";
        this.getAPI(url,Request.Method.GET,null ,ACCESS_TOKEN,listener,errorListener);
    }



    public void patchProductDevivery(int id , final NetworkResponseListener<JSONObject> listener , final NetworkResponseErrorListener errorListener ){
        String url = URL + "/requestAsset/successItemRequest?items_request_id=" + id;
        this.postAPI(url, Request.Method.PATCH,null,listener,errorListener);
    }
    public void patchCheckoutItemRequest(int id, final NetworkResponseListener<JSONObject> listener,final NetworkResponseErrorListener errorListener){
        String url = URL + "/requestAsset/checkedoutItemRequest?items_request_id=" + id;
        this.postAPI(url,Request.Method.PATCH,null,listener,errorListener);
    }
    // =============================================
    // ======= Generic method ======================
    // =============================================
    public <T> void postAPI(String Url, int httpMethod, T myObject, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(myObject);

        // Sử dụng JSONObject từ chuỗi JSON thay vì thủ công lấy các trường
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(httpMethod, Url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorListener.onErrorResult(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<>();
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

    public <T> void postAPIObject(String Url, int httpMethod, List<T> myObjectList, Type type, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        JSONArray jsonArray = null;
        JSONObject jsonObject = new JSONObject();
        if (myObjectList != null) {
            Gson gson = new Gson();
            String jsonString = gson.toJson(myObjectList, type);
            try {
                jsonArray = new JSONArray(jsonString);
                jsonObject.put("data", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(httpMethod, Url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorListener.onErrorResult(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("Content-Type", "application/json");
                headerMap.put("Authorization", "Bearer " + ACCESS_TOKEN);
                return headerMap;
            }
        };

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonArrayRequest);
    }

    public <T> void getAPI(String Url, int httpMethod, T myObject, String apiToken, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        if (myObject != null) {
            Url += this.getQueryParams(myObject);
        }
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
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Content-Type", "application/json");
                headerMap.put("Authorization", "Bearer " + apiToken);
                return headerMap;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }

    private <T> String getQueryParams(T myObject) {
        StringBuilder queryParams = new StringBuilder("?");
        Field[] fields = myObject.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                queryParams.append(field.getName()).append("=").append(String.valueOf(field.get(myObject))).append("&");
            } catch (IllegalAccessException ignored) {
            }
        }
        return queryParams.substring(0, queryParams.length() - 1);
    }

    public void getAPK(final NetworkResponseListener<byte[]> listener, final  NetworkResponseErrorListener errorListener){
        String url = URL + "/application/downloadApp";
        this.getAPKFile(url,Request.Method.GET,null,listener,errorListener);
    }

    public <T> void getAPKFile(String url, int httpMethod, T myObject, final NetworkResponseListener<byte[]> listener, final NetworkResponseErrorListener errorListener) {
        // Tạo một request để trả về mảng byte thay vì JSONObject
        final  InputStreamVolleyRequest byteRequest = new InputStreamVolleyRequest(httpMethod, url, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                listener.onResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (errorListener != null) {
                    errorListener.onErrorResult(error);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                // Không cần xác thực, không cần thêm header
                return new HashMap<>();
            }
        };

        // Cài đặt RetryPolicy
        byteRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Thêm request vào requestQueue
        requestQueue.add(byteRequest);
    }

    public <T> void getAPIString(String Url, int httpMethod, T myObject, final NetworkResponseListener<String> listener, final NetworkResponseErrorListener errorListener) {
        if (myObject != null) {
            Url += this.getQueryParams(myObject);
        }

        // Tạo StringRequest để nhận chuỗi từ API
        StringRequest stringRequest = new StringRequest(httpMethod, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        listener.onResult(response);
                       if(response.startsWith("<!DOCTYPE html>")){
                           errorListener.onErrorResult(new Exception("Received HTML instead of JSON. Possible API error or redirect."));
                       } else {
                           listener.onResult(response);
                       }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorListener.onErrorResult(error);
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("Content-Type", "application/json");
                return headerMap;
            }
        };
        // Đặt RetryPolicy cho StringRequest
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Thêm request vào hàng đợi
        requestQueue.add(stringRequest);
    }
}