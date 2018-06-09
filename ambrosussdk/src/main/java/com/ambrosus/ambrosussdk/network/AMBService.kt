package com.lancelotmobile.android.hellokotlin

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import java.util.*

/*
Copyright: Ambrosus Technologies GmbH
Email: tech@ambrosus.com

This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

This Source Code Form is "Incompatible With Secondary Licenses", as defined by the Mozilla Public License, v. 2.0.
*/

public interface AMBService {
    @GET("assets/{assetId}")
    fun getAsset(@Path("assetId") assetId: String): Call<HashMap<String, Object>>

    @GET("events")
    fun getEvents(@Query("assetId") assetId: String): Call<HashMap<String,
            Object>>

    @GET("events?data[type]=ambrosus.asset.identifier")
    fun getEventsByCode(@QueryMap params: Map<String, String>):
            Call<HashMap<String,
                    Object>>
}