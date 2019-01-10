package com.ambrosus.sdk;

import android.support.annotation.NonNull;

import java.util.List;

public interface EventFactory<T extends Event> {

    @NonNull
    List<T> processEvents(List<Event> sourceEvents);
    boolean isValidEvent(Event event);

}
