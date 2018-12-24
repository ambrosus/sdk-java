package com.ambrosus.sdk;

import java.util.Locale;

public class EventsSearchParamsBuilder extends CommonSearchParamsBuilder {

    EventsSearchParamsBuilder byAssetId(String assetId) {
        queryParams.put("assetId", assetId);
        return this;
    }

    EventsSearchParamsBuilder byEventIdentifier(String eventIdentifierType, String identifier) {
        return byDataObjectField(String.format(Locale.US, "identifier.%s", eventIdentifierType), identifier);
    }

    EventsSearchParamsBuilder byDataObjectField(String fieldName, String fieldValue) {
        queryParams.put(String.format(Locale.US, "data[%s]", fieldName), fieldValue);
        return this;
    }


}
