/*
 * Copyright: Ambrosus Inc.
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

package com.ambrosus.apps

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.util.Log
import com.ambrosus.sdk.Entity
import com.ambrosus.sdk.Network
import com.ambrosus.sdk.NetworkCall
import com.ambrosus.sdk.NetworkCallback
import com.ambrosus.sdk.PageQueryBuilder
import com.ambrosus.sdk.Query
import com.ambrosus.sdk.SearchResult
import com.ambrosus.sdk.utils.Assert
import java.util.Collections
import java.util.LinkedList

class SearchResultsViewModel(network: Network, private val query: Query<*>, cacheSize: Int = DEFAULT_CACHE_SIZE) : ViewModel() {

    private val results = MutableLiveData<List<SearchResult<out Entity>>>()
    private val errors = MutableLiveData<DataRequestException>()

    private var refreshing = false
    private var pageQueryBuilder: PageQueryBuilder<*>? = null

    fun getResults(): LiveData<List<SearchResult<out Entity>>> = results
    fun getErrors(): LiveData<DataRequestException> = errors

    private val repository = CachedPagesRepository(
            network,
            {requestType: RequestType, page: SearchResult<out Entity> ->
                errors.value = null
                refreshing = false

                when(requestType) {
                    RequestType.REFRESH -> {
                        pageQueryBuilder = PageQueryBuilder(page)
                        pagesCache.init(page)
                    }
                    RequestType.FETCH_PREVIOUS_PAGE, RequestType.FETCH_NEXT_PAGE -> {
                        //Log.d("SearchResultsListFragment", "got result for: ${page.page}")
                        pagesCache.cache(page, requestType == RequestType.FETCH_NEXT_PAGE)
                    }
                }
            },
            {requestType: RequestType, error: Throwable ->
                errors.value = DataRequestException(requestType, error)
                refreshing = false
            }
    );

    private val pagesCache = PagesCache(cacheSize) { cachedPages ->
        //Log.d("SearchResultsListFragment", "Passing pages to UI: $cachedPages")
        results.value = cachedPages
    }

    init {
        update(RequestType.REFRESH)
    }

    enum class RequestType {
        REFRESH, FETCH_PREVIOUS_PAGE, FETCH_NEXT_PAGE
    }

    class DataRequestException(val requestType: RequestType, cause: Throwable) : Exception(cause)

    private class PagesCache(private val cacheSize: Int, private val onUpdate: (List<SearchResult<out Entity>>) -> Unit) {

        private val cachedPages = LinkedList<SearchResult<out Entity>>()

        val headPage: Int
            get() {
                ensureInitialized()
                return cachedPages[0].page
            }

        val tailPage: Int
            get() {
                ensureInitialized()
                return cachedPages[cachedPages.size - 1].page
            }

        private val cachedItemsCount: Int
            get() {
                var cachedItemsCount = 0
                for (page in cachedPages) {
                    cachedItemsCount += page.values.size
                }
                return cachedItemsCount
            }

        private fun ensureInitialized() {
            Assert.assertTrue(cachedPages.size > 0, IllegalArgumentException::class.java, "Cache not initialized")
        }

        fun init(firstPage: SearchResult<out Entity>) {
            cachedPages.clear()
            cache(firstPage)
        }

        fun cache(page: SearchResult<out Entity>, tailPage: Boolean = true) {
            if (tailPage)
                cachedPages.add(page)
            else
                cachedPages.addFirst(page)
            trimCache(tailPage)
            onUpdate(ArrayList(cachedPages))
        }

        private fun trimCache(fromHead: Boolean) {
            while (cachedPages.size > 1 && cachedItemsCount > cacheSize) {
                if (fromHead)
                    cachedPages.removeFirst()
                else
                    cachedPages.removeLast()
            }
        }
    }

    private class CachedPagesRepository(
            private val network: Network,
            private val onResult: (RequestType, SearchResult<out Entity>) -> Unit,
            private val onError: (RequestType, Throwable) -> Unit) {

        private var activeCall: NetworkCall<SearchResult<out Entity>>? = null
        private var activeRequestType: RequestType? = null

        fun requestPage(requestType: RequestType, query: Query<*>){
            if(requestType != activeRequestType) {
                activeCall?.cancel()
                activeCall = network.find(query)
                activeRequestType = requestType
                //Log.d("SearchResultsListFragment", "Fire request for: ${query.page}")
                activeCall!!.enqueue(object: NetworkCallback<SearchResult<out Entity>>{
                    override fun onSuccess(call: NetworkCall<SearchResult<out Entity>>, result: SearchResult<out Entity>) {
                        resetState()
                        onResult(requestType, result)
                    }

                    override fun onFailure(call: NetworkCall<SearchResult<out Entity>>, t: Throwable) {
                        resetState()
                        onError(requestType, t)
                    }

                    private fun resetState() {
                        activeCall = null
                        activeRequestType = null
                    }


                })
            }
        }


    }

    fun update(requestType: RequestType) {
        when (requestType) {
            SearchResultsViewModel.RequestType.REFRESH -> {
                if (!refreshing) {
                    repository.requestPage(requestType, query)
                }
            }
            RequestType.FETCH_PREVIOUS_PAGE, RequestType.FETCH_NEXT_PAGE -> {
                pageQueryBuilder?.let { pageQueryBuilder ->
                    val page =
                            if (requestType == RequestType.FETCH_NEXT_PAGE)
                                pagesCache.tailPage + 1
                            else
                                pagesCache.headPage - 1

                    if (page >= 0 && page < pageQueryBuilder.totalPages) {
                        //Log.d("SearchResultsListFragment", "PAGE REQUEST: $page" )
                        repository.requestPage(requestType, pageQueryBuilder.getQueryForPage(page))
                    }
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_CACHE_SIZE = 300
    }

    class Factory(private val network: Network, private val query: Query<*>): ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchResultsViewModel(network, query) as T
        }

    }
}