package com.ambrosus.sdk;

import java.util.List;

class EventsSearchResultAdapter<T extends Event> implements ResponseResultAdapter<SearchResult<Event>, List<T>> {

    private final EventFactory<T> eventFactory;

    EventsSearchResultAdapter(EventFactory<T> eventFactory) {
        this.eventFactory = eventFactory;
    }

    @Override
    public List<T> getResponseResult(SearchResult<Event> result) {
        return eventFactory.processEvents(result.getValues());
    }
}
