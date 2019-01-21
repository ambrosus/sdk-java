package com.ambrosus.ambviewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.ambrosus.ambviewer.utils.BundleArgument
import com.ambrosus.ambviewer.utils.DateAdapter
import com.ambrosus.ambviewer.utils.RepresentationAdapter
import com.ambrosus.sdk.Event
import com.ambrosus.sdk.model.AMBEvent
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.activity_asset.*
import kotlinx.android.synthetic.main.activity_event.*
import java.util.Date

class EventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        setSupportActionBar(eventAssetToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        eventDetails.layoutManager = LinearLayoutManager(this)

        displayEventData(ARG_EVENT.get(intent.extras))
    }

    private fun displayEventData(event: Event){
        val dataSetBuilder = RepresentationAdapter.DataSetBuilder()

        //event title
        dataSetBuilder.add("Event", SectionTitleRepresentation.factory)

        //generic event section
        val eventSection = LinkedHashMap<String, Any>()
        dataSetBuilder.add(eventSection, SectionRepresentation.factory)

        eventSection["assetId"] = event.assetId
        eventSection["createdBy"] = event.authorId
        eventSection["timestamp"] = DateAdapter.dateToText(Date(event.gmtTimeStamp))

        if(event is AMBEvent) {
            eventSection["type"] = event.type

            addSection(dataSetBuilder, "Attributes", event.attributes)
            addSection(dataSetBuilder, "Documents", event.documents)
            addSection(dataSetBuilder, "Images", event.images)

            title = event.name ?: event.type ?: event.systemId
        } else title = event.systemId

        eventDetails.adapter = dataSetBuilder.createAdapter(this)
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {

        private val ARG_EVENT = BundleArgument<Event>("KEY_EVENT_ARG", Event::class.java)

        fun startFor(event: Event, context: Context) {
            context.startActivity(
                    Intent(context, EventActivity::class.java).putExtras(
                            ARG_EVENT.put(Bundle(), event)
                    )
            )
        }
    }
}
