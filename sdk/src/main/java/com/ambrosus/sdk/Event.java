package com.ambrosus.sdk;

import com.google.gson.JsonObject;

import java.util.List;

public class Event {

    private final String systemId;
    private final String assetId;
    private final String createdBy;

    private final long timeStamp;

    private final MetaData metaData;
    private final List<JsonObject> rawData;

    Event(String systemId, String assetId, String createdBy, long timeStamp, MetaData metaData, List<JsonObject> rawData) {
        this.systemId = systemId;
        this.assetId = assetId;
        this.createdBy = createdBy;
        this.timeStamp = timeStamp;
        this.metaData = metaData;
        this.rawData = rawData;
    }

    protected Event(Event source){
        this(source.systemId, source.assetId, source.createdBy, source.timeStamp, source.metaData, source.rawData);
    }

    public String getSystemId() {
        return systemId;
    }

    public String getAssetId() {
        return assetId;
    }

    public String getAuthorId() {
        return createdBy;
    }

    public long getGMTTimeStamp() {
        return timeStamp;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public List<JsonObject> getRawData() {
        return rawData;
    }
}
