package com.ambrosus.sdk;

import android.support.annotation.NonNull;

import com.ambrosus.sdk.model.Identifier;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {

    private final Service service;

    public Network(){

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

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Event.class, new EventDeserializer());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gateway-test.ambrosus.com/")
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .client(client)
                .build();

        service = retrofit.create(Service.class);
    }

    @NonNull
    public NetworkCall<Asset> getAsset(@NonNull String assetId) {
        return new TransparentNetworkCallWrapper<>(service.getAsset(assetId), new MissingEntityErrorHandler());
    }

    @NonNull
    public NetworkCall<Event> getEvent(@NonNull String eventId) {
        return new TransparentNetworkCallWrapper<>(service.getEvent(eventId), new MissingEntityErrorHandler());
    }

    @NonNull
    public NetworkCall<SearchResult<Asset>> findAssets(@NonNull AssetSearchParams searchParams) {
        return new TransparentNetworkCallWrapper<>(service.findAssets(searchParams.queryParams));
    }

    @NonNull
    public NetworkCall<SearchResult<Event>> findEvents(@NonNull EventSearchParams searchParams) {
        return new TransparentNetworkCallWrapper<>(service.findEvents(searchParams.queryParams));
    }

    //TODO return SearchResult when pagination will be ready
    public <T extends Event> NetworkCall<List<T>> findEvents(@NonNull EventSearchParams searchParams, EventFactory<T> factory) {
        return new NetworkCallWrapper<>(service.findEvents(searchParams.queryParams), new EventsSearchResultAdapter<>(factory));
    }
}
