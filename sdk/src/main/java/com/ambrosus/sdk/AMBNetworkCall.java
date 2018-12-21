package com.ambrosus.sdk;

public interface AMBNetworkCall<T> extends Cloneable {

    T execute() throws Throwable;

    /**
     * Asynchronously send the request and notify {@code callback} of its response or if an error
     * occurred talking to the server, creating the request, or processing the response.
     */
    void enqueue(AMBNetworkCallback<T> callback);

    boolean isExecuted();

    void cancel();

    /** True if {@link #cancel()} was called. */
    boolean isCanceled();

    /**
     * Create a new, identical call to this one which can be enqueued or executed even if this call
     * has already been.
     */
    AMBNetworkCall<T> clone();
}

