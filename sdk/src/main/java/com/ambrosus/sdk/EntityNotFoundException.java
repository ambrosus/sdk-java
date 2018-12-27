package com.ambrosus.sdk;

public class EntityNotFoundException extends NetworkException {

    public EntityNotFoundException(int code, String message) {
        super(code, message);
    }
}