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


import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambrosus.demoapp.AMBSampleApp
import com.ambrosus.demoapp.AssetActivity
import com.ambrosus.demoapp.GlideApp
import com.ambrosus.demoapp.R
import com.ambrosus.demoapp.utils.BundleArgument
import com.ambrosus.demoapp.utils.Representation
import com.ambrosus.demoapp.utils.RepresentationFactory
import com.ambrosus.demoapp.utils.ViewUtils
import com.ambrosus.sdk.Entity
import com.ambrosus.sdk.GenericEventQueryBuilder
import com.ambrosus.sdk.Query
import com.ambrosus.sdk.model.AMBAssetInfo
import kotlinx.android.synthetic.main.fragment_recycler_view.*

class SearchResultsListFragment : androidx.fragment.app.Fragment() {

    private var scrollingToTheEnd: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = getViewModel()
        viewModel.getResults().observe(this, Observer {
            refreshLayout.isRefreshing = false

            var adapter = recyclerView.adapter as SearchResultsListAdapter?
            if(adapter == null) {
                adapter = SearchResultsListAdapter(this.context!!, it!!, AssetInfoRepresentation.AssetInfoRepresentationFactory(this))
                recyclerView.adapter = adapter
            }

            adapter.update(it!!)

            //we may need more than single update to get enough cached items especially for first page
            // size can be less than CACHE_REQUEST_THRESHOLD or even empty
            // so checking for that situation
            var cacheRequest = getCacheRequest()

            if(cacheRequest != null)
                getViewModel().update(cacheRequest)
        })
        viewModel.getErrors().observe(this, Observer {
            it?.let {
                val messages = mapOf(
                        SearchResultsViewModel.RequestType.REFRESH to "refresh results",
                        SearchResultsViewModel.RequestType.FETCH_NEXT_PAGE to "get next page",
                        SearchResultsViewModel.RequestType.FETCH_PREVIOUS_PAGE to "get previous page"
                )
                AMBSampleApp.errorHandler.handleError("Can't ${messages[it.requestType]}:", it.cause!!);
            }
        })
    }

    private fun getViewModel(): SearchResultsViewModel {
        return ViewModelProviders.of(
                this,
                SearchResultsViewModel.Factory(AMBSampleApp.network, ARG_SEARCH_QUERY.get(this))
        ).get(SearchResultsViewModel::class.java)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        recyclerView.addOnScrollListener(object: androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                if(dy != 0) {
                    scrollingToTheEnd = dy > 0

//                    Log.d(SearchResultsListFragment::class.java.name,
//                            "position: ${(recyclerView!!.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()}/${recyclerView.adapter.itemCount} ${if(dy>0) "+" else "-"}")

                    val requestType = getCacheRequest()

                    if(requestType != null)
                        getViewModel().update(requestType)
                }
            }
        })
        refreshLayout.setOnRefreshListener {
            getViewModel().update(SearchResultsViewModel.RequestType.REFRESH)
        }
    }

    fun getCacheRequest(): SearchResultsViewModel.RequestType? {
        val request = if(scrollingToTheEnd) SearchResultsViewModel.RequestType.FETCH_NEXT_PAGE
        else SearchResultsViewModel.RequestType.FETCH_PREVIOUS_PAGE

        if(cachedItemsLeft(scrollingToTheEnd) < CACHE_REQUEST_THRESHOLD)
            return request
        return null
    }

    private fun cachedItemsLeft(beforeEndOfTheList: Boolean): Int {
        val layoutManager = recyclerView!!.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition() +
                (layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstVisibleItemPosition()) / 2
        return Math.abs(
                (if (beforeEndOfTheList) layoutManager.itemCount else 0) - position
        )
    }

    private class AssetInfoRepresentation(inflater: LayoutInflater, parent: ViewGroup, private val fragment: androidx.fragment.app.Fragment) : Representation<Entity>(R.layout.item_asset_info, inflater, parent) {

        override fun display(data: Entity?) {
            val assetInfo = data as AMBAssetInfo
            ViewUtils.setText(itemView, R.id.title, assetInfo.name ?: "no title")
            ViewUtils.setText(itemView, R.id.assetID, assetInfo.assetId)
            ViewUtils.setText(itemView, R.id.authorID, Html.fromHtml(fragment.resources.getString(R.string.assetCreator, assetInfo.accountAddress)))

            if(!assetInfo.images.isEmpty()) {
                val url = assetInfo.images.entries.iterator().next().value.get("url")
                if(url != null)
                    GlideApp.with(fragment)
                            //TODO add Image resultType to SDK
                            .load(url.asString)
                            .placeholder(R.drawable.placeholder_logo)
                            .into(itemView.findViewById(R.id.assetImage))
            }
            itemView.setOnClickListener {
                AssetActivity.startFor(assetInfo, fragment.context!!)
            }
        }

        class AssetInfoRepresentationFactory(private val fragment: androidx.fragment.app.Fragment) : RepresentationFactory<Entity>(){

            override fun createRepresentation(inflater: LayoutInflater, parent: ViewGroup): Representation<Entity> {
                return AssetInfoRepresentation(inflater, parent, fragment)
            }

        }

    }

    companion object {

        private const val CACHE_REQUEST_THRESHOLD = GenericEventQueryBuilder.MAX_PAGE_SIZE/2

        private val ARG_SEARCH_QUERY = BundleArgument<Query<*>>("ARG_QUERY", Query::class.java)

        fun create(query: Query<*>) : SearchResultsListFragment {
            return ARG_SEARCH_QUERY.putTo(SearchResultsListFragment(), query)
        }

    }
}