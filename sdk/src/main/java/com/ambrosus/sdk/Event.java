package com.ambrosus.sdk;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Event implements Serializable {

    private String eventId;

    private Content content;

    static class Content {
        String signature;
        private IdData idData;
        private List<JsonObject> data;

        static class IdData {

            private String assetId;
            private String createdBy;
            private long accessLevel;
            private long timestamp;

        }
    }

    public String getSystemId() {
        return eventId;
    }

    public List<Identifier> getAssetIdentifiers() {
        List<Identifier> result = new ArrayList<>();
        for (JsonObject dataObject : getData()) {
            if(Identifier.TYPE_ASSET.equals(getDataType(dataObject))) {
                result.addAll(getIdentifiersFrom(dataObject));
            }
        }
        return result;
    }

    public String getAssetId() {
        return content.idData.assetId;
    }

    public List<JsonObject> getData() {
        return content.data;
    }

    private static String getDataType(JsonObject dataObject) {
        return dataObject.get("type").getAsString();
    }

    private static List<Identifier> getIdentifiersFrom(JsonObject data) {
        List<Identifier> result = new ArrayList<>();
        JsonObject identifiers = data.get("identifiers").getAsJsonObject();
        if (identifiers != null) {
            for (Map.Entry<String, JsonElement> identifier : identifiers.entrySet()) {
                if(identifier.getValue().isJsonArray()) {
                    for (JsonElement identifierValue: identifier.getValue().getAsJsonArray()) {
                        result.add(new Identifier(identifier.getKey(), identifierValue.getAsString()));
                    }
                } else {
                   result.add(new Identifier(identifier.getKey(), identifier.getValue().getAsString()));
                }
            }
        }
        return result;
    }
}


