package com.ambrosus.ambviewer

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.ambrosus.ambviewer.utils.*
import com.ambrosus.sdk.Asset
import com.ambrosus.sdk.Event
import com.ambrosus.sdk.model.Location
import kotlinx.android.synthetic.main.activity_asset.*
import kotlinx.android.synthetic.main.loading_indicator.*
import java.util.*


class AssetActivity : AppCompatActivity() {

    private var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    private var toolbarImage: ImageView? = null

    private lateinit var asset: Asset

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asset)
        asset = intent.extras.getSerializable("asset") as Asset
        //events = asset.events ?: ArrayList()
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        toolbarImage = findViewById(R.id.toolbarImage)

        assetToolbar.setTitleTextColor(resources.getColor(R.color.colorTextPrimary))
        setSupportActionBar(assetToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout!!.title = asset.name ?: asset.systemId

        rvAssetList.layoutManager = LinearLayoutManager(this)


//        {
//            //            IntentsUtil.runEventActivity(this,
////                    it)
//        }

// test data doesn't contain any image reference right now, so leaving it for now
//        if (events.firstOrNull() != null) {
//            val imageUrl = asset.imageUrl
//            if (toolbarImage != null && imageUrl
//                    != null) {
//                GlideApp.with(this).load(imageUrl).placeholder(R.drawable.placeholder_logo).into(toolbarImage!!)
//            }
//        }

        getEventsViewModel().eventsList.observe(
                this,
                Observer {
                    if (it!!.isSuccessful()) {
                        loadingIndicator.visibility = View.INVISIBLE;

                        val dataSetBuilder = RepresentationAdapter.DataSetBuilder()

                        dataSetBuilder.add("Asset", SectionTitleRepresentation.factory)

                        val assetSection = LinkedHashMap<String, Any>()
                        assetSection.put("assetId", asset.systemId)
                        assetSection.put("createdBy", asset.account)
                        assetSection.put("timestamp", asset.timestamp)

                        dataSetBuilder.add(assetSection, SectionRepresentation.factory)
                        dataSetBuilder.add("Events", SectionTitleRepresentation.factory)

                        for (event in it.data) {
                            dataSetBuilder.add(event, ShortEventRepresentation.factory)
                        }

                        rvAssetList.adapter = dataSetBuilder.createAdapter(this)

                    } else {
                        AMBSampleApp.errorHandler.handleError(it.error);
                    }
                }
        )
    }

    private fun getEventsViewModel(): EventsListViewModel {
        return ViewModelProviders.of(
                this,
                EventsListViewModelFactory(asset.systemId, AMBSampleApp.network)
        ).get(EventsListViewModel::class.java)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        getEventsViewModel().refreshEventsList()
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
        val factory = SectionTitleRepresentationFactory()
    }

}

class SectionTitleRepresentationFactory : RepresentationFactory<String>() {

    override fun createRepresentation(inflater: LayoutInflater, parent: ViewGroup): Representation<String> {
        return SectionTitleRepresentation(inflater, parent)
    }
}


class SectionRepresentation(inflater: LayoutInflater, parent: ViewGroup) : Representation<Map<String, Any>>(R.layout.item_section, inflater, parent) {

    private val itemsLayout: LinearLayout

    init {
        itemsLayout = itemView.findViewById(R.id.items)
    }

    override fun display(data: Map<String, Any>?) {
        itemsLayout.removeAllViews();

        val context: Context = itemView.context

        var tempTitleTv: TextView
        var tempSubtitleTv: TextView

        for ((key, value) in data!!) {
            tempTitleTv = TextView(itemView.context)
            tempTitleTv.text = key
            tempTitleTv.setTypeface(null, Typeface.BOLD);
            context.resources?.getColor(R.color.colorTextPrimary)?.let {
                tempTitleTv.setTextColor(it)
            }
            itemsLayout.addView(tempTitleTv)

            tempSubtitleTv = TextView(itemView.context)
//            if (key.toLowerCase() == "timestamp") {
//                tempSubtitleTv.text = (value as Long).toString()
//            } else {
//                tempSubtitleTv.text = value.toString()
//            }

            tempSubtitleTv.text = value.toString()

            context.resources?.getColor(R.color.colorTextSecondary)?.let {
                tempSubtitleTv.setTextColor(it)
            }
            itemsLayout.addView(tempSubtitleTv)
            tempSubtitleTv.setOnClickListener { (it as TextView).text.setClipboard(context, key) }
        }
    }

    companion object {
        val factory = SectionRepresentationFactory()
    }

}

class SectionRepresentationFactory : RepresentationFactory<Map<String, Any>>() {
    override fun createRepresentation(inflater: LayoutInflater, parent: ViewGroup): Representation<Map<String, Any>> {
        return SectionRepresentation(inflater, parent)
    }
}

class ShortEventRepresentation(inflater: LayoutInflater, parent: ViewGroup) : Representation<Event>(R.layout.single_event_view, inflater, parent) {
    override fun display(event: Event?) {
        ViewUtils.setText(itemView, R.id.eventTitle, if (event is com.ambrosus.sdk.model.AMBEvent) event.type else event!!.systemId)
        ViewUtils.setDate(itemView, R.id.eventDate, Date(event!!.gmtTimeStamp))

        //TODO need to understand how to check if event public or private
        var eventLocation : Location? = null
        if(event is com.ambrosus.sdk.model.AMBEvent) eventLocation = event?.location

        ViewUtils.setText(itemView, R.id.eventSubTitle, eventLocation?.name)
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
        val factory = ShortEventRepresentationFactory()
    }
}

class ShortEventRepresentationFactory : RepresentationFactory<Event>() {
    override fun createRepresentation(inflater: LayoutInflater, parent: ViewGroup): Representation<Event> {
        return ShortEventRepresentation(inflater, parent)
    }
}