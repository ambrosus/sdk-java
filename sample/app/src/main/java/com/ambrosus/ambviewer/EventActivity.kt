/*
 * Copyright: Ambrosus Technologies GmbH
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
import kotlinx.android.synthetic.main.activity_event.*
import java.util.Date

class EventActivity : AppCompatActivity() {

    val mapRepresentationFactory = com.ambrosus.ambviewer.MapRepresentationFactory(this)

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

            val location = event.location
            if(location != null) {
                dataSetBuilder.add("Location", SectionTitleRepresentation.factory)
                dataSetBuilder.add(location, mapRepresentationFactory)

                dataSetBuilder.add(
                        mapOf("name" to location.name, "city" to location.city, "country" to location.country).filterValues {it != null},
                        SectionRepresentation.factory
                )
            }

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
