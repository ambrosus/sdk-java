package com.ambrosus.sdk.model;

import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.NetworkResultAdapter;
import com.ambrosus.sdk.SearchResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

class EventAdapter implements NetworkResultAdapter<SearchResult<Event>, List<AMBEvent>> {

    private static final HashSet<String> AMBROSUS_SERVICE_EVENT_TYPES = new HashSet<String>(){
        {
            add("ambrosus.asset.redirection");
            add("ambrosus.asset.info");
            add("ambrosus.asset.identifiers");
        }
    };

    @Override
    public List<AMBEvent> convert(SearchResult<Event> source) {
        List<AMBEvent> result = new ArrayList<>(source.getValues().size());
        for (Event sourceEvent : source.getValues()) {
            if(isValidSourceEvent(sourceEvent)) {
                result.add(new AMBEvent(sourceEvent));
            }
        }
        return result;
    }

    private static boolean isValidSourceEvent(Event event) {
        //limiting output to non-service event
        List<String> dataTypes = event.getDataTypes();
        dataTypes.removeAll(AMBROSUS_SERVICE_EVENT_TYPES);
        //check if we have at least one data object of non-service ambrosus type
        return AmbrosusData.hasAmbosusData(dataTypes);
    }

}
