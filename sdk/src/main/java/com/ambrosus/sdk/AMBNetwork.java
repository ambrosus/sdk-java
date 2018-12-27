package com.ambrosus.sdk;

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

    public AMBNetworkCall<SearchResult<Asset>> findAssets(AssetSearchParams searchParams) {
        return new NetworkCallWrapper<>(service.findAssets(searchParams.queryParams));
    }

    public AMBNetworkCall<SearchResult<Event>> findEvents(EventSearchParams searchParams) {
        return new NetworkCallWrapper<>(service.findEvents(searchParams.queryParams));
    }

    public AMBNetworkCall<Asset> getAsset(String assetId) {
        return new NetworkCallWrapper<>(service.getAsset(assetId), new MissingEntityErrorHandler());
    }

}
