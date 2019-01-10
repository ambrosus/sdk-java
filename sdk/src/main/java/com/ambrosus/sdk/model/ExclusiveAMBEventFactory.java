package com.ambrosus.sdk.model;

import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.EventFactory;

import java.util.ArrayList;
import java.util.List;

public class ExclusiveAMBEventFactory implements EventFactory<AMBEvent> {

    @Override
    public List<AMBEvent> processEvents(List<Event> sourceEvents) {
        List<AMBEvent> result = new ArrayList<>(sourceEvents.size());
        for (Event sourceEvent : sourceEvents) {
            if(isValidEvent(sourceEvent)) {
                result.add(new AMBEvent(sourceEvent));
            }
        }
        return result;
    }

    @Override
    public boolean isValidEvent(Event event) {
        return AMBEvent.isValidAMBEvent(event);
    }


}
