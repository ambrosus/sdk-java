package com.ambrosus.ambrosussdk.network

import android.util.Log
import com.ambrosus.ambrosussdk.model.AMBAsset
import com.ambrosus.ambrosussdk.model.AMBEvent
import com.lancelotmobile.android.hellokotlin.AMBService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
Copyright: Ambrosus Technologies GmbH
Email: tech@ambrosus.com

This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

This Source Code Form is "Incompatible With Secondary Licenses", as defined by the Mozilla Public License, v. 2.0.
*/

class AMBNetworkConfiguration {

    /// The base path for the Ambrosus API
    var ambrosusNetworkPath = "https://gateway-test.ambrosus.com/"

}

//    val imageLoader: ImageLoader by lazy {
//        ImageLoader(requestQueue,
//                object : ImageLoader.ImageCache {
//                    private val cache = LruCache<String, Bitmap>(20)
//                    override fun getBitmap(url: String): Bitmap {
//                        return cache.get(url)
//                    }
//
//                    override fun putBitmap(url: String, bitmap: Bitmap) {
//                        cache.put(url, bitmap)
//                    }
//                })
//    }
//    val requestQueue: RequestQueue by lazy {
//        // applicationContext is key, it keeps you from leaking the
//        // Activity or BroadcastReceiver if someone passes one in.
//        Volley.newRequestQueue(context.applicationContext)
//    }
//
//    fun <T> addToRequestQueue(req: Request<T>) {
//        requestQueue.add(req)
//    }

class AMBNetwork {

    val configuration: AMBNetworkConfiguration = AMBNetworkConfiguration()

    private val retrofit: Retrofit

    private val service: AMBService

    init {
        println("This ($this) is a singleton")
        retrofit = Retrofit.Builder()
                .baseUrl("https://gateway-test.ambrosus.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        service = retrofit.create<AMBService>(AMBService::class.java!!)
    }

    private object Holder {
        val INSTANCE = AMBNetwork()
    }

    companion object {
        val instance: AMBNetwork by lazy { Holder.INSTANCE }
    }

    var b: String? = null

    /**
     * Request JSON from the API
     *
     * @param type - code type
     * @param barcodeData - string representation of the data in the code
     * @param completion - callback which will be invoked once the data is
     * fetched and converted to a model
     */
    public fun requestAsset(type: String, barcodeData: String, completion:
    (data:
     AMBAsset?) -> Unit) {
        service.getEventsByCode(
                mapOf(Pair(type, barcodeData!!)))
                .enqueue(success = { response ->
                    if (response.body() != null) {
                        Log.i("AMB", response.body().toString())
                        val events = AMBEvent.fetchEvents(response.body())
                        if (events.firstOrNull()?.assetId != null && events.firstOrNull()?.assetId!!.length > 0) {
                            service.getAsset(
                                    events.first().assetId.toString())
                                    .enqueue(success = { response ->
                                        val asset = AMBAsset(response.body())
                                        asset.events = events
                                        completion(asset)
                                    },

                                            failure = { t -> completion(null) })
                        } else {
                            completion(null)
                        }
                    } else {
                        completion(null)

                    }

                },
                        failure = { t -> completion(null) })

    }

    /**
     * Request JSON from the API
     *
     * @param assetId - the id of the asset
     * @param completion - callback which will be invoked once the data is
     * fetched and converted to a model
     */
    public fun requestAsset(assetId: String, completion:
    (data:
     AMBAsset?) -> Unit) {

        service.getAsset(
                assetId)
                .enqueue(success = { response ->
                    if (response.body() != null) {
                        val asset = AMBAsset(response.body())
                        Log.i("AMB", response.body().toString())
                        service.getEvents(assetId)
                                .enqueue(success = { response ->
                                    val events = AMBEvent.fetchEvents(response.body())
                                    asset.events = events
                                    completion(asset)
                                },
                                        failure = { t -> completion(null) })
                    } else {
                        completion(null)
                    }
                },

                        failure = { t -> completion(null) })
    }


    /// Gets events back based on a scanner identifier
    ///
    /// - Parameters:
    ///   - query: The identifier including type of scanner e.g. [gtin]=0043345534
    ///   - completion: The events if available, nil if none returned
//    fun requestEvents(fromQuery: String, context: Context, completion: (response:
//                                                                        List<AMBEvent>?) -> Unit) {
//        val path = configuration.ambrosusNetworkPath +
//                "events?data[type]=ambrosus.asset.identifier&data" + fromQuery
//        request(path, context) { data ->
//            fetchEvents(data) { events ->
//                completion(events)
//            }
//        }
//    }

    /// Gets a single asset back directly from an ID
    ///
    /// - Parameters:
    ///   - id: The identifier associated with the desired asset
    ///   - completion: The asset if available, nil if unavailable
//    fun requestAsset(fromId: String, context: Context, completion: (data:
//                                                                    AMBAsset?) ->
//    Unit) {
//        val asset = AMBDataStore.sharedInstance.assetStore.fetch(fromId)
//        if (asset != null) {
//            completion(asset)
//            return
//        }
//
//        val path = configuration.ambrosusNetworkPath + "assets/" + fromId
//        request(path, context) { data ->
//            if (data == null) {
//                completion(null)
//            } else {
//                val asst = AMBAsset(data)
//                completion(asst)
//            }
//        }
//    }

    /// Fetches the events associated with an asset ID
    ///
    /// - Parameters:
    ///   - id: The identifier associated with the asset with desired events
    ///   - completion: The array of events if available, nil if unavailable
//    fun requestEventsFrom(assetId: String, context: Context, completion:
//    (data:
//     List<AMBEvent>?) -> Unit) {
//        val path = configuration.ambrosusNetworkPath + "events?assetId=" +
//                assetId
//        request(path, context) { data ->
//            fetchEvents(data) { events ->
//                if (events != null) {
//                    completion(events)
//                }
//            }
//        }
//    }

    private fun fetchEvents(from: Any?, completion: (data: List<AMBEvent>?) ->
    Unit) {
        val data = from as? Map<String, Any>
        if (data == null) {
            completion(null)
            return
        }

        val results = data["results"] as? List<Map<String, Any>>
        if (results == null) {
            completion(null)
            return
        }
//        val events = results.map { AMBEvent(it) }
//        completion(events)
    }

}

fun <T> Call<T>.enqueue(success: (response: Response<T>) -> Unit,
                        failure: (t: Throwable) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>?, response: Response<T>) = success(response)

        override fun onFailure(call: Call<T>?, t: Throwable) = failure(t)
    })
}
