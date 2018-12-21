package com.ambrosus.sdk;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class NetworkCallWrapper<T> implements AMBNetworkCall<T> {


    private static final String TAG = NetworkCallWrapper.class.getName();
    private final Call<T> retrofitCall;

    NetworkCallWrapper(Call<T> retrofitCall) {
        this.retrofitCall = retrofitCall;
    }

    @Override
    public T execute() throws Throwable {
        return getResponseResult(retrofitCall.execute());
    }

    @Override
    public void enqueue(final AMBNetworkCallback<T> callback) {
        retrofitCall.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                try {
                    callback.onSuccess(NetworkCallWrapper.this, getResponseResult(response));
                } catch (Exception e) {
                    onFailure(call, e);
                }
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

    @Override
    public AMBNetworkCall<T> clone() {
        return new NetworkCallWrapper<>(retrofitCall.clone());
    }

    private static <T> T getResponseResult(Response<T> response) throws Exception{
        if(response.isSuccessful())
            return response.body();
        else {
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

            throw new NetworkError(response.code(), message);
        }
    }


}
