package com.ambrosus.sdk;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class NetworkCallWrapper<I, O> implements AMBNetworkCall<O> {

    private static final String TAG = NetworkCallWrapper.class.getName();

    private final Call<I> retrofitCall;
    private final NetworkErrorHandler[] errorHandlers;
    private final ResponseResultAdapter<I,O> resultAdapter;

    NetworkCallWrapper(Call<I> retrofitCall, ResponseResultAdapter<I,O> resultAdapter) {
        this(retrofitCall, resultAdapter, (NetworkErrorHandler[]) null);
    }

    NetworkCallWrapper(Call<I> retrofitCall, ResponseResultAdapter<I,O> resultAdapter, NetworkErrorHandler ... errorHandlers) {
        this.retrofitCall = retrofitCall;
        this.resultAdapter = resultAdapter;
        this.errorHandlers = errorHandlers;
    }

    @Override
    public O execute() throws Throwable {
        return getResponseResult(retrofitCall.execute());
    }

    @Override
    public void enqueue(final AMBNetworkCallback<O> callback) {
        retrofitCall.enqueue(new Callback<I>() {
            @Override
            public void onResponse(Call<I> call, Response<I> response) {
                O responseResult;
                try {
                    responseResult = getResponseResult(response);
                } catch (Exception e) {
                    onFailure(call, e);
                    return;
                }
                callback.onSuccess(NetworkCallWrapper.this, responseResult);
            }

            @Override
            public void onFailure(Call<I> call, Throwable t) {
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

    @Override
    public AMBNetworkCall<O> clone() {
        //TODO: need to check clone result with unit test, I did an error in its implementation
        return new NetworkCallWrapper<>(retrofitCall.clone(), resultAdapter, errorHandlers);
    }

    O getResponseResult(Response<I> response) throws Exception{
        checkForNetworkError(response);
        return resultAdapter.getResponseResult(response.body());
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
                Log.e(TAG, "Can't parse error response: " + responseString);
            }

            int code = response.code();

            if(errorHandlers != null) {
                for (NetworkErrorHandler errorHandler : errorHandlers) {
                    errorHandler.handleNetworkError(code, message);
                }
            }

            new CommonNetworkErrorHandler().handleNetworkError(code, message);
        }
    }
}
