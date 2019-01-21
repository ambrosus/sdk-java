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