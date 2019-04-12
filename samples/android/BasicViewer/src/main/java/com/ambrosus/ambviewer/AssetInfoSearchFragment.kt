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

package com.ambrosus.ambviewer

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import com.ambrosus.ambviewer.utils.BundleArgument
import com.ambrosus.sdk.Entity
import com.ambrosus.sdk.Query
import com.ambrosus.sdk.SearchResult
import com.ambrosus.sdk.model.AMBAssetInfo
import com.ambrosus.sdk.model.AssetInfoQueryBuilder
import com.ambrosus.sdk.model.Identifier
import java.io.Serializable

class AssetInfoSearchFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getViewModel().getResults().observe(this, Observer {
            getListener()?.onSearchResult(it!!, getIdentifier())
        });
    }

    private fun getViewModel(): SingleSearchResultViewModel {
        return ViewModelProviders.of(
                this,
                SingleSearchResultViewModel.Factory(AMBSampleApp.network, getQuery())
        ).get(SingleSearchResultViewModel::class.java)
    }

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_status, container, false);
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        loadingMessage.text = "Searching for AMBAssetInfo (${getIdentifier()})"
//    }

    private fun getQuery() : Query<AMBAssetInfo> {
        val identifier = getIdentifier()
        val queryBuilder = AssetInfoQueryBuilder();
        when(identifier) {
            is Id -> queryBuilder.forAsset(identifier.value)
            else -> queryBuilder.byIdentifier(identifier)
        }
        return queryBuilder.build();
    }

    private fun getListener() = parentFragment as? SearchResultListener

    private fun getIdentifier() = ARG_IDENTIFIER.get(this)

    interface SearchResultListener {

        fun onSearchResult(result: LoadResult<SearchResult<out Entity>>, searchCriteria: Identifier)

    }

    companion object {
        fun createFor(identifier: Identifier): AssetInfoSearchFragment =
                ARG_IDENTIFIER.putTo(AssetInfoSearchFragment(), identifier)
    }

}