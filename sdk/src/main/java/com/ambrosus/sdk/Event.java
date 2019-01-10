package com.ambrosus.sdk;

import com.ambrosus.sdk.utils.Assert;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Event {

    protected static final String KEY_TYPE_ATTR = "type";

    private final String systemId;
    private final String assetId;
    private final String createdBy;

    private final long timeStamp;

    private final MetaData metaData;
    private final List<JsonObject> rawData;

    Event(String systemId, String assetId, String createdBy, long timeStamp, MetaData metaData, List<JsonObject> rawData) {
        this.systemId = Assert.assertNotNull(systemId, "systemId == null");
        this.assetId = Assert.assertNotNull(assetId, "assetId == null");
        this.createdBy = Assert.assertNotNull(createdBy, "createdBy == null");
        this.timeStamp = timeStamp;
        this.metaData = Assert.assertNotNull(metaData, "metaData == null");

        ensureRawDataObjectTypes(rawData);

        this.rawData = Collections.unmodifiableList(rawData);
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

    @Override
    public String toString() {
        return super.toString() + String.format(Locale.US, "(%s)", getSystemId());
    }

    private static void ensureRawDataObjectTypes(List<JsonObject> rawData){
        for (JsonObject dataObject : rawData) {
            getDataObjectType(dataObject);
        }
    }

    public static String getDataObjectType(JsonObject dataObject) {
        if(dataObject.has(KEY_TYPE_ATTR))
            return dataObject.get(KEY_TYPE_ATTR).getAsString();
        throw new IllegalArgumentException("Invalid data object: " + dataObject.toString() + " (missing type key)");
    }
}
