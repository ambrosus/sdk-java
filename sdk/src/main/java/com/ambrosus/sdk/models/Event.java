package com.ambrosus.sdk.models;

import com.google.gson.JsonObject;

import java.util.List;

public class Event {

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

    public String getEventId() {
        return eventId;
    }

    public String getAssetId() {
        return content.idData.assetId;
    }

    public List<JsonObject> getData() {
        return content.data;
    }
}


