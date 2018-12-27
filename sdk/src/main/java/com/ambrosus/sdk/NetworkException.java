package com.ambrosus.sdk;

import java.lang.Exception;

public class NetworkException extends Exception {

    public final int code;

    public NetworkException(int code, String message) {
        super(message);
        this.code = code;
    }

}
