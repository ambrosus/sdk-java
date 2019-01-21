/*
 * Copyright: Ambrosus Technologies GmbH
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

package com.ambrosus.ambviewer

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.ambrosus.sdk.Event
import com.ambrosus.sdk.EventSearchParamsBuilder
import com.ambrosus.sdk.Network
import com.ambrosus.sdk.NetworkCall
import com.ambrosus.sdk.NetworkCallback
import com.ambrosus.sdk.SearchResult
import com.ambrosus.sdk.model.AMBEvent
import com.ambrosus.sdk.model.AMBNetwork

abstract class EventsListViewModel : ViewModel() {

    abstract val eventsList: LiveData<LoadResult<List<Event>>>
    abstract fun refreshEventsList()

}

class AMBEventsListViewModel(
        private val assetId: String,
        private val network: AMBNetwork
) : EventsListViewModel() {

    private val _eventsList = MutableLiveData<LoadResult<List<Event>>>()

    override val eventsList: LiveData<LoadResult<List<Event>>>
        get() = _eventsList

    override fun refreshEventsList() {
        val searchParams = EventSearchParamsBuilder().forAsset(assetId).build();
        network.findAMBEvents(searchParams).enqueue(
                object: NetworkCallback<List<AMBEvent>> {
                    override fun onSuccess(call: NetworkCall<List<AMBEvent>>, result: List<AMBEvent>) {
                        LoadResult.passResult(_eventsList, result)
                    }

                    override fun onFailure(call: NetworkCall<List<AMBEvent>>, t: Throwable) {
                        LoadResult.passError(_eventsList, t)
                    }
                }
        )
    }

}

class AMBEventsListViewModelFactory(private val assetId: String, private val network: AMBNetwork) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AMBEventsListViewModel(assetId, network) as T
    }

}

class GenericEventsListViewModel(
        private val assetId: String,
        private val network: Network
) : EventsListViewModel() {

    private val _eventsList = MutableLiveData<LoadResult<List<Event>>>()

    override val eventsList: LiveData<LoadResult<List<Event>>>
        get() = _eventsList

    override fun refreshEventsList() {
        val searchParams = EventSearchParamsBuilder().forAsset(assetId).build();
        network.findEvents(searchParams).enqueue(
                object: NetworkCallback<SearchResult<Event>> {
                    override fun onSuccess(call: NetworkCall<SearchResult<Event>>, result: SearchResult<Event>) {
                        //TODO: handle pagination here
                        LoadResult.passResult(_eventsList, result.values)
                    }

                    override fun onFailure(call: NetworkCall<SearchResult<Event>>, t: Throwable) {
                        LoadResult.passError(_eventsList, t)
                    }
                }
        )
    }
}

class GenericEventsListViewModelFactory(private val assetId: String, private val network: Network) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return GenericEventsListViewModel(assetId, network) as T
    }

}