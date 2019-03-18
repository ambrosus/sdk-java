/*
 * Copyright: Ambrosus Inc.
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
import com.ambrosus.sdk.utils.UnixTime;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Event extends Entity{

    public static final String DATA_OBJECT_ATTR_TYPE = "type";

    private String eventId;
    private EventContent content;
    private MetaData metadata;

    //no-args constructor for GSON
    private Event(){}

    private Event(EventContent content) {
        this.eventId = Network.getObjectHash(content);
        this.content = content;
    }


    protected Event(Event source){
        // it looks like it's more reliable to use copy constructor there instead of this(EventContent)
        this.eventId = source.eventId;
        this.content = source.content;
        this.metadata = source.metadata;
    }

    @NonNull
    @Override
    final public String getSystemId() {
        return eventId;
    }

    public String getAssetId() {
        return content.getIdData().getAssetId();
    }

    @NonNull
    @Override
    public String getAccountAddress() {
        return content.getIdData().getAccountAddress();
    }

    @NonNull
    @Override
    final public Date getTimestamp() {
        return content.getIdData().getTimestamp();
    }

    public int getAccessLevel() {
        return content.idData.accessLevel;
    }

    public MetaData getMetadata() {
        return metadata;
    }

    public List<JsonObject> getRawData() throws RestrictedDataAccessException {
        Assert.assertNotNull(
                content.getData(),
                RestrictedDataAccessException.class,
                String.format(
                        Locale.US,
                        "You have to be authorized as %s (or one of its child accounts) and have account access level greater or equal to %d",
                        getAccountAddress(),
                        getAccessLevel()
                )
        );
        return content.getData();
    }

    //we need to be sure about the order of dataTypes in some cases, so result is list
    @NonNull
    public List<String> getDataTypes() throws RestrictedDataAccessException {
        List<String> result = new ArrayList<>();
        for (JsonObject dataObject : getRawData()) {
            result.add(getDataObjectType(dataObject));
        }
        return result;
    }

    @Nullable
    public JsonObject getDataObject(String type) throws RestrictedDataAccessException {
        for (JsonObject dataObject : getRawData()) {
            if(type.equals(getDataObjectType(dataObject)))
                return dataObject.deepCopy();
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

    public static class EventIdData extends CreationData {

        private String assetId;
        private int accessLevel;
        private String dataHash;

        //no-args constructor for GSON
        private EventIdData(){}

        private EventIdData(@NonNull String assetId, @NonNull String createdBy, long timestamp, int accessLevel, JsonArray data) {
            super(createdBy, timestamp);
            this.assetId = Assert.assertNotNull(assetId, "assetId == null");
            this.accessLevel = accessLevel;
            this.dataHash = Network.getObjectHash(data);
        }

        String getAssetId() {
            return assetId;
        }

        int getAccessLevel() {
            return accessLevel;
        }
    }

    static class EventContent extends SignedContent<EventIdData> {

        private List<JsonObject> data;

        //no-args constructor for GSON
        private EventContent(){}

        private EventContent(String assetId, long timestamp, int accessLevel, JsonArray data, String privateKey) {
            super(
                    new EventIdData(
                            assetId,
                            Ethereum.getAddress(privateKey),
                            timestamp,
                            accessLevel,
                            data
                    ),
                    privateKey
            );
            Assert.assertTrue(data.size() > 0, IllegalStateException.class, "You have to add at least 1 data object to build a valid Event");
            this.data = Collections.unmodifiableList(GsonUtil.getAsObjectsList(data));
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
        private long timeStamp = UnixTime.get();

        private Map<String, JsonObject> data = new HashMap<>();

        public Builder(@NonNull String assetId) {
            setAssetId(assetId);
        }

        //TODO is it possible to create events for asset which was created by another account?
        public Builder setAssetId(@NonNull String assetId) {
            this.assetId = Assert.assertNotNull(assetId, "assetId == null");
            return this;
        }

        //TODO we have to mention in java-doc that event can't contain several data objects of the same type
        //TODO we have to cover this behaviour with API integration test
        public Builder addData(@NonNull String type, @NonNull JsonObject object) {
            Assert.assertNotNull(type, "Type argument can't be null");
            JsonObject dataObject = object.deepCopy();
            dataObject.addProperty(Event.DATA_OBJECT_ATTR_TYPE, type);
            data.put(type, dataObject);
            return this;
        }

        public Builder clearData() {
            data.clear();
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

        /**
         * TimeStamp precision is limited to seconds, milliseconds value will be truncated. {@link Event#getTimestamp()}
         * will return just a date value with the same amount of seconds as original {@code date} plus 0 milliseconds
         *
         * @param date
         */
        public Builder setTimeStamp(@NonNull Date date) {
            return setUnixTime(UnixTime.get(date));
        }

        public Event createEvent(@NonNull String privateKey){
            return new Event(new EventContent(assetId,  timeStamp, accessLevel, getDataAsArray(), privateKey));
        }

        private JsonArray getDataAsArray(){
            JsonArray result = new JsonArray();
            for (JsonObject dataObject : data.values()) {
                result.add(dataObject);
            }
            return result;
        }
    }

    //for tests
    EventContent getContent() {
        return content;
    }

}
