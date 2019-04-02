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
 * This interface provides you with results of asynchronous execution of a {@link NetworkCall}
 * One and only one method will be invoked when execution of a {@link NetworkCall} will be finished or failed
 * On Android, callback methods will be invoked on the main thread.
 * On the JVM, callbacks will happen on a thread responsible for network communication.
 *
 * @param <T> type of the data which would be passed to {@link #onSuccess(NetworkCall, Object)} method
 */

//TODO Retrofit Callbacks methods are executed using the {@link Retrofit} callback executor. It would be nice to provide an alternative inside Network configuration
public interface NetworkCallback<T> {

    /**
     * Invoked in case of successful asynchronous execution of corresponding {@link NetworkCall}
     */
    void onSuccess(@NonNull NetworkCall<T> call, @NonNull T result);

    /**
     * Invoked when some exception occurred while performing corresponding {@link NetworkCall}
     * You can get the same types of exception as when performing {@link NetworkCall#execute()} method
     */
    void onFailure(@NonNull NetworkCall<T> call, @NonNull Throwable error);
}
