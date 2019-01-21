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