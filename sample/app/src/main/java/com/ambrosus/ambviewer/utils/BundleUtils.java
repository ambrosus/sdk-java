/*
 * Copyright: Ambrosus Technologies GmbH
 * Email: tech@ambrosus.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
