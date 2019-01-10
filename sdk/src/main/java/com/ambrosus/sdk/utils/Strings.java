package com.ambrosus.sdk.utils;

public class Strings {

    public static String defaultToString(Object o) {
        return o.getClass().getName() + "@" +
                Integer.toHexString(System.identityHashCode(o));
    }


}
