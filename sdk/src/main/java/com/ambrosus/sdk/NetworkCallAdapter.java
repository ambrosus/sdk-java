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

public class NetworkCallAdapter<I, O> implements NetworkCall<O> {

    private final NetworkCall<I> networkCall;
    private final DataConverter<I, O> resultAdapter;

    public NetworkCallAdapter(NetworkCall<I> networkCall, DataConverter<I, O> resultAdapter) {
        this.networkCall = networkCall;
        this.resultAdapter = resultAdapter;
    }

    @NonNull
    @Override
    public O execute() throws Throwable {
        return resultAdapter.convert(networkCall.execute());
    }

    @Override
    public void enqueue(@NonNull final NetworkCallback<O> callback) {
        networkCall.enqueue(new NetworkCallback<I>() {
            @Override
            public void onSuccess(@NonNull NetworkCall<I> call, @NonNull I result) {
                try {
                    callback.onSuccess(NetworkCallAdapter.this, resultAdapter.convert(result));
                } catch (Throwable throwable) {
                    callback.onFailure(NetworkCallAdapter.this, throwable);
                }
            }

            @Override
            public void onFailure(@NonNull NetworkCall<I> call, @NonNull Throwable t) {
                callback.onFailure(NetworkCallAdapter.this, t);
            }
        });
    }

    @Override
    public boolean isExecuted() {
        return networkCall.isExecuted();
    }

    @Override
    public void cancel() {
        networkCall.cancel();
    }

    @Override
    public boolean isCanceled() {
        return networkCall.isCanceled();
    }

    @NonNull
    @Override
    public NetworkCall<O> clone() {
        return new NetworkCallAdapter<>(networkCall.clone(), resultAdapter);
    }
}
