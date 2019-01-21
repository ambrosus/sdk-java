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
import com.ambrosus.sdk.*
import com.ambrosus.sdk.model.AMBAssetInfo
import com.ambrosus.sdk.model.AMBNetwork
import com.ambrosus.sdk.model.Identifier
import com.ambrosus.sdk.utils.Assert
import java.lang.IllegalArgumentException

class AssetInfoSearchViewModel(
        private val assetSearchCriteria: Any,
        private val network: AMBNetwork
) : ViewModel() {

    private val _assetsList = MutableLiveData<LoadResult<List<AMBAssetInfo>>>()

    init {
        Assert.assertTrue(
                assetSearchCriteria is String || assetSearchCriteria is Identifier,
                IllegalArgumentException::class.java,
                "assetSearchCriteria must be instance of String or Identifier"
        );
        refreshAssetsList()
    }

    val assetsList: LiveData<LoadResult<List<AMBAssetInfo>>>
        get() = _assetsList

    fun refreshAssetsList() {
        var networkCall = when (assetSearchCriteria) {
            is String -> network.getAssetInfo(assetSearchCriteria)
            is Identifier -> network.getAssetInfo(assetSearchCriteria)
            else -> throw IllegalArgumentException("you should pass String (assetID) or Identifier as searchCriteria")
        }
        networkCall.enqueue(
                object : NetworkCallback<List<AMBAssetInfo>> {
                    override fun onSuccess(call: NetworkCall<List<AMBAssetInfo>>, result: List<AMBAssetInfo>) {
                        _assetsList.value = LoadResult(result)
                    }

                    override fun onFailure(call: NetworkCall<List<AMBAssetInfo>>, t: Throwable) {
                        _assetsList.value = LoadResult(t)
                    }
                }
        )
    }

}

class AMBAssetInfoSearchViewModelFactory(private val searchCriteria: Any, private val network: Network) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AssetInfoSearchViewModel(searchCriteria, AMBSampleApp.network) as T
    }

}