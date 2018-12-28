package com.ambrosus.sdk;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EventsSearchParamsBuilder {

    private final Map<String, String> queryParams = new HashMap<>();

    @NonNull
    public EventsSearchParamsBuilder from(long timestamp) {
        QueryParamsHelper.addFrom(queryParams, timestamp);
        return this;
    }

    @NonNull
    public EventsSearchParamsBuilder to(long timestamp) {
        QueryParamsHelper.addTo(queryParams, timestamp);
        return this;
    }

    @NonNull
    public EventsSearchParamsBuilder createdBy(@NonNull String accountAddress) {
        QueryParamsHelper.addCreatedBy(queryParams, accountAddress);
        return this;
    }

    @NonNull
    public EventsSearchParamsBuilder byDataObjectIdentifier(@NonNull String eventIdentifierType, @NonNull String identifier) {
        return byDataObjectField(String.format(Locale.US, "identifiers.%s", eventIdentifierType), identifier);
    }

    @NonNull
    public EventsSearchParamsBuilder byDataObjectField(@NonNull String fieldName, String fieldValue) {
        queryParams.put(String.format(Locale.US, "data[%s]", fieldName), fieldValue);
        return this;
    }

    @NonNull
    public EventSearchParams build(){
        return new EventSearchParams(queryParams);
    }

}
