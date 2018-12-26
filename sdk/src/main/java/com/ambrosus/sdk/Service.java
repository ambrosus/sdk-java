package com.ambrosus.sdk;

import com.ambrosus.sdk.models.Asset;
import com.ambrosus.sdk.models.Event;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

interface Service {

    @GET("assets")
    Call<SearchResult<Asset>> findAssets(@QueryMap Map<String, String> options);

    @GET("events")
    Call<SearchResult<Event>> findEvents(@QueryMap Map<String, String> options);

}
