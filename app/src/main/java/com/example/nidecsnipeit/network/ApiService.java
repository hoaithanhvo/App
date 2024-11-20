package com.example.nidecsnipeit.network;

import androidx.cardview.widget.CardView;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import java.util.Map;
public interface ApiService {
    @GET
    Call<ResponseBody> getDynamicAPI(@Url String url);

    @POST
    Call<ResponseBody> postDynamicAPI(@Url String fullUrl, @Body Object dynamicBody);


}
