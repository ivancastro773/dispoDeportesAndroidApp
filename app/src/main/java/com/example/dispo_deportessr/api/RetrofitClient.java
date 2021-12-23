package com.example.dispo_deportessr.api;

import com.example.dispo_deportessr.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    //configuramos la URL del servidor
    private  static  final String BASE_URL = "https://dispo-deportes-sr.herokuapp.com";
    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    //configuramos retrofit
    private RetrofitClient(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance(){
        if(mInstance == null){
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }
    public Api getApi(){
        return retrofit.create(Api.class);
    }
}
