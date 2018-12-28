package com.ambrosus.sdk;

import android.support.annotation.NonNull;

import com.ambrosus.sdk.models.Asset;
import com.ambrosus.sdk.models.Event;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AMBNetwork {

    private final Service service;

    public AMBNetwork(){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                System.out.println(message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gateway-test.ambrosus.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(Service.class);
    }

    @NonNull
    public AMBNetworkCall<SearchResult<Asset>> findAssets(@NonNull AssetSearchParams searchParams) {
        return new NetworkCallWrapper<>(service.findAssets(searchParams.queryParams));
    }

    @NonNull
    public AMBNetworkCall<SearchResult<Event>> findEvents(@NonNull EventSearchParams searchParams) {
        return new NetworkCallWrapper<>(service.findEvents(searchParams.queryParams));
    }

    @NonNull
    public AMBNetworkCall<Asset> getAsset(@NonNull String assetId) {
        return new NetworkCallWrapper<>(service.getAsset(assetId), new MissingEntityErrorHandler());
    }

    @NonNull
    public AMBNetworkCall<Event> getEvent(@NonNull String eventId) {
        return new NetworkCallWrapper<>(service.getEvent(eventId), new MissingEntityErrorHandler());
    }

}
