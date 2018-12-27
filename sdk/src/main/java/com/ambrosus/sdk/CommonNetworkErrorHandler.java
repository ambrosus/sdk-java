package com.ambrosus.sdk;

public class CommonNetworkErrorHandler implements NetworkErrorHandler {

    @Override
    public void handleNetworkError(int code, String message) throws Exception {
        throw new NetworkException(
                code,
                "Unexpected HTTP error, code: " + code + (message != null ? " (" + message + ')' : "")
        );
    }
}
