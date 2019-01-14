package com.ambrosus.sdk.model;

import android.support.annotation.NonNull;

import com.ambrosus.sdk.Event;

import java.util.ArrayList;
import java.util.List;

class AmbrosusData {

    private static final String AMBROSUS_OBJECT_TYPE_PREFIX = "ambrosus.asset.";

    static boolean hasAmbosusData(List<String> dataTypes){
        return !getAmbrosusDataTypes(dataTypes).isEmpty();
    }

    @NonNull
    static List<String> getAmbrosusDataTypes(List<String> dataTypes){
        List<String> result = new ArrayList<>();
        for (String dataType : dataTypes) {
            if(dataType.startsWith(AMBROSUS_OBJECT_TYPE_PREFIX))
                result.add(dataType);
        }
        return result;
    }
}
