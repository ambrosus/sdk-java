package com.ambrosus.sdk;

import android.support.annotation.NonNull;

public class NetworkCallAdapter<I, O> implements NetworkCall<O> {

    private final NetworkCall<I> networkCall;
    private final NetworkResultAdapter<I, O> resultAdapter;

    public NetworkCallAdapter(NetworkCall<I> networkCall, NetworkResultAdapter<I, O> resultAdapter) {
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
                callback.onSuccess(NetworkCallAdapter.this, resultAdapter.convert(result));
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
