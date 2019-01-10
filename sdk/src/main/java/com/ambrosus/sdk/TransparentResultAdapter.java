package com.ambrosus.sdk;

class TransparentResultAdapter<T> implements ResponseResultAdapter<T, T> {

    @Override
    public T getResponseResult(T result) {
        return result;
    }
}
