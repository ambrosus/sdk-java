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

package com.ambrosus.sdk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ambrosus.sdk.utils.Assert;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Event {

    public static final String DATA_OBJECT_ATTR_TYPE = "type";

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

    //we need to be sure about the order of dataTypes in some cases, so result is list
    @NonNull
    public List<String> getDataTypes() {
        List<String> result = new ArrayList<>();
        for (JsonObject dataObject : rawData) {
            result.add(getDataObjectType(dataObject));
        }
        return result;
    }

    @Nullable
    public JsonObject getDataObject(String type) {
        for (JsonObject dataObject : getRawData()) {
            if(type.equals(getDataObjectType(dataObject)))
                return dataObject;
        }
        return null;
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
        if(dataObject.has(DATA_OBJECT_ATTR_TYPE))
            return dataObject.get(DATA_OBJECT_ATTR_TYPE).getAsString();
        throw new IllegalArgumentException("Invalid data object: " + dataObject.toString() + " (missing type key)");
    }
}
