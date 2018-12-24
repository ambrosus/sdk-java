package com.ambrosus.sdk;

import com.ambrosus.sdk.CommonSearchParamsBuilder;

import java.util.Locale;

public class AssetSearchParamsBuilder extends CommonSearchParamsBuilder {

    public CommonSearchParamsBuilder byEventIdentifier(String eventIdentifierType, String identifier) {
        queryParams.put(String.format(Locale.US, "identifier[%s]", eventIdentifierType), identifier);
        return this;
    }


}
