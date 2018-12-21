package com.ambrosus.sdk;

import java.lang.Exception;

public class NetworkError extends Exception {

    public final int code;

    public NetworkError(int code, String message) {
        super("Unexpected HTTP error, code: " + code + (message != null ? " (" + message + ')' : ""));
        this.code = code;
    }
}
