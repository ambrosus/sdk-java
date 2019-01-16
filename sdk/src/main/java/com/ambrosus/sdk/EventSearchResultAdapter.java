package com.ambrosus.sdk;

import java.util.List;
class EventSearchResultAdapter<T extends Event> implements NetworkResultAdapter<SearchResult<Event>, List<T>> {

    private final EventFactory<T> eventFactory;

    EventSearchResultAdapter(EventFactory<T> eventFactory) {
        this.eventFactory = eventFactory;
    }

    @Override
    public List<T> convert(SearchResult<Event> result) {
        return eventFactory.processEvents(result.getValues());
    }
}
