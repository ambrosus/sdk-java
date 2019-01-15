package com.ambrosus.sdk.model;


import com.ambrosus.sdk.Event;
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

    private final Set<Identifier> identifiers;

    /**
     *
     * @param source - source event which contains data objects with DATA_OBJECT_TYPE_ASSET_IDENTIFIERS and DATA_OBJECT_TYPE_ASSET_INFO types
     */
    public AMBAssetInfo(Event source) {
        super(source);
        Assert.assertTrue(
                isValidSourceEvent(source),
                String.format(
                        "Source event has to contain data objects with %s and %s types",
                        DATA_OBJECT_TYPE_ASSET_INFO,
                        DATA_OBJECT_TYPE_ASSET_IDENTIFIERS)
        );
        identifiers = Collections.unmodifiableSet(extractIdentifiers());
    }

    public Set<Identifier> getIdentifiers() {
        return identifiers;
    }

    public Map<String, List<String>> getIdentifiersMap() {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (Identifier identifier : identifiers) {
            List<String> listOfType = result.get(identifier.type);
            if(listOfType == null) {
                listOfType = new ArrayList<>();
                result.put(identifier.type, listOfType);
            }
            listOfType.add(identifier.value);
        }
        return result;
    }

    private Set<Identifier> extractIdentifiers(){
        Set<Identifier> result = new LinkedHashSet<>();
        JsonObject identifiersDataObject = getIdentifiersData();
        JsonObject identifiersJson = identifiersDataObject.getAsJsonObject("identifiers");
        for (String identifierType : identifiersJson.keySet()) {
            JsonArray identifiers = identifiersJson.getAsJsonArray(identifierType);
            for (JsonElement identifier : identifiers) {
                result.add(new Identifier(identifierType, identifier.getAsString()));
            }
        }
        return result;
    }

    private JsonObject getIdentifiersData() {
        return getDataObject(DATA_OBJECT_TYPE_ASSET_IDENTIFIERS);
    }

    static boolean isValidSourceEvent(Event event){
        List<String> sourceDataTypes = event.getDataTypes();
        return sourceDataTypes.contains(DATA_OBJECT_TYPE_ASSET_INFO) && sourceDataTypes.contains(DATA_OBJECT_TYPE_ASSET_IDENTIFIERS);
    }
}

