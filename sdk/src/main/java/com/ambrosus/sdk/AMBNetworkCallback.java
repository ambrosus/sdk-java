package com.ambrosus.sdk;

public interface AMBNetworkCallback<T> {

    void onSuccess(AMBNetworkCall<T> call, T result);

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected
     * exception occurred creating the request or processing the response.
     */
    void onFailure(AMBNetworkCall<T> call, Throwable t);
}
