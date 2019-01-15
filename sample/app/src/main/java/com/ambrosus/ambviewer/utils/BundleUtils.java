package com.ambrosus.ambviewer.utils;

import android.os.Bundle;

public class BundleUtils {

    private static final String TAG = BundleUtils.class.getName();

    public static <T> T extractBundleValue(Bundle args, String key, Class<T> clazz){
        T result = extractBundleValueIfExists(args, key, clazz);

        if(result == null)
            throwIncorrectBundleDataException(key, clazz);

        return result;
    }

    /**
     * Be careful with this methods, it returns null value in case search key not exists in bundle,
     * which can lead to NPE when autoBoxing, e.g:
     *
     * long t = extractBundleValueIfExists(args, "key", Long.class, this);
     *
     * will lead to NPE if value with key "key" doesn't exists in bundle
     *
     * @param args
     * @param key
     * @param clazz
     * @return
     */
    public static <T> T extractBundleValueIfExists(Bundle args, String key, Class<T> clazz){
        if(args != null){
            Object value = args.get(key);
            if(value != null) {
                if(clazz.isInstance(value))
                    return (T) value;
                else throwIncorrectBundleDataException(key, clazz);
            }
        }
        return null;
    }

    public static <T> T extractBundleValueIfExists(Bundle args, String key, Class<T> clazz, T defaultValue){
        if(args != null){
            Object value = args.get(key);
            if(value != null) {
                if(clazz.isInstance(value))
                    return (T) value;
                else throwIncorrectBundleDataException(key, clazz);
            }
        }
        return defaultValue;
    }

    private static void throwIncorrectBundleDataException(String key, @SuppressWarnings("rawtypes") Class clazz){
        throw new IllegalArgumentException("You must pass " + clazz.getSimpleName() + " value with key: " + key);
    }
}
