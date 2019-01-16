package com.ambrosus.ambviewer


import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.ambrosus.sdk.*
import com.ambrosus.sdk.Event

class EventsListViewModel(
        private val assetId: String,
        private val network: Network
) : ViewModel() {

    private val _eventsList = MutableLiveData<LoadResult<List<Event>>>()

    val eventsList: LiveData<LoadResult<List<Event>>>
        get() = _eventsList

    fun refreshEventsList() {
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

class EventsListViewModelFactory(private val assetId: String, private val network: Network) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EventsListViewModel(assetId, network) as T
    }

}