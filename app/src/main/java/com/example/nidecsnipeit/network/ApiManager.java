package com.example.nidecsnipeit.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.example.nidecsnipeit.activity.MyApplication;
import com.example.nidecsnipeit.utility.RetrofitClientHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ApiManager {
    private ApiService apiService;
    private final String ACCESS_TOKEN;
    private final String URL;
    private static ApiManager instance = null;

    public static synchronized ApiManager getInstance(Context context) {
        if (instance == null) {
            instance = new ApiManager(context.getApplicationContext());
        }
        return instance;
    }

    public static synchronized void resetInstance() {
        instance = null;
    }

    public static synchronized ApiManager getInstance() {
        if (null == instance) {
            throw new IllegalStateException(NetworkManager.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    public ApiManager(Context context) {
        MyApplication MyApp = (MyApplication) context.getApplicationContext();
        URL = MyApp.getUrlServer() + "/api/v1/";
        ACCESS_TOKEN = MyApp.getApiKeyServer();
    }

    public void getProductDelivery(Map<String, String> params,
                                   final NetworkResponseListener<JSONObject> listener,
                                   final NetworkResponseErrorListener errorListener) {
        String endpoint = "requestAsset/getAll";
        this.callGetApi(endpoint,params,listener,errorListener);
    }

    public void getManafactoryAll(Map<String,String> params,final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener){
        String endpoint = "manufacturers/getById";
        this.callGetApi(endpoint,params ,listener,errorListener);
    }


    public void getModelById( Map<String,String> params,final  NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener){
        String endpoint =  "models";
        this.callGetApi(endpoint,params,listener,errorListener);
    }

    public void getVarrials(Map<String,String> params, final NetworkResponseListener<JSONObject> listener, final  NetworkResponseErrorListener errorListener){
        String endpoint =  "varrials/getAll";
        this.callGetApi(endpoint,params,listener,errorListener);
    }

    private void callGetApi(String endpoint, Map<String,String> params, final NetworkResponseListener<JSONObject> listener, final NetworkResponseErrorListener errorListener) {
        String urlWithParams = RetrofitClientHelper.urlEncodeUTF8(params);
        String fullURL = buildUrl(URL ,endpoint , params);
        Retrofit retrofit = RetrofitClientHelper.getClient(URL, ACCESS_TOKEN);
        this.apiService = retrofit.create(ApiService.class);
        Call<ResponseBody> call = apiService.getDynamicAPI(fullURL);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String rawResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(rawResponse);
                        listener.onResult(jsonObject);
                    } catch (Exception e) {
                        errorListener.onErrorResult(e);
                    }
                } else {
                    errorListener.onErrorResult(new Exception(response.errorBody().toString()));
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (errorListener != null) {
                    errorListener.onErrorResult(new Exception(t.getMessage()));
                }
            }
        });
    }

    private String buildUrl(String baseUrl, String endpoint, Map<String, String> params) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + endpoint).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        return urlBuilder.build().toString();
    }

    public void postAssetObject(Map<String,Object> params, final NetworkResponseListener<JSONObject> listener, final  NetworkResponseErrorListener errorListener){
        String endpoint =  "/hardware/createNewAssets";
        this.postDynamicData(endpoint,params,listener,errorListener);
    }


    public void postDynamicData(String endpoint, Map<String, Object> bodyData, final NetworkResponseListener<JSONObject> listener , final NetworkResponseErrorListener errorListener) {
        Retrofit retrofit = RetrofitClientHelper.getClient(URL,ACCESS_TOKEN);
        ApiService apiService = retrofit.create(ApiService.class);

        // Xây dựng URL đầy đủ
        String fullUrl = URL + endpoint;

        // Gọi API
        Call<ResponseBody> call = apiService.postDynamicAPI(fullUrl, bodyData);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String rawResponse = response.body().string();
                        System.out.println("Response: " + rawResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Error: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("Request failed: " + t.getMessage());
            }
        });
    }

}
