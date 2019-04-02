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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class NetworkCallWrapper<T> implements NetworkCall<T> {

    private static final String TAG = NetworkCallWrapper.class.getName();

    private final Call<T> retrofitCall;
    private final NetworkErrorHandler[] errorHandlers;

    NetworkCallWrapper(Call<T> retrofitCall) {
        this(retrofitCall, (NetworkErrorHandler[]) null);
    }

    NetworkCallWrapper(Call<T> retrofitCall, NetworkErrorHandler ... errorHandlers) {
        this.retrofitCall = retrofitCall;
        this.errorHandlers = errorHandlers;
    }

    @NonNull
    @Override
    public T execute() throws Throwable {
        return getResponseResult(retrofitCall.execute());
    }

    @Override
    public void enqueue(@NonNull final NetworkCallback<T> callback) {
        retrofitCall.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                T responseResult;
                try {
                    responseResult = getResponseResult(response);
                } catch (Exception e) {
                    onFailure(call, e);
                    return;
                }
                callback.onSuccess(NetworkCallWrapper.this, responseResult);
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callback.onFailure(NetworkCallWrapper.this, t);
            }
        });
    }

    @Override
    public boolean isExecuted() {
        return retrofitCall.isExecuted();
    }

    @Override
    public void cancel() {
        retrofitCall.cancel();
    }

    @Override
    public boolean isCanceled() {
        return retrofitCall.isCanceled();
    }

    @NonNull
    @Override
    public NetworkCall<T> clone() {
        //TODO: need to check clone result with unit test, I did an error in its implementation
        return new NetworkCallWrapper<>(retrofitCall.clone(), errorHandlers);
    }

    T getResponseResult(Response<T> response) throws Exception{
        checkForNetworkError(response);
        return response.body();
    }

    private void checkForNetworkError(Response response) throws Exception {
        if(!response.isSuccessful()) {
            String responseString =  response.errorBody().string();

            String message = null;

            try {
                JsonParser parser = new JsonParser();
                JsonElement responseJSON = parser.parse(responseString);

                if(responseJSON.isJsonObject()) {
                    JsonElement reason = responseJSON.getAsJsonObject().get("reason");
                    if(reason != null)
                        message = reason.toString();
                }
            } catch (JsonSyntaxException e) {
                //TODO do we need a Logger?
                //Log.e(TAG, "Can't parse error response: " + responseString, e);
            }

            int code = response.code();

            RequestFailedException failReason = new RequestFailedException(code, message);

            if(errorHandlers != null) {
                for (NetworkErrorHandler errorHandler : errorHandlers) {
                    errorHandler.handleNetworkError(failReason);
                }
            }

            throw failReason;
        }
    }
}
