package com.ambrosus.sdk;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CommonSearchParamsBuilder {

    protected final Map<String, String> queryParams = new HashMap<>();

    public CommonSearchParamsBuilder from(long timestamp) {
        queryParams.put("fromTimestamp", Long.toString(timestamp));
        return this;
    }

    public CommonSearchParamsBuilder to(long timestamp) {
        queryParams.put("toTimestamp", Long.toString(timestamp));
        return this;
    }

    public CommonSearchParamsBuilder createdBy(String accountAddress) {
        queryParams.put("createdBy", accountAddress);
        return this;
    }

    public AssetSearchParams build(){
        return new AssetSearchParams(queryParams);
    }


}
