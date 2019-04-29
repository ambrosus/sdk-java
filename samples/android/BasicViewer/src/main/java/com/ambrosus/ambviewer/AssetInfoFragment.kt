package com.ambrosus.ambviewer

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ambrosus.sdk.Asset
import com.ambrosus.sdk.model.AMBAssetInfo
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_asset_info.*
import java.text.SimpleDateFormat
import java.util.Date


class AssetInfoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
        = inflater.inflate(R.layout.fragment_asset_info, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val asset = ARG_ASSET_DATA.get(this)
        //TODO: change to X days ago
        creationDate.text = SimpleDateFormat("D, MMM").format(asset.timestamp)
        createdBy.text = asset.accountAddress
        when(asset) {
            is AMBAssetInfo -> {
                assetName.text = asset.name
                assetID.text = asset.assetId

                val productInformation = asset.attributes["Product Information"]
                detailsDescription.text = when(productInformation) {
                    is JsonObject -> productInformation.asJsonObject.get("Description")?.asString
                    else -> {
                        productInformation?.asString
                    }
                }
            }
            is Asset -> {
                assetName.text = getString(R.string.txtNoName)
                assetID.text = asset.systemId
            }
        }
    }
}
