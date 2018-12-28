package com.ambrosus.sdk;

import android.support.annotation.NonNull;

public interface AMBNetworkCallback<T> {

    void onSuccess(@NonNull AMBNetworkCall<T> call, @NonNull T result);

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected
     * exception occurred creating the request or processing the response.
     */
    void onFailure(@NonNull AMBNetworkCall<T> call, @NonNull Throwable t);
}
