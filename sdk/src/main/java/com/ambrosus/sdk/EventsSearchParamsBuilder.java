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

    public EventsSearchParamsBuilder byEventIdentifier(String eventIdentifierType, String identifier) {
        queryParams.put(String.format(Locale.US, "identifier[%s]", eventIdentifierType), identifier);
        return this;
    }
    public EventsSearchParamsBuilder createdBy(String accountAddress) {
        QueryParamsHelper.addCreatedBy(queryParams, accountAddress);
        return this;
    }

    public EventSearchParams build(){
        return new EventSearchParams(queryParams);
    }

}
