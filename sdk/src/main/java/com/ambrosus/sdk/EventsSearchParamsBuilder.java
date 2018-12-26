package com.ambrosus.sdk;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EventsSearchParamsBuilder extends CommonSearchParamsBuilder {

    private final Map<String, String> queryParams = new HashMap<>();

    public EventsSearchParamsBuilder from(long timestamp) {
        QueryParamsHelper.addFrom(queryParams, timestamp);
        return this;
    }

    public EventsSearchParamsBuilder to(long timestamp) {
        QueryParamsHelper.addTo(queryParams, timestamp);
        return this;
    }

    public EventsSearchParamsBuilder createdBy(String accountAddress) {
        QueryParamsHelper.addCreatedBy(queryParams, accountAddress);
        return this;
    }

    public EventsSearchParamsBuilder byDataObjectIdentifier(String eventIdentifierType, String identifier) {
        return byDataObjectField(String.format(Locale.US, "identifiers.%s", eventIdentifierType), identifier);
    }

    public EventsSearchParamsBuilder byDataObjectField(String fieldName, String fieldValue) {
        queryParams.put(String.format(Locale.US, "data[%s]", fieldName), fieldValue);
        return this;
    }

    public EventSearchParams build(){
        return new EventSearchParams(queryParams);
    }

}
