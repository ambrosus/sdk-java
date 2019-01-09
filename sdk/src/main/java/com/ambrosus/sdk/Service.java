package com.ambrosus.sdk;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

interface Service {

    @GET("assets/{assetId}")
    Call<Asset> getAsset(@Path("assetId") String assetId);

    @GET("events/{eventId}")
    Call<Event> getEvent(@Path("eventId") String assetId);


    @GET("assets")
    Call<SearchResult<Asset>> findAssets(@QueryMap Map<String, String> options);

    @GET("events")
    Call<SearchResult<Event>> findEvents(@QueryMap Map<String, String> options);

}
