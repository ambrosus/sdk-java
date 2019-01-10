package com.ambrosus.sdk.utils;

import com.google.gson.JsonObject;

public class GsonUtil {

    public static String getStringValue(JsonObject jsonObject, String key) {
        return jsonObject.has(key) ? jsonObject.get(key).getAsString() : null;
    }

}
