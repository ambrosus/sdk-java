package com.ambrosus.sdk.model;

import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.EventFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Creates AMBEvent instances using appropriate source Events, limits output to non-service events only
 */
public class AMBEventFactory implements EventFactory<AMBEvent> {

    private static final HashSet<String> AMBROSUS_SERVICE_EVENT_TYPES = new HashSet<String>(){
        {
            add("ambrosus.asset.redirection");
            add("ambrosus.asset.info");
            add("ambrosus.asset.identifiers");
        }
    };

    @Override
    public List<AMBEvent> processEvents(List<Event> sourceEvents) {
        List<AMBEvent> result = new ArrayList<>(sourceEvents.size());
        for (Event sourceEvent : sourceEvents) {
            if(isValidSourceEvent(sourceEvent)) {
                result.add(new AMBEvent(sourceEvent));
            }
        }
        return result;
    }

    @Override
    public boolean isValidSourceEvent(Event event) {
        //limiting output to non-service event
        List<String> dataTypes = event.getDataTypes();
        dataTypes.removeAll(AMBROSUS_SERVICE_EVENT_TYPES);
        //check if we have at least one data object of non-service ambrosus type
        return AmbrosusData.hasAmbosusData(dataTypes);
    }


}
