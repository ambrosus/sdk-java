package com.ambrosus.sdk;

import java.util.Map;

class SearchParams {

    final Map<String, String> queryParams;

    SearchParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }
}
