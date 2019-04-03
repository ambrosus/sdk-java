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

package com.ambrosus.sdk;

import android.support.annotation.NonNull;

/**
 * NetworkCall interface represents a network request which can be executed {@linkplain #execute() synchronously}
 * or {@linkplain #enqueue(NetworkCallback) asynchronously}.
 * It provides the same behaviour as <a href="https://square.github.io/retrofit/2.x/retrofit/retrofit2/Call.html">Call</a> interface from Retrofit library
 *
 * @param <T> - type of the data which would be returned in the case of successful excectuion of this request
 */
public interface NetworkCall<T> extends Cloneable {

    /**
     * Synchronously executes request and return its response.
     * <p>
     * If you want to execute this request asynchronously use {@link #enqueue(NetworkCallback)} method instead of this one.
     *
     * @throws java.io.IOException if a connection problem occurred while communicating with the network
     * @throws AmbrosusException in case of some client-side error
     * @throws RuntimeException (and subclasses) which signal about an issue with SDK or Backend implementation
     * @see #enqueue(NetworkCallback)
     */
    @NonNull T execute() throws Throwable;

    /**
     * Asynchronously executes the request and notifies {@code callback} of its response or about an error
     * which have happened during execution or processing the response.
     * <p>
     * If you want to execute this request synchronously use {@link #execute()} method instead this one.
     */
    void enqueue(@NonNull NetworkCallback<T> callback);

    /**
     * Returns true if this call has been either {@linkplain #execute() executed} or {@linkplain
     * #enqueue(NetworkCallback) enqueued}. It is an error to execute or enqueue a call more than once.
     */
    boolean isExecuted();

    /**
     * Cancel this call. An attempt will be made to cancel in-flight calls, and if the call has not
     * yet been executed it never will be.
     */
    void cancel();

    /** True if {@link #cancel()} was called. */
    boolean isCanceled();

    /**
     * Create a new, identical call to this one which can be enqueued or executed even if this call
     * has already been.
     */
    @NonNull
    NetworkCall<T> clone();
}

