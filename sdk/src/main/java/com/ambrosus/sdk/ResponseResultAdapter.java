package com.ambrosus.sdk;

import retrofit2.Call;

interface ResponseResultAdapter<I, O> {

    O getResponseResult(I result);

}
