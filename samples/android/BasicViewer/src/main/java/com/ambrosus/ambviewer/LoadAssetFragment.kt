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
import com.ambrosus.sdk.Asset
import com.ambrosus.sdk.model.Identifier

class LoadAssetFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getViewModel().asset.observe(this, Observer {
            getListener()?.onLoadResult(it!!, getAssetIdentifier())
        });
    }

    private fun getListener() = parentFragment as? LoadResultListener

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_status, container, false);
//    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        loadingMessage.text = "Loading Asset ${getAssetId()}"
//    }

    private fun getViewModel(): AssetViewModel {
        return ViewModelProviders.of(
                this,
                AssetViewModelFactory(getAssetIdentifier().value, AMBSampleApp.network)
        ).get(AssetViewModel::class.java)
    }

    private fun getAssetIdentifier() = ARG_IDENTIFIER.get(this) as Id

    companion object {

        fun createFor(assetIdentifier: Id): LoadAssetFragment {
            return ARG_IDENTIFIER.putTo(LoadAssetFragment(), assetIdentifier)
        }
    }

    interface LoadResultListener {
        fun onLoadResult(result: LoadResult<Asset>, assetIdentifier: Id)
    }

}