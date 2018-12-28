package com.ambrosus.sdk;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AssetSearchParamsBuilder {

    private final Map<String, String> queryParams = new HashMap<>();

    @NonNull
    public AssetSearchParamsBuilder from(long timestamp) {
        QueryParamsHelper.addFrom(queryParams, timestamp);
        return this;
    }

    @NonNull
    public AssetSearchParamsBuilder to(long timestamp) {
        QueryParamsHelper.addTo(queryParams, timestamp);
        return this;
    }

    @NonNull
    public AssetSearchParamsBuilder byEventIdentifier(@NonNull String eventIdentifierType, String identifier) {
        queryParams.put(String.format(Locale.US, "identifier[%s]", eventIdentifierType), identifier);
        return this;
    }

    @NonNull
    public AssetSearchParamsBuilder createdBy(@NonNull String accountAddress) {
        QueryParamsHelper.addCreatedBy(queryParams, accountAddress);
        return this;
    }

    @NonNull
    public AssetSearchParams build(){
        return new AssetSearchParams(queryParams);
    }

}
