package com.ambrosus.sdk;

import android.support.annotation.NonNull;

public interface NetworkCallback<T> {

    void onSuccess(@NonNull NetworkCall<T> call, @NonNull T result);

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected
     * exception occurred creating the request or processing the response.
     */
    void onFailure(@NonNull NetworkCall<T> call, @NonNull Throwable t);
}
