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
import com.ambrosus.sdk.utils.UnixTime;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *  This class is designed to represent an Event.
 *  <p>
 *  Events describe all registered changes of state that occurred with the {@link Asset}.
 *  E.g. measured temperature, noted big acceleration or changing pallets.
 *  <p>
 *  Every event contains an array of {@linkplain JsonObject} with information what actually happened.
 *  Each item in this array is an object of a some type identified by a string constant.
 *  You can get types of all available data objects with {@link #getDataTypes()} method.
 *  Use {@link #getDataObject(String)} method to retrieve an object of certain type.
 *  <p>
 *  It's also possible to get all available data objects with {@link #getUserData()} method
 */
public class Event extends Entity {

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


    /**
     * This constructor makes it possible to extend Event class in order to implement your own Event data model
     * @param source
     */
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

    /**
     *
     * @return Id of an asset to which this event is connected.
     */
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

    /**
     * @return access level of the event. It's not possible to get event data with {@link #getUserData()}, {@link #getDataTypes()}, {@link #getDataObject(String)} methods if access level of your account is lower than event's or if you are not {@linkplain Network#authorize(AuthToken) authenticated} as a holder of the account which was used to create this event
     */
    public int getAccessLevel() {
        return content.idData.accessLevel;
    }

    public MetaData getMetadata() {
        return metadata;
    }

    /**
     * Returns a list of {@link JsonObject} with information what actually happened.
     * You can get this data for all events which {@linkplain #getAccessLevel() access level} belongs to range  of [0; {@linkplain Account#getAccessLevel() your account accessLevel}] if you are authenticated as a holder of {@linkplain #getAccountAddress() account} which was used to create this event or a holder of one of it's child accounts
     * If you are not authenticated on the network with {@link Network#authorize(AuthToken)} method you can get only data for events which
     * access level is set to 0.
     *
     * @return a list of {@linkplain JsonObject JsonObjects} associated with this event
     * @throws RestrictedDataAccessException in the following cases:
     *
     * <ul>
     * <li>if you are not {@linkplain Network#authorize(AuthToken) authenticated} and this event has {@linkplain #getAccessLevel() accessLevel} greater than 0
     * <li>if you are not {@linkplain Network#authorize(AuthToken) authenticated} as a holder of {@linkplain #getAccountAddress() account} which was used to create this event or a holder of one of it's child accounts
     * <li>if {@linkplain Account#getAccessLevel() access level} of your account is less than {@linkplain #getAccessLevel() access level} of this event
     * </ul>
     */
    public List<JsonObject> getUserData() throws RestrictedDataAccessException {
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



    /**
     * Returns list containing types for all data objects which you can get with {@link #getUserData()} method.
     * I.e: <p>
     * <code>
     *     [getDataObjectType(getUserData().get(0)),
     *        ... ,
     *      getDataObjectType(getUserData().get(getUserData().size()-1)]
     * </code>
     *
     * @return list of {@linkplain #getUserData() all data objects} types
     * @throws RestrictedDataAccessException under the same conditions as {@link #getUserData()} method
     * @see #getDataObjectType(JsonObject)
     * @see #getUserData()
     *
     */
    @NonNull
    //we need to be sure about the order of dataTypes in some cases (i.e. for AMBEventImplementation), so result is list
    public List<String> getDataTypes() throws RestrictedDataAccessException {
        List<String> result = new ArrayList<>();
        for (JsonObject dataObject : getUserData()) {
            result.add(getDataObjectType(dataObject));
        }
        return result;
    }


    /**
     * Searches for a data object of specified <code>type</code> in the {@linkplain #getUserData() user data list}
     * @return data object of specified type from {@link #getUserData() user data list} or null if this event doesn't contain an object of this type
     * @throws RestrictedDataAccessException under the same conditions as {@link #getUserData()} method
     * @see #getUserData()
     * @see #getDataObjectType(JsonObject)
     */
    @Nullable
    public JsonObject getDataObject(String type) throws RestrictedDataAccessException {
        for (JsonObject dataObject : getUserData()) {
            if(type.equals(getDataObjectType(dataObject)))
                return dataObject.deepCopy();
        }
        return null;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(Locale.US, "(%s)", getSystemId());
    }

    /**
     * Returns type of the provided <code>dataObject</code>
     * @throws IllegalArgumentException when it's not possible to determine <code>dataObject</code> type
     * because it's missing <code>type</code> field or this field is not a valid string field
     */
    //TODO move these methods to separate DataObject class
    public static String getDataObjectType(JsonObject dataObject) throws IllegalArgumentException {
        String type = getDataObjectTypeOrNull(dataObject);
        Assert.assertNotNull(type, IllegalArgumentException.class, "Invalid data object: " + dataObject.toString() + " (missing \"type\" field)");
        return type;
    }

    @Nullable
    private static String getDataObjectTypeOrNull(JsonObject dataObject) throws IllegalArgumentException {
        JsonElement typeField = dataObject.get(DATA_OBJECT_ATTR_TYPE);
        if(typeField != null) {
            Assert.assertTrue(
                    typeField.isJsonPrimitive() && typeField.getAsJsonPrimitive().isString(),
                    IllegalArgumentException.class,
                    "Type field must contain a valid string value, but has: " + typeField.toString()
            );
            return typeField.getAsString();
        }
        return null;
    }

    private static JsonObject setDataObjectType(JsonObject dataObject, String type) {
        dataObject.addProperty(Event.DATA_OBJECT_ATTR_TYPE, type);
        return dataObject;
    }

    static class EventIdData extends CreationData {

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

        @Expose(deserialize = true)
        private transient List<JsonObject> data;

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
            this.data = Collections.unmodifiableList(Json.getAsObjectsList(data));
        }

        //for tests
        EventIdData getIdData(){
            return idData;
        }

        //for tests
        List<JsonObject> getData() {
            return data;
        }

        private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
            stream.defaultReadObject();
            data = Json.fromJson(stream.readUTF(), new TypeToken<List<JsonObject>>() {}.getType());
        }

        private void writeObject(ObjectOutputStream stream) throws IOException {
            stream.defaultWriteObject();
            stream.writeUTF(Json.toJson(data));
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
        public Builder addData(@NonNull String type, @NonNull JsonObject dataObject) throws IllegalArgumentException {
            Assert.assertNotNull(type, "Type argument can't be null");

            String dataObjectType = getDataObjectTypeOrNull(dataObject);
            Assert.assertTrue(
                    dataObjectType == null || type.equals(dataObjectType),
                    IllegalArgumentException.class,
                    "dataObject contains type field which value doesn't equal to type argument"
            );

            data.put(type, setDataObjectType(dataObject.deepCopy(), type));
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

        public Builder setUnixTimeStamp(long unixTime) {
            this.timeStamp = unixTime;
            return this;
        }

        /**
         * TimeStamp precision is limited to seconds, milliseconds value will be truncated. {@link Event#getTimestamp()}
         * will return just a date value with the same amount of seconds as original {@code date} plus 0 milliseconds
         *
         * @param date
         */
        public Builder setTimestamp(@NonNull Date date) {
            return setUnixTimeStamp(UnixTime.get(date));
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
