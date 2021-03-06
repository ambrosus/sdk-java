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

package com.ambrosus.demoapp

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.ambrosus.demoapp.utils.BundleArgument
import com.ambrosus.demoapp.utils.RepresentationAdapter
import com.ambrosus.demoapp.utils.SelfRepresentingItem
import com.ambrosus.sdk.Asset
import com.ambrosus.sdk.Entity
import com.ambrosus.sdk.Event
import com.ambrosus.sdk.EventQueryBuilder
import com.ambrosus.sdk.SearchResult
import com.ambrosus.sdk.model.AMBAssetInfo
import com.ambrosus.sdk.model.AMBEventQueryBuilder
import kotlinx.android.synthetic.main.activity_asset.*

class AssetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_asset)

        rvAssetList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        assetToolbar.setTitleTextColor(resources.getColor(R.color.colorTextPrimary))
        setSupportActionBar(assetToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        displayData(null);

//        {
//            //            IntentsUtil.runEventActivity(this,
////                    it)
//        }
    }

    private fun addRepresentationOf(asset: Asset, dataSetBuilder: RepresentationAdapter.DataSetBuilder) {
        //asset title
        dataSetBuilder.add("Asset", SectionTitleRepresentation.factory)

        val assetSection = LinkedHashMap<String, Any>()
        assetSection["assetId"] = asset.systemId
        assetSection["createdBy"] = asset.accountAddress
        assetSection["timestamp"] = asset.timestamp
        dataSetBuilder.add(assetSection, SectionRepresentation.factory)
    }

    private fun addRepresentationOf(assetInfo: AMBAssetInfo, dataSetBuilder: RepresentationAdapter.DataSetBuilder) {
        if(!assetInfo.identifiers.isEmpty()) {
            //identifiers title
            dataSetBuilder.add("Identifiers", SectionTitleRepresentation.factory)

            //identifiers section
            val identifiersSection = LinkedHashMap<String, Any>()
            for (identifier in assetInfo.identifiers) {
                identifiersSection[identifier.type] = identifier.value
            }
            dataSetBuilder.add(identifiersSection, SectionRepresentation.factory)
        }

        addSection(dataSetBuilder, "Asset details", assetInfo.attributes)

        //generic asset title
        dataSetBuilder.add("Asset", SectionTitleRepresentation.factory)

        //generic asset section
        val assetSection = LinkedHashMap<String, Any>()
        assetSection["assetId"] = assetInfo.assetId
        assetSection["createdBy"] = assetInfo.accountAddress
        assetSection["timestamp"] = assetInfo.timestamp
        dataSetBuilder.add(assetSection, SectionRepresentation.factory)
    }

    private fun addRepresentationOf(eventsLoadResult: LoadResult<SearchResult<out Entity>>?, dataSetBuilder: RepresentationAdapter.DataSetBuilder) {
        dataSetBuilder.add("Events", SectionTitleRepresentation.factory)

        if(eventsLoadResult == null) {
            dataSetBuilder.add(
                    object : SelfRepresentingItem {
                        override fun getLayoutResID(): Int {return R.layout.item_loading_indicator}
                        override fun updateView(view: View?) {}
                    }
            )
        } else {
            if(eventsLoadResult.isSuccessful()) {
                for (entity in eventsLoadResult.data.items) {
                    dataSetBuilder.add(entity as Event, ShortEventRepresentation.factory)
                }
            } else
                dataSetBuilder.add(
                        object : SelfRepresentingItem {
                            override fun getLayoutResID(): Int {
                               return R.layout.item_events_loading_error
                            }

                            override fun updateView(view: View?) {
                                (view as TextView).setText("Wasn't able to load events due to: ${eventsLoadResult.error}")
                            }

                        }
                )
        }
    }

    private fun displayData(eventsLoadResult: LoadResult<SearchResult<out Entity>>?) {

        val dataSetBuilder = RepresentationAdapter.DataSetBuilder()
        val assetData = getAsset()

        when(assetData) {
            is Asset -> {
                collapsing_toolbar.title = assetData.systemId
                addRepresentationOf(assetData, dataSetBuilder)
            }
            is AMBAssetInfo -> {

                collapsing_toolbar.title = assetData.name ?: assetData.systemId

                if(!assetData.images.isEmpty()) {
                    GlideApp.with(this)
                            //TODO add Image type to SDK
                            .load(assetData.images.entries.iterator().next().value.get("url").asString)
                            .placeholder(R.drawable.placeholder_logo)
                            .into(toolbarImage)
                }

                addRepresentationOf(assetData, dataSetBuilder)
            }
            else -> throw IllegalArgumentException("This activity can display only Asset or AssetInfo but got ${assetData.javaClass.name}")
        }

        addRepresentationOf(eventsLoadResult, dataSetBuilder);

        rvAssetList.adapter = dataSetBuilder.createAdapter(this)

        if(eventsLoadResult == null) {
            getViewModel().getResults().observe(
                    this,
                    Observer {
                        displayData(it)
                    }
            )
        }
    }

    private fun getViewModel(): SingleSearchResultViewModel {
        val assetData = getAsset()
        val queryBuilder = when (assetData) {
            is Asset -> EventQueryBuilder().forAsset(assetData.systemId)
            is AMBAssetInfo -> AMBEventQueryBuilder().forAsset(assetData.assetId)
            else -> throw IllegalArgumentException("This activity can display only Asset or AssetInfo but got ${assetData.javaClass.name}")
        }
        val factory = SingleSearchResultViewModel.Factory(AMBSampleApp.network, queryBuilder.build())

        return ViewModelProviders.of(
                this,
                factory
        ).get(SingleSearchResultViewModel::class.java)
    }

    private fun getAsset() = ARG_ASSET_DATA.get(intent.extras)

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {

        private val ARG_ASSET_DATA = BundleArgument<Entity>("KEY_ASSET_DATA", Entity::class.java)


        fun startFor(asset: Asset, context: Context) {
            start(asset, context)
        }

        fun startFor(assetInfo: AMBAssetInfo, context: Context) {
            start(assetInfo, context)
        }

        private fun start(data: Entity, context: Context) {
            context.startActivity(
                    Intent(context, AssetActivity::class.java).putExtras(createArguments(data))
            )
        }

        private fun createArguments(data: Entity): Bundle {
            return ARG_ASSET_DATA.put(Bundle(), data)
        }

    }
}

