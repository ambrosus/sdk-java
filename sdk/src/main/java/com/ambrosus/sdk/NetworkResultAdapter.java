package com.ambrosus.sdk;

public interface NetworkResultAdapter<I, O> {

    O convert(I source);

}
