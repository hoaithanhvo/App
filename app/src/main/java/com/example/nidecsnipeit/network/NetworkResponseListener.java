package com.example.nidecsnipeit.network;
public interface NetworkResponseListener<T> {
    void onResult(T object);
}