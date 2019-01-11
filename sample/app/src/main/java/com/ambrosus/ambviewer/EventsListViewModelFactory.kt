package com.ambrosus.ambviewer


import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.ambrosus.sdk.AMBNetwork

class EventsListViewModelFactory(private val assetId: String, private val ambNetwork: AMBNetwork) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EventsListViewModel(assetId, ambNetwork) as T
    }

}