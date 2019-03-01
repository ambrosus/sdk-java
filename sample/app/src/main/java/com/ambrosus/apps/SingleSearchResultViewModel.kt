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

class SingleSearchResultViewModel (
        private val network: Network,
        private val query: Query<*>
) : ViewModel() {

    private val searchResult = MutableLiveData<LoadResult<SearchResult<out Entity>>>()

    init {
        reQuery()
    }

    fun getResults(): LiveData<LoadResult<SearchResult<out Entity>>> = searchResult

    fun reQuery() {
        network.find(query).enqueue(
                object : NetworkCallback<SearchResult<out Entity>> {
                    override fun onSuccess(call: NetworkCall<SearchResult<out Entity>>, result: SearchResult<out Entity>) {
                        searchResult.value = LoadResult(result)
                    }

                    override fun onFailure(call: NetworkCall<SearchResult<out Entity>>, t: Throwable) {
                        searchResult.value = LoadResult(t)
                    }

                }
        )
    }

    class Factory(private val network: Network, private val query: Query<*>) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return SingleSearchResultViewModel(network, query) as T
        }

    }
}

