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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambrosus.ambviewer.utils.BundleArgument
import com.ambrosus.ambviewer.utils.FragmentSwitchHelper
import com.ambrosus.ambviewer.utils.TitleHelper
import com.ambrosus.sdk.model.Identifier
import kotlinx.android.synthetic.main.fragment_asset_search.*
import kotlinx.android.synthetic.main.loading_indicator_small.*
import java.io.Serializable

class AssetInfoSearchFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getViewModel().assetsList.observe(this, Observer {
            loadingIndicatorSmall.visibility = View.GONE
            if(it != null) {
                if(it.isSuccessful()) {
                    if(it.data.isEmpty()) {
                        val searchCriteria = getSearchCriteria()
                        when(searchCriteria) {
                            is String -> FragmentSwitchHelper.replaceFragment(this, LoadAssetFragment.createFor(searchCriteria), false)
                            is Identifier ->  FragmentSwitchHelper.replaceFragment(this, AssetIDsSearchFragment.createFor(searchCriteria), false)
                        }
                    } else {
                        AssetActivity.startFor(it.data[0], activity!!)
                        activity!!.onBackPressed()
                    }
                } else
                    message.text = AMBSampleApp.errorHandler.getErrorMessage(it.error)
            }
        });
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_asset_search, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        message.text = "Searching for AMBAssetInfo ${getSearchCriteria()}"
    }

    override fun onResume() {
        super.onResume()
        TitleHelper.ensureTitle(this, "Searching for Info...")
    }

    private fun getViewModel(): AssetInfoSearchViewModel {
        return ViewModelProviders.of(
                this,
                AMBAssetInfoSearchViewModelFactory(getSearchCriteria(), AMBSampleApp.network)
        ).get(AssetInfoSearchViewModel::class.java)
    }

    private fun getSearchCriteria() = ARG_SEARCH_CRITERIA.get(this)

    companion object {

        private val ARG_SEARCH_CRITERIA = BundleArgument<Serializable>("ARG_SEARCH_CRITERIA", Serializable::class.java)

        fun getArguments(assetID: String): Bundle {
            return getArgumentsByCriteria(assetID)
        }

        fun getArguments(identifier: Identifier): Bundle {
            return getArgumentsByCriteria(identifier)
        }

        private fun getArgumentsByCriteria(searchCriteria: Serializable): Bundle {
            return ARG_SEARCH_CRITERIA.put(Bundle(), searchCriteria)
        }


    }

}