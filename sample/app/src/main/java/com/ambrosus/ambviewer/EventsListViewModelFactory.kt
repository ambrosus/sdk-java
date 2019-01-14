package com.ambrosus.ambviewer


import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.ambrosus.sdk.Network

class EventsListViewModelFactory(private val assetId: String, private val network: Network) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EventsListViewModel(assetId, network) as T
    }

}