package com.ambrosus.sdk.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.utils.Assert;
import com.ambrosus.sdk.utils.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.internal.platform.Platform;

public class AMBEvent extends Event {

    private static final String AMBROSUS_EVENT_TYPE_PREFIX = "ambrosus.asset.";

    private static final HashSet<String> AMBROSUS_SERVICE_EVENT_TYPES = new HashSet<String>(){
        {
            add("ambrosus.asset.redirection");
            add("ambrosus.asset.info");
            add("ambrosus.asset.identifiers");
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

    private final Location location;

    public AMBEvent(Event source){
        super(source);

        List<JsonObject> rawData = source.getRawData();

        type = Assert.assertNotNull(getAmbrosusEventType(rawData), IllegalArgumentException.class, "Source event is not valid Ambrosus event.");

        JsonObject mainDataObject = getDataObject(type, rawData);

        name = getEventName(mainDataObject);

        images = getEntityMap(KEY_IMAGES_ATTR, mainDataObject);
        documents = getEntityMap(KEY_DOCUMENTS_ATTR, mainDataObject);
        attributes = getAttributesMap(mainDataObject);

        JsonObject locationDataJson = getDataObject("ambrosus.event.location", rawData);
        location = locationDataJson != null ? Location.createFrom(locationDataJson) : null;
    }

    @NonNull
    public String getType(){
        return type;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public Location getLocation() {
        return location;
    }

    public static boolean isValidAMBEvent(Event event){
        return getAmbrosusEventType(event.getRawData()) != null;
    }

    @Nullable
    private static String getAmbrosusEventType(List<JsonObject> rawData) {
        for (JsonObject dataObject : rawData) {
            String dataObjectType = Event.getDataObjectType(dataObject);
            if(dataObjectType.startsWith(AMBROSUS_EVENT_TYPE_PREFIX)
                    && !AMBROSUS_SERVICE_EVENT_TYPES.contains(dataObjectType))
                return dataObjectType;
        }
        return null;
    }

    @Nullable
    private static JsonObject getDataObject(String type, List<JsonObject> rawData) {
        for (JsonObject dataObject : rawData) {
           if(type.equals(Event.getDataObjectType(dataObject)))
               return dataObject;
        }
        return null;
    }

    @Override
    public String toString() {
        return Strings.defaultToString(this) + String.format(Locale.US, "(id: %s, type: %s, name: %s)", getSystemId(), getType(), getName());
    }

    private static String getEventName(JsonObject dataObject) {
        JsonElement jsonElement = dataObject.get(KEY_NAME_ATTR);
        return jsonElement != null ? jsonElement.getAsString() : null;
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
            Platform.get().log(Platform.WARN, "Can't parse Ambrosus event images", e);
        }
        return Collections.unmodifiableMap(result);
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
