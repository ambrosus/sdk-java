package com.ambrosus.ambviewer

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.ambrosus.sdk.Asset
import com.ambrosus.sdk.Network
import com.ambrosus.sdk.NetworkCall
import com.ambrosus.sdk.NetworkCallback
import com.ambrosus.sdk.model.AMBNetwork

class AssetViewModel(
        private val assetID: String,
        private val network: AMBNetwork
) : ViewModel() {

    private val _asset = MutableLiveData<LoadResult<Asset>>()

    init {
        loadAsset()
    }

    val asset: LiveData<LoadResult<Asset>>
        get() = _asset

    fun loadAsset() {
        network.getAsset(assetID).enqueue(
                object : NetworkCallback<Asset> {
                    override fun onSuccess(call: NetworkCall<Asset>, result: Asset) {
                        LoadResult.passResult(_asset, result)
                    }

                    override fun onFailure(call: NetworkCall<Asset>, t: Throwable) {
                        LoadResult.passError(_asset, t)
                    }
                }
        )
    }
}

class AssetViewModelFactory(private val assetID: String, private val network: Network) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AssetViewModel(assetID, AMBSampleApp.network) as T
    }

}