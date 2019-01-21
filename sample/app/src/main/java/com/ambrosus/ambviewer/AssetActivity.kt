package com.ambrosus.ambviewer

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.ambrosus.ambviewer.utils.BundleArgument
import com.ambrosus.ambviewer.utils.Representation
import com.ambrosus.ambviewer.utils.RepresentationAdapter
import com.ambrosus.ambviewer.utils.RepresentationFactory
import com.ambrosus.ambviewer.utils.SelfRepresentingItem
import com.ambrosus.ambviewer.utils.ViewUtils
import com.ambrosus.sdk.Asset
import com.ambrosus.sdk.Event
import com.ambrosus.sdk.model.AMBAssetInfo
import com.ambrosus.sdk.model.Location
import kotlinx.android.synthetic.main.activity_asset.*
import java.util.Date

class AssetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_asset)

        rvAssetList.layoutManager = LinearLayoutManager(this)

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
        dataSetBuilder.add("Asset", SectionTitleRepresentation.factory)

        val assetSection = LinkedHashMap<String, Any>()
        assetSection["assetId"] = asset.systemId
        assetSection["createdBy"] = asset.account
        assetSection["timestamp"] = asset.timestamp
        dataSetBuilder.add(assetSection, SectionRepresentation.factory)
    }

    private fun addRepresentationOf(assetInfo: AMBAssetInfo, dataSetBuilder: RepresentationAdapter.DataSetBuilder) {
        //identifiers title
        dataSetBuilder.add("Identifiers", SectionTitleRepresentation.factory)

        //identifiers section
        val identifiersSection = LinkedHashMap<String, Any>()
        for (identifier in assetInfo.identifiers) {
            identifiersSection[identifier.type] = identifier.value
        }
        dataSetBuilder.add(identifiersSection, SectionRepresentation.factory)

        //asset details title
        dataSetBuilder.add("Asset details", SectionTitleRepresentation.factory)

        //asset details section
        val detailsSection = LinkedHashMap<String, Any?>()
        for (attribute in assetInfo.attributes) {
            val key = attribute.key
            val value = attribute.value
            if(!value.isJsonObject) {
                detailsSection[key] = value.asString
            } else {
                detailsSection[key] = null
                val childJson = value.asJsonObject
                for (childJsonKey in childJson.keySet()) {
                    detailsSection[childJsonKey] = childJson.get(childJsonKey).asString
                }
            }
        }
        dataSetBuilder.add(detailsSection, SectionRepresentation.factory)

        //generic asset title
        dataSetBuilder.add("Generic asset", SectionTitleRepresentation.factory)

        //generic asset section
        val assetSection = LinkedHashMap<String, Any>()
        assetSection["assetId"] = assetInfo.assetId
        assetSection["createdBy"] = assetInfo.authorId
        assetSection["timestamp"] = assetInfo.gmtTimeStamp
        dataSetBuilder.add(assetSection, SectionRepresentation.factory)
    }

    private fun addRepresentationOf(eventsLoadResult: LoadResult<List<Event>>?, dataSetBuilder: RepresentationAdapter.DataSetBuilder) {
        dataSetBuilder.add("Events", SectionTitleRepresentation.factory)

        if(eventsLoadResult == null) {
            dataSetBuilder.add(
                    object : SelfRepresentingItem {
                        override fun getLayoutResID(): Int {return R.layout.item_events_loading_indicator}
                        override fun updateView(view: View?) {}
                    }
            )
        } else {
            if(eventsLoadResult.isSuccessful()) {
                for (event in eventsLoadResult.data) {
                    dataSetBuilder.add(event, ShortEventRepresentation.factory)
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

    private fun displayData(eventsLoadResult: LoadResult<List<Event>>?) {

        val dataSetBuilder = RepresentationAdapter.DataSetBuilder()
        val assetData = getAssetData()

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
                getEventsListViewModel().eventsList.observe(
                        this,
                        Observer {
                            displayData(it)
                        }
                )
        }
    }

    private fun getEventsListViewModel(): EventsListViewModel {
        val assetData = getAssetData()
        val viewModelFactory: ViewModelProvider.Factory = when (assetData) {
            is Asset -> GenericEventsListViewModelFactory(assetData.systemId, AMBSampleApp.network)
            is AMBAssetInfo -> AMBEventsListViewModelFactory(assetData.assetId, AMBSampleApp.network)
            else -> throw IllegalArgumentException("This activity can display only Asset or AssetInfo but got ${assetData.javaClass.name}")
        }
        return ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(EventsListViewModel::class.java)
    }

    private fun getAssetData() = ARG_ASSET_DATA.get(intent.extras)

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        getEventsListViewModel().refreshEventsList()
    }

    companion object {

        private val ARG_ASSET_DATA = BundleArgument<Any>("KEY_ASSET_DATA", Any::class.java)


        fun startFor(asset: Asset, context: Context) {
            start(asset, context)
        }

        fun startFor(assetInfo: AMBAssetInfo, context: Context) {
            start(assetInfo, context)
        }

        private fun start(data: Any, context: Context) {
            context.startActivity(
                    Intent(context, AssetActivity::class.java).putExtras(createArguments(data))
            )
        }

        private fun createArguments(data: Any): Bundle {
            return ARG_ASSET_DATA.put(Bundle(), data)
        }

    }
}

fun CharSequence.setClipboard(context: Context, label: String) {
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
        clipboard.text = this
    } else {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Copied Text", this)
        clipboard.primaryClip = clip
    }
    Toast.makeText(context, "Copied $label!", Toast
            .LENGTH_LONG).show()
}

class SectionTitleRepresentation(inflater: LayoutInflater, parent: ViewGroup) : Representation<String>(R.layout.item_section_title, inflater, parent) {

    private val title: TextView

    init {
        title = itemView as TextView
    }

    override fun display(data: String?) {
        title.setText(data!!)
    }

    companion object {
        val factory = object : RepresentationFactory<String>() {
            override fun createRepresentation(inflater: LayoutInflater, parent: ViewGroup): Representation<String> {
                return SectionTitleRepresentation(inflater, parent)
            }
        }
    }

}

class SectionRepresentation(private val inflater: LayoutInflater, parent: ViewGroup) : Representation<Map<String, Any?>>(R.layout.item_section, inflater, parent) {

    private val itemsLayout: LinearLayout

    init {
        itemsLayout = itemView.findViewById(R.id.items)
    }

    override fun display(data: Map<String, Any?>?) {
        itemsLayout.removeAllViews();

        for ((key, value) in data!!) {
            val layoutId = when(value) {
                null -> R.layout.text_view_section_header
                else -> R.layout.text_view_section_key
            }
            val tempTitleTv =  inflater.inflate(layoutId, itemsLayout, false) as TextView
            itemsLayout.addView(tempTitleTv)

            tempTitleTv.text = key.capitalize()

            if(value != null) {
                val tempSubtitleTv = inflater.inflate(R.layout.text_view_section_value, itemsLayout, false) as TextView
                itemsLayout.addView(tempSubtitleTv)

                //            if (key.toLowerCase() == "timestamp") {
                //                tempSubtitleTv.text = (value as Long).toString()
                //            } else {
                //                tempSubtitleTv.text = value.toString()
                //            }

                tempSubtitleTv.text = value.toString()

                tempSubtitleTv.setOnClickListener { (it as TextView).text.setClipboard(itemView.context, key) }
            }
        }
    }

    companion object {
        val factory = object : RepresentationFactory<Map<String, Any?>>() {
            override fun createRepresentation(inflater: LayoutInflater, parent: ViewGroup): Representation<Map<String, Any?>> {
                return SectionRepresentation(inflater, parent)
            }
        }
    }

}

class ShortEventRepresentation(inflater: LayoutInflater, parent: ViewGroup) : Representation<Event>(R.layout.item_event, inflater, parent) {
    override fun display(event: Event?) {
        ViewUtils.setText(itemView, R.id.eventTitle, if (event is com.ambrosus.sdk.model.AMBEvent) event.name ?: event.type else event!!.systemId)
        ViewUtils.setDate(itemView, R.id.eventDate, Date(event!!.gmtTimeStamp))

        //TODO need to understand how to check if event public or private
        var eventLocation : Location? = null
        if(event is com.ambrosus.sdk.model.AMBEvent) eventLocation = event?.location

        var locationSting = StringBuffer()

        eventLocation?.city?.let {
            locationSting.append(it).append(", ")
        }

        eventLocation?.country?.let {
            locationSting.append(it)
        }

        if(locationSting.endsWith(", "))
            locationSting.removeSuffix(", ")

        if(locationSting.isEmpty())
            eventLocation?.name?.let { locationSting.append(it) }

        ViewUtils.setText(
                itemView,
                R.id.eventSubTitle,
                locationSting.toString()
        )
        ViewUtils.setText(itemView, R.id.eventVisibility, generatePrivacy())

//        tempCard.tag = event
//            tempCard.setOnClickListener {
//                eventClickListener(it.tag as com.ambrosus.sdk.model.AMBEvent)
//            }
//        }
    }

    private fun generatePrivacy(): String {
        val random = (0..2).shuffled().last()

        return when (random) {
            0 -> "Public"
            else -> "Private"
        }
    }


    companion object {
        val factory = object : RepresentationFactory<Event>() {
            override fun createRepresentation(inflater: LayoutInflater, parent: ViewGroup): Representation<Event> {
                return ShortEventRepresentation(inflater, parent)
            }
        }
    }
}