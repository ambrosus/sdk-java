package com.ambrosus.sdk;

public class MissingEntityErrorHandler implements NetworkErrorHandler {

    @Override
    public void handleNetworkError(int code, String message) throws Exception {
        if(code == 404)
            throw new EntityNotFoundException(code, message);
    }
}
