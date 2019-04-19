package com.ambrosus.sdk;

import android.support.annotation.NonNull;

import com.ambrosus.sdk.utils.Assert;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;

import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class Json {

    static final Gson GSON;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
                    Number resultNumber;
                    resultNumber = Math.ceil(src) == Math.floor(src) ? new PlainLong(src.longValue()) : src;
                    return new JsonPrimitive(resultNumber);
                })
                .addDeserializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.hasModifier(Modifier.TRANSIENT) && (
                                f.getAnnotation(Expose.class) == null || !f.getAnnotation(Expose.class).deserialize());
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.hasModifier(Modifier.TRANSIENT) && (
                                f.getAnnotation(Expose.class) == null || !f.getAnnotation(Expose.class).serialize());
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .excludeFieldsWithModifiers(Modifier.STATIC);
        GSON = gsonBuilder.create();
    }

    static String getLexNormalizedJsonStr(@NonNull Object src) {
        Assert.assertNotNull(src, "src == null");
        return getLexNormalizedJson(GSON.toJsonTree(src)).toString();
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

    static List<JsonObject> getAsObjectsList(JsonArray array) {
        ArrayList<JsonObject> result = new ArrayList<>();
        for (JsonElement dataObject : array) {
            result.add(dataObject.getAsJsonObject());
        }
        return result;
    }

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static <T> T fromJson(String json, Type type){
        return GSON.fromJson(json, type);
    }

    public static <T> T fromJson(InputStreamReader in, Type type){
        return GSON.fromJson(in, type);
    }

    private static class PlainLong extends Number {

        private final Long value;

        private PlainLong(Long value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value.toString();
        }

        @Override
        public int intValue() {
            return value.intValue();
        }

        @Override
        public long longValue() {
            return value;
        }

        @Override
        public float floatValue() {
            return value.floatValue();
        }

        @Override
        public double doubleValue() {
            return value.doubleValue();
        }
    }

}
