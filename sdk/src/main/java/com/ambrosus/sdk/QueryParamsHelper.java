package com.ambrosus.sdk;

import java.util.Map;

class QueryParamsHelper {

    static void addFrom(Map<String, String> queryParams, long timestamp) {
        queryParams.put("fromTimestamp", Long.toString(timestamp));
    }

    static void addTo(Map<String, String> queryParams, long timestamp) {
        queryParams.put("toTimestamp", Long.toString(timestamp));
    }


}
