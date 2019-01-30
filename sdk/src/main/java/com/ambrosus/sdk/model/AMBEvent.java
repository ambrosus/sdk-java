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

package com.ambrosus.sdk.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.RestrictedDataAccessException;
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

    private static final String DATA_OBJECT_ATTR_IMAGES = "images";
    private static final String DATA_OBJECT_ATTR_DOCUMENTS = "documents";
    private static final String DATA_OBJECT_ATTR_NAME = "name";

    private final String type;
    private final String name;

    private final Map<String, JsonObject> images;
    private final Map<String, JsonObject> documents;
    private final Map<String, JsonElement> attributes;

    private final Location location;

    /**
     *
     * @param source - any source event which contains at least one data object of "ambrosus" type (see AmbrosusData implementation)
     */
    public AMBEvent(Event source) throws RestrictedDataAccessException {
        super(source);

        List<String> ambrosusDataTypes = AmbrosusData.getAmbrosusDataTypes(source.getDataTypes());

        Assert.assertTrue(!ambrosusDataTypes.isEmpty(), IllegalArgumentException.class, "Source event is not valid Ambrosus event.");
        type = ambrosusDataTypes.get(0);

        JsonObject mainDataObject = getDataObject(type);

        name = getEventName(mainDataObject);

        images = Collections.unmodifiableMap(getEntityMap(DATA_OBJECT_ATTR_IMAGES, mainDataObject));
        documents = Collections.unmodifiableMap(getEntityMap(DATA_OBJECT_ATTR_DOCUMENTS, mainDataObject));
        attributes = Collections.unmodifiableMap(getAttributesMap(mainDataObject));

        JsonObject locationDataJson = getDataObject("ambrosus.event.location");
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

    public Map<String, JsonObject> getImages() {
        return images;
    }

    public Map<String, JsonObject> getDocuments() {
        return documents;
    }

    public Map<String, JsonElement> getAttributes() {
        return attributes;
    }

    @Nullable
    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return Strings.defaultToString(this) + String.format(Locale.US, "(name: %s, type: %s)", getName() != null ? getName() : getType(), getType());
    }

    private static String getEventName(JsonObject dataObject) {
        JsonElement jsonElement = dataObject.get(DATA_OBJECT_ATTR_NAME);
        return jsonElement != null ? jsonElement.getAsString() : null;
    }

    @NonNull
    //package-local for tests
    static Map<String, JsonObject> getEntityMap(String entityName, JsonObject dataObject){
        Map<String, JsonObject> result = new LinkedHashMap<>();
        try {
            JsonObject entityJson = dataObject.getAsJsonObject(entityName);
            if(entityJson != null) { //if we have this section
                for (String imageKey : entityJson.keySet()) {
                    JsonElement imageAttrsElement = entityJson.get(imageKey);
                    if(imageAttrsElement.isJsonObject()) {
                        result.put(imageKey, imageAttrsElement.getAsJsonObject());
                    }
                }
            }
        } catch(RuntimeException e) {
            Platform.get().log(Platform.WARN, "Can't parse Ambrosus event images", e);
        }
        return result;
    }

    //package-local for tests
    static Map<String, JsonElement> getAttributesMap(JsonObject dataObject){
        HashSet<String> reservedAttrs = new HashSet<>();

        reservedAttrs.add(DATA_OBJECT_ATTR_TYPE);
        reservedAttrs.add(DATA_OBJECT_ATTR_IMAGES);
        reservedAttrs.add(DATA_OBJECT_ATTR_DOCUMENTS);

        Map<String, JsonElement> result = new LinkedHashMap<>();
        for (String key : dataObject.keySet()) {
            if(!reservedAttrs.contains(key))
                result.put(key, dataObject.get(key));
        }

        return result;
    }
}
