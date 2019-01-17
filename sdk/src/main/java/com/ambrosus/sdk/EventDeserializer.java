package com.ambrosus.sdk;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class EventDeserializer implements JsonDeserializer<Event> {

    @Override
    public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        try {
            JsonObject eventJson = json.getAsJsonObject();
            JsonObject eventContentJson = eventJson.getAsJsonObject("content");
            JsonObject idDataJson = eventContentJson.getAsJsonObject("idData");

            JsonArray rawDataJsonArray = eventContentJson.getAsJsonArray("data");
            List<JsonObject> rawData = new ArrayList<>(rawDataJsonArray != null ? rawDataJsonArray.size() : 0);
            if(rawDataJsonArray != null)
                for (JsonElement jsonElement : rawDataJsonArray) {
                    rawData.add(jsonElement.getAsJsonObject());
                }

            MetaData metaData = context.deserialize(eventJson.get("metadata"), MetaData.class);
            return new Event(
                    eventJson.get("eventId").getAsString(),
                    idDataJson.get("assetId").getAsString(),
                    idDataJson.get("createdBy").getAsString(),
                    idDataJson.get("timestamp").getAsLong()*1000,
                    metaData,
                    rawData
            );

        } catch(RuntimeException e) {
            throw new JsonParseException("Can't deserialize event", e);
        }
    }
}
