package com.ambrosus.ambviewer

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambrosus.ambviewer.utils.FragmentSwitchHelper
import com.ambrosus.ambviewer.utils.TitleHelper
import com.ambrosus.sdk.model.Identifier
import kotlinx.android.synthetic.main.fragment_asset_search.*
import kotlinx.android.synthetic.main.loading_indicator_small.*

class AssetIDsSearchFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getViewModel().assetIdList.observe(this, Observer {
            loadingIndicatorSmall.visibility = View.GONE
            if(it != null) {
                if(it.isSuccessful()) {
                    if(it.data.isEmpty()) {
                        message.text = "Can't find any asset with ${identifier()}"
                    } else {
                        FragmentSwitchHelper.showNextFragment(this, LoadAssetByIDFragment.createFor(it.data[0]))
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
        message.text = "Searching for asset ids by ${identifier()}"
    }

    override fun onResume() {
        super.onResume()
        TitleHelper.ensureTitle(this, "Searching")
    }

    private fun getViewModel(): AssetIDsSearchViewModel {
        return ViewModelProviders.of(
                this,
                AssetIDsSearchViewModelFactory(identifier(), AMBSampleApp.network)
        ).get(AssetIDsSearchViewModel::class.java)
    }

    private fun identifier() = ARG_IDENTIFIER.get(this)

    companion object {

        fun createFor(identifier: Identifier): AssetIDsSearchFragment {
            return ARG_IDENTIFIER.putTo(AssetIDsSearchFragment(), identifier)
        }

    }

}