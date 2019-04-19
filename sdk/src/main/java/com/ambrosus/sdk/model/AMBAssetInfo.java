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

package com.ambrosus.sdk.model;


import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.RestrictedDataAccessException;
import com.ambrosus.sdk.utils.Assert;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AMBAssetInfo extends AMBEvent {

    static final String DATA_OBJECT_TYPE_ASSET_INFO = "ambrosus.asset.info";

    private static final String DATA_OBJECT_TYPE_ASSET_IDENTIFIERS = "ambrosus.asset.identifiers";

    /**
     *
     * @param source - source event which contains data objects with DATA_OBJECT_TYPE_ASSET_IDENTIFIERS and DATA_OBJECT_TYPE_ASSET_INFO types
     */
    public AMBAssetInfo(Event source) throws RestrictedDataAccessException {
        super(source);
        Assert.assertTrue(
                isValidSourceEvent(source),
                String.format(
                        "Source event has to contain data objects with %s and %s types",
                        DATA_OBJECT_TYPE_ASSET_INFO,
                        DATA_OBJECT_TYPE_ASSET_IDENTIFIERS)
        );
    }

    public Set<Identifier> getIdentifiers() {
        Set<Identifier> result = new LinkedHashSet<>();
        JsonObject identifiersDataObject = getIdentifiersData();
        JsonObject identifiersJson = identifiersDataObject.getAsJsonObject("identifiers");
        for (String identifierType : identifiersJson.keySet()) {
            JsonElement identifiersItem = identifiersJson.get(identifierType);
            if(identifiersItem.isJsonArray()) {
                JsonArray identifiers = identifiersJson.getAsJsonArray(identifierType);
                for (JsonElement identifier : identifiers) {
                    result.add(new Identifier(identifierType, identifier.getAsString()));
                }
            } else {
                result.add(new Identifier(identifierType, identifiersItem.getAsString()));
            }
        }
        return result;
    }

    public Map<String, List<String>> getIdentifiersMap() {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (Identifier identifier : getIdentifiers()) {
            List<String> listOfType = result.get(identifier.type);
            if(listOfType == null) {
                listOfType = new ArrayList<>();
                result.put(identifier.type, listOfType);
            }
            listOfType.add(identifier.value);
        }
        return result;
    }

    private JsonObject getIdentifiersData() {
        try {
            return getDataObject(DATA_OBJECT_TYPE_ASSET_IDENTIFIERS);
        } catch (RestrictedDataAccessException e) {
            //this should never happen
            throw new IllegalStateException();
        }
    }

    static boolean isValidSourceEvent(Event event) throws RestrictedDataAccessException {
        List<String> sourceDataTypes = event.getDataTypes();
        return sourceDataTypes.contains(DATA_OBJECT_TYPE_ASSET_INFO) && sourceDataTypes.contains(DATA_OBJECT_TYPE_ASSET_IDENTIFIERS);
    }
}

