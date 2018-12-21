package com.ambrosus.sdk;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AssetSearchParamsBuilder {

    private final Map<String, String> queryParams = new HashMap<>();

    public AssetSearchParamsBuilder from(long timestamp) {
        QueryParamsHelper.addFrom(queryParams, timestamp);
        return this;
    }

    public AssetSearchParamsBuilder to(long timestamp) {
        QueryParamsHelper.addTo(queryParams, timestamp);
        return this;
    }

    public AssetSearchParamsBuilder byEventIdentifier(String eventIdentifierType, String identifier) {
        queryParams.put(String.format(Locale.US, "identifier[%s]", eventIdentifierType), identifier);
        return this;
    }
    public AssetSearchParamsBuilder createdBy(String accountAddress) {
        queryParams.put("createdBy", accountAddress);
        return this;
    }

    public AssetSearchParams build(){
        return new AssetSearchParams(queryParams);
    }


}
