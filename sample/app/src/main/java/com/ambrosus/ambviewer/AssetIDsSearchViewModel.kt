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
import com.ambrosus.sdk.model.Identifier

class AssetIDsSearchViewModel(
        private val identifier: Identifier,
        private val network: AMBNetwork
) : ViewModel() {

    private val _assetIdList = MutableLiveData<LoadResult<List<String>>>()

    init {
        loadAsset()
    }

    val assetIdList: LiveData<LoadResult<List<String>>>
    get() = _assetIdList

    fun loadAsset() {
        network.getAssetIDs(identifier).enqueue(
                object : NetworkCallback<List<String>> {
                    override fun onSuccess(call: NetworkCall<List<String>>, result: List<String>) {
                        LoadResult.passResult(_assetIdList, result)
                    }

                    override fun onFailure(call: NetworkCall<List<String>>, t: Throwable) {
                        LoadResult.passError(_assetIdList, t)
                    }
                }
        )
    }
}

class AssetIDsSearchViewModelFactory(private val identifier: Identifier, private val network: Network) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AssetIDsSearchViewModel(identifier, AMBSampleApp.network) as T
    }

}