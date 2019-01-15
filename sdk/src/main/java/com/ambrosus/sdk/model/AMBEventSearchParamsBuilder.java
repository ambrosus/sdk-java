package com.ambrosus.sdk.model;

import android.support.annotation.NonNull;

import com.ambrosus.sdk.Event;
import com.ambrosus.sdk.GenericEventSearchParamsBuilder;

import java.util.Locale;

public class AMBEventSearchParamsBuilder extends GenericEventSearchParamsBuilder<AMBEventSearchParamsBuilder> {

    @NonNull
    public AMBEventSearchParamsBuilder byDataObjectIdentifier(@NonNull Identifier identifier) {
        return byDataObjectIdentifier(identifier.type, identifier.value);
    }

    @NonNull
    public AMBEventSearchParamsBuilder byDataObjectIdentifier(@NonNull String eventIdentifierType, @NonNull String identifier) {
        return byDataObjectField(String.format(Locale.US, "identifiers.%s", eventIdentifierType), identifier);
    }
}
