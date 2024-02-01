package com.example.nidecsnipeit.network;

import org.json.JSONException;

public interface NetworkResponseListener<T> {
    void onResult(T object) throws JSONException;
}