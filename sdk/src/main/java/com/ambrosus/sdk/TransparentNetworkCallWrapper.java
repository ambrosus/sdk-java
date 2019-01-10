package com.ambrosus.sdk;

import retrofit2.Call;

class TransparentNetworkCallWrapper<T> extends NetworkCallWrapper<T, T> {

    TransparentNetworkCallWrapper(Call<T> retrofitCall) {
        super(retrofitCall, new TransparentResultAdapter<T>());
    }

    TransparentNetworkCallWrapper(Call<T> retrofitCall, NetworkErrorHandler ... errorHandlers) {
        super(retrofitCall, new TransparentResultAdapter<T>(), errorHandlers);
    }

}
