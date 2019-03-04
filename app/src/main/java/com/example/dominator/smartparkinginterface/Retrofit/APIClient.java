package com.example.dominator.smartparkinginterface.Retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String link) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);

        // Creates the json object which will manage the information received
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Register an adapter to manage the date types as long values
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

        Gson gson = gsonBuilder.setLenient().create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(1, TimeUnit.MINUTES) // connect timeout
                .writeTimeout(1, TimeUnit.MINUTES) // write timeout
                .readTimeout(1, TimeUnit.MINUTES); // read timeout

        OkHttpClient client = builder.addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(link)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();



        return retrofit;
    }

    public Object ObjectConverter(Object treeMapResponse, Object convertClass){

        GsonBuilder gsonBuilder = new GsonBuilder();

        // Register an adapter to manage the date types as long values
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

        Gson gson = gsonBuilder.setLenient().create();

        if(treeMapResponse.getClass().equals(LinkedTreeMap.class)) {
            return gson.fromJson(gson.toJson((LinkedTreeMap) treeMapResponse), convertClass.getClass());
        }
        else{
            return gson.fromJson(gson.toJson(treeMapResponse), convertClass.getClass());
        }
    }
}
