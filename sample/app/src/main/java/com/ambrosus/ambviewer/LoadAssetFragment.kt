package com.ambrosus.ambviewer

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambrosus.ambviewer.utils.TitleHelper
import com.ambrosus.sdk.EntityNotFoundException
import kotlinx.android.synthetic.main.fragment_asset_search.*
import kotlinx.android.synthetic.main.loading_indicator_small.*

class LoadAssetFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getViewModel().asset.observe(this, Observer {
            loadingIndicatorSmall.visibility = View.GONE
            if(it != null) {
                if(it.isSuccessful()) {
                    AssetActivity.startFor(it.data, activity!!)
                    activity!!.onBackPressed()
                } else {
                    if(it.error is EntityNotFoundException)
                        message.text = "Can't find any asset with id: ${getAssetId()}"
                    else
                        message.text = AMBSampleApp.errorHandler.getErrorMessage(it.error)
                }
            }
        });
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_asset_search, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        message.text = "Loading Asset ${getAssetId()}"
    }

    override fun onResume() {
        super.onResume()
        TitleHelper.ensureTitle(this, "Loading...")
    }

    private fun getViewModel(): AssetViewModel {
        return ViewModelProviders.of(
                this,
                AssetViewModelFactory(getAssetId(), AMBSampleApp.network)
        ).get(AssetViewModel::class.java)
    }

    private fun getAssetId() = ARG_ID.get(this)

    companion object {

        fun createFor(assetID: String): LoadAssetFragment {
            return ARG_ID.putTo(LoadAssetFragment(), assetID)
        }

    }

}