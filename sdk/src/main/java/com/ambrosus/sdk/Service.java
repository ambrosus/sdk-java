package com.ambrosus.sdk;

import com.ambrosus.sdk.models.Asset;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

interface Service {

    @GET("assets")
    Call<SearchResult<Asset>> findAsset(@QueryMap Map<String, String> options);

}
