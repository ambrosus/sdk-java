package com.ambrosus.ambviewer

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambrosus.ambviewer.utils.BundleArgument
import com.ambrosus.ambviewer.utils.TitleHelper
import com.ambrosus.sdk.Identifier
import kotlinx.android.synthetic.main.fragment_asset_search.*
import kotlinx.android.synthetic.main.loading_indicator_small.*
import java.io.Serializable

class AssetInfoSearchFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getViewModel().assetsList.observe(this, Observer {
            loadingIndicatorSmall.visibility = View.GONE
            if(it != null) {
                message.text = if(it.isSuccessful()) "${it.data}" else AMBSampleApp.errorHandler.getErrorMessage(it.error)
            }
        });
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_asset_search, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        message.text = "Searching for AMBAssetInfo ${ARG_SEARCH_CRITERIA.get(this)}"
    }

    override fun onResume() {
        super.onResume()
        TitleHelper.ensureTitle(this, "Search")
        getViewModel().refreshAssetsList()
    }

    private fun getViewModel(): AssetInfoSearchViewModel {
        return ViewModelProviders.of(
                this,
                AMBAssetInfoSearchViewModelFactory(ARG_SEARCH_CRITERIA.get(this), AMBSampleApp.network)
        ).get(AssetInfoSearchViewModel::class.java)
    }

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

//    network.getAsset(assetId).enqueue(
//    object: NetworkCallback<Asset> {
//        override fun onSuccess(call: NetworkCall<Asset>, result: Asset) {
//            IntentsUtil.runAssetActivity(activity!!, result)
//        }
//
//        override fun onFailure(call: NetworkCall<Asset>, t: Throwable) {
//            handleAPICallFailure(call, this, t);
//        }
//    }
//    )


//    AMBSampleApp.ambNetwork.findEvents(eventSearchParams).enqueue(
//    object: AMBNetworkCallback<SearchResult<Event>> {
//        override fun onSuccess(call: AMBNetworkCall<SearchResult<Event>>, result: SearchResult<Event>) {
//            for(event in result.values) {
////                                    if(event.assetIdentifiers.contains(assetIdentifier)) {
////                                        AMBSampleApp.ambNetwork.getAsset(event.assetId).enqueue(
////                                            object: AMBNetworkCallback<Asset> {
////                                                override fun onSuccess(call: AMBNetworkCall<Asset>, result: Asset) {
////                                                    IntentsUtil.runAssetActivity(activity!!, result)
////                                                }
////
////                                                override fun onFailure(call: AMBNetworkCall<Asset>, t: Throwable) {
////                                                    handleAPICallFailure(call, this, t);
////                                                }
////                                            }
////                                        )
////                                        return
////                                    }
//            }
//            resumeScanning()
//        }
//
//        override fun onFailure(call: AMBNetworkCall<SearchResult<Event>>, t: Throwable) {
//            handleAPICallFailure(call, this, t);
//        }
//    }

}