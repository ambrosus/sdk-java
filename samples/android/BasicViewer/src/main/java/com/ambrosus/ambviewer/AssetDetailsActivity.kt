package com.ambrosus.ambviewer

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ambrosus.ambviewer.utils.BundleArgument
import com.ambrosus.ambviewer.utils.FragmentSwitchHelper
import com.ambrosus.sdk.Asset
import com.ambrosus.sdk.Entity
import com.ambrosus.sdk.model.AMBAssetInfo

class AssetDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_asset_details)

        setSupportActionBar(findViewById(R.id.toolBar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.back_arrow_white)

        FragmentSwitchHelper.addChild(
                this,
                setArguments(intent.extras, AssetDetailsFragment()),
                R.id.contentContainer
        )
    }

    companion object {

        fun startFor(asset: Asset, context: Context) {
            start(asset, context)
        }

        fun startFor(assetInfo: AMBAssetInfo, context: Context) {
            start(assetInfo, context)
        }

        private fun start(data: Entity, context: Context) {
            context.startActivity(
                    Intent(context, AssetDetailsActivity::class.java).putExtras(createArguments(data))
            )
        }

        private fun createArguments(data: Entity): Bundle {
            return ARG_ASSET_DATA.put(Bundle(), data)
        }
    }
}


