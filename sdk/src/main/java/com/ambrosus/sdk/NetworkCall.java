package com.ambrosus.sdk;

import android.support.annotation.NonNull;

public interface NetworkCall<T> extends Cloneable {

    @NonNull T execute() throws Throwable;

    /**
     * Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred talking to the server, creating the request, or processing the response.
     */
    void enqueue(@NonNull NetworkCallback<T> callback);

    boolean isExecuted();

    void cancel();

    /** True if {@link #cancel()} was called. */
    boolean isCanceled();

    /**
     * Create a new, identical call to this one which can be enqueued or executed even if this call
     * has already been.
     */
    @NonNull
    NetworkCall<T> clone();
}

