package com.ambrosus.ambviewer


import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
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
        val searchParams = EventsSearchParamsBuilder().forAsset(assetId).build();
        network.findEvents(searchParams).enqueue(
                object: NetworkCallback<SearchResult<Event>> {
                    override fun onSuccess(call: NetworkCall<SearchResult<Event>>, result: SearchResult<Event>) {
                        //TODO: handle pagination here
                        _eventsList.value = LoadResult(result.values)
                    }

                    override fun onFailure(call: NetworkCall<SearchResult<Event>>, t: Throwable) {
                        _eventsList.value = LoadResult(t);
                    }
                }
        )
    }

}