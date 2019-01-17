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
                            is String -> FragmentSwitchHelper.showNextFragment(this, LoadAssetByIDFragment.createFor(searchCriteria))
                            is Identifier ->  FragmentSwitchHelper.showNextFragment(this, AssetIDsSearchFragment.createFor(searchCriteria))
                        }
                    } else
                        message.text = "${it.data}" //display
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
        TitleHelper.ensureTitle(this, "Searching for AMBAssetInfo")
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

        fun createFor(assetID: String): AssetInfoSearchFragment {
            return createForSearchCriteria(assetID)
        }

        fun createFor(identifier: Identifier): AssetInfoSearchFragment {
            return createForSearchCriteria(identifier)
        }

        private fun createForSearchCriteria(searchCriteria: Serializable): AssetInfoSearchFragment {
            return ARG_SEARCH_CRITERIA.putTo(AssetInfoSearchFragment(), searchCriteria)
        }
    }

}