package com.ambrosus.sdk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ambrosus.sdk.utils.Assert;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.internal.platform.Platform;

public class AMBEvent extends Event {

    private static final String AMBROSUS_EVENT_TYPE_PREFIX = "ambrosus.asset.";

    private static final HashSet<String> AMBROSUS_SERVICE_EVENT_TYPES = new HashSet<String>(){
        {
            add("ambrosus.asset.redirection");
            add("ambrosus.asset.info");
        }
    };
    private static final String KEY_IMAGES_ATTR = "images";
    private static final String KEY_DOCUMENTS_ATTR = "documents";
    private static final String KEY_NAME_ATTR = "name";

    private final String type;
    private final String name;

    private final Map<String, JsonObject> images;
    private final Map<String, JsonObject> documents;
    private final Map<String, JsonElement> attributes;

    public AMBEvent(Event source){
        super(source);

        JsonObject mainDataObject = Assert.assertNotNull(getMainDataObject(getRawData()), IllegalArgumentException.class, "Source event is not valid Ambrosus event.");

        type = Event.getDataObjectType(mainDataObject);
        name = getEventName(mainDataObject);

        images = getEntityMap(KEY_IMAGES_ATTR, mainDataObject);
        documents = getEntityMap(KEY_DOCUMENTS_ATTR, mainDataObject);
        attributes = getAttributesMap(mainDataObject);
    }

    @NonNull
    public String getType(){
        return type;
    }

    public static boolean isValidAMBEvent(Event event){
        return getMainDataObject(event.getRawData()) != null;
    }

    private static boolean isMainDataObject(JsonObject dataObject) {
        String dataObjectType = Event.getDataObjectType(dataObject);
        return dataObjectType.startsWith(AMBROSUS_EVENT_TYPE_PREFIX)
                && !AMBROSUS_SERVICE_EVENT_TYPES.contains(dataObjectType);
    }

    @Nullable
    private static JsonObject getMainDataObject(List<JsonObject> rawData) {
        for (JsonObject dataObject : rawData) {
           if(isMainDataObject(dataObject))
               return dataObject;
        }
        return null;
    }

    @NonNull
    //package-local for tests
    static Map<String, JsonObject> getEntityMap(String entityName, JsonObject dataObject){
        Map<String, JsonObject> result = new LinkedHashMap<>();
        try {
            JsonObject imagesJson = dataObject.getAsJsonObject(entityName);
            for (String imageKey : imagesJson.keySet()) {
                JsonElement imageAttrsElement = imagesJson.get(imageKey);
                if(imageAttrsElement.isJsonObject()) {
                      result.put(imageKey, imageAttrsElement.getAsJsonObject());
                }
            }
        } catch(RuntimeException e) {
            Platform.get().log(Platform.WARN, "Can't parse ambrosus event images", e);
        }
        return Collections.unmodifiableMap(result);
    }

    private static String getEventName(JsonObject dataObject) {
        JsonElement jsonElement = dataObject.get(KEY_NAME_ATTR);
        return jsonElement != null ? jsonElement.getAsString() : null;
    }

    //package-local for tests
    static Map<String, JsonElement> getAttributesMap(JsonObject dataObject){
        HashSet<String> reservedAttrs = new HashSet<>();

        reservedAttrs.add(KEY_TYPE_ATTR);
        reservedAttrs.add(KEY_NAME_ATTR);
        reservedAttrs.add(KEY_IMAGES_ATTR);
        reservedAttrs.add(KEY_DOCUMENTS_ATTR);

        Map<String, JsonElement> result = new LinkedHashMap<>();
        for (String key : dataObject.keySet()) {
            if(!reservedAttrs.contains(key))
                result.put(key, dataObject.get(key));
        }

        return Collections.unmodifiableMap(result);
    }
}
