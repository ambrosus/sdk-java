/*
 * Copyright: Ambrosus Inc.
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
