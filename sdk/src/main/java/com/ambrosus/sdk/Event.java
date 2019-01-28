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
import com.ambrosus.sdk.utils.GsonUtil;
import com.ambrosus.sdk.utils.Time;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Event {

    public static final String DATA_OBJECT_ATTR_TYPE = "type";

    @SerializedName("eventId")
    private String systemId;
    private EventContent content;
    private MetaData metadata;

    //no-args constructor for GSON
    private Event(){}

    private Event(EventContent content) {
        this.systemId = Network.getObjectHash(content);
        this.content = content;
    }


    protected Event(Event source){
        // it looks like it's more reliable to use copy constructor there instead of this(EventContent)
        this.systemId = source.systemId;
        this.content = source.content;
        this.metadata = source.metadata;
    }

    //TODO rename to getEventId()
    public String getSystemId() {
        return systemId;
    }

    public String getAssetId() {
        return content.getIdData().getAssetId();
    }

    public String getAuthorId() {
        return content.getIdData().getCreatedBy();
    }

    public long getUnixTimeStamp() {
        return content.getIdData().getTimestamp();
    }

    public long getTimeStamp() {
        return Time.getMillis(getUnixTimeStamp());
    }

    public MetaData getMetadata() {
        return metadata;
    }

    public List<JsonObject> getRawData() {
        return content.getData();
    }

    //we need to be sure about the order of dataTypes in some cases, so result is list
    @NonNull
    public List<String> getDataTypes() {
        List<String> result = new ArrayList<>();
        for (JsonObject dataObject : content.getData()) {
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


    public static String getDataObjectType(JsonObject dataObject) {
        if(dataObject.has(DATA_OBJECT_ATTR_TYPE))
            return dataObject.get(DATA_OBJECT_ATTR_TYPE).getAsString();
        throw new IllegalArgumentException("Invalid data object: " + dataObject.toString() + " (missing type key)");
    }

    static class EventIdData extends IdData {

        private String assetId;
        private int accessLevel;
        private String dataHash;

        EventIdData(@NonNull String assetId, @NonNull String createdBy, long timestamp, int accessLevel, String dataHash) {
            super(createdBy, timestamp);
            this.assetId = Assert.assertNotNull(assetId, "assetId == null");
            this.accessLevel = accessLevel;
            this.dataHash = Assert.assertNotNull(dataHash, "dataHash == null");
        }

        public String getAssetId() {
            return assetId;
        }
    }

    static class EventContent extends ContentField {

        private EventIdData idData;
        private List<JsonObject> data;

        static EventContent create(@NonNull String assetId, long timeStamp, int accessLevel, @NonNull JsonArray data, @NonNull String privateKey) {
            EventIdData idData = new EventIdData(
                    assetId,
                    Ethereum.getPublicKey(privateKey),
                    timeStamp, accessLevel,
                    Network.getObjectHash(data)
            );
            EventContent result = create(EventContent.class, idData, privateKey);
            result.idData = idData;
            result.data = Collections.unmodifiableList(GsonUtil.getAsObjectsList(data));
            return result;
        }

        //for tests
        EventIdData getIdData(){
            return idData;
        }

        //for tests
        List<JsonObject> getData() {
            return data;
        }
    }

    public static class Builder {

        private String assetId;

        private int accessLevel;
        private long timeStamp = Time.getUnixTime();

        private JsonArray data = new JsonArray();

        public Builder() {}

        public Builder(String assetId) {
            setAssetId(assetId);
        }

        public Builder setAssetId(@NonNull String assetId) {
            this.assetId = Assert.assertNotNull(assetId, "assetId == null");
            return this;
        }

        public Builder addData(@NonNull String type, JsonObject object) {
            JsonObject dataObject = object.deepCopy();
            dataObject.addProperty(Event.DATA_OBJECT_ATTR_TYPE, type);
            data.add(dataObject);
            return this;
        }

        public Builder setAccessLevel(int accessLevel) {
            this.accessLevel = accessLevel;
            return this;
        }

        public Builder setUnixTime(long unixTime) {
            this.timeStamp = unixTime;
            return this;
        }

        public Builder setTimeStamp(long millis) {
            return setUnixTime(Time.getUnixTime(millis));
        }

        public Event createEvent(@NonNull String privateKey){
            return new Event(
                    EventContent.create(
                            assetId,
                            timeStamp,
                            accessLevel,
                            data,
                            privateKey
                    )
            );
        }
    }

    //for tests
    EventContent getContent() {
        return content;
    }


}
