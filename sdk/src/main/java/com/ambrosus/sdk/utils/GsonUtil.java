/*
 * Copyright: Ambrosus Technologies GmbH
 * Email: tech@ambrosus.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ambrosus.sdk.utils;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

abstract public class GsonUtil {

    public static String getStringValue(JsonObject jsonObject, String key) {
        return jsonObject.has(key) ? jsonObject.get(key).getAsString() : null;
    }

    public static String getLexNormalizedJsonStr(@NonNull Object src, Gson gson) {
        Assert.assertNotNull(src, "src == null");
        return getLexNormalizedJson(gson.toJsonTree(src)).toString();
    }

    private static JsonElement getLexNormalizedJson(JsonElement json) {
        if(json.isJsonObject())
            return getLexNormalizedJson(json.getAsJsonObject());
        else if(json.isJsonArray())
            return getLexNormalizedJsonArray(json.getAsJsonArray());
        else
            return json;
    }

    private static JsonArray getLexNormalizedJsonArray(JsonArray jsonArray){
        JsonArray result = new JsonArray();
        for (JsonElement item : jsonArray) {
            result.add(getLexNormalizedJson(item));
        }
        return result;
    }

    private static JsonElement getLexNormalizedJson(JsonObject json) {
        JsonObject result = new JsonObject();
        List<Map.Entry<String, JsonElement>> entriesList = new ArrayList<>(json.entrySet());
        Collections.sort(entriesList, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
        for (Map.Entry<String, JsonElement> entry : entriesList) {
            result.add(entry.getKey(), getLexNormalizedJson(entry.getValue()));
        }
        return result;
    }

    public static List<JsonObject> getAsObjectsList(JsonArray array) {
        ArrayList<JsonObject> result = new ArrayList<>();
        for (JsonElement dataObject : array) {
            result.add(dataObject.getAsJsonObject());
        }
        return result;
    }



}
