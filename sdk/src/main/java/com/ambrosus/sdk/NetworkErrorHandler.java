package com.ambrosus.sdk;


import retrofit2.Response;

public interface NetworkErrorHandler {

    void handleNetworkError(int code, String message) throws Exception;

}
