package com.ambrosus.sdk.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Assert {

    public static void assertTrue(boolean condition, @Nullable String message) throws IllegalStateException {
        assertTrue(condition, IllegalStateException.class, message);
    }

    public static <E extends Exception> void assertTrue(boolean condition, @NonNull Class<E> exceptionType, @Nullable String message) throws E {
        if(!condition) {
            E exceptionToThrow;
            try {
                exceptionToThrow = exceptionType.getConstructor(String.class).newInstance(message);
            } catch (Exception e) {
                throw new RuntimeException("Can't create exception with message: " + message + " to throw when value is null", e);
            }
            throw exceptionToThrow;
        }
    }

    @NonNull
    public static <T> T assertNotNull(@Nullable T value, @Nullable String message) throws NullPointerException {
        return assertNotNull(value, NullPointerException.class, message);
    }

    @NonNull
    public static <T, E extends Exception> T assertNotNull(@Nullable T value, @NonNull Class<E> exceptionType, @Nullable String message) throws E {
        assertTrue(value != null, exceptionType, message);
        //noinspection ConstantConditions
        return value;
    }


}
