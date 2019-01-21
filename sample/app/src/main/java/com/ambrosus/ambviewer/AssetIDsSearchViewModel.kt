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